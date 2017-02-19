package me.nabil.pa.queue.common;

import me.nabil.pa.queue.common.job.Job;
import me.nabil.pa.queue.common.job.JobMessage;
import me.nabil.pa.queue.common.job.RedisJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.*;

import javax.annotation.Resource;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Queue的Redis实现
 *
 * @author nabilzhang
 */
public class RedisQueue<T> extends AbstractQueue implements Queue<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisQueue.class);

    private static final String QUEUE_PREFIX = "queues:";
    /**
     * 延迟队列对应的后缀
     */
    private static final String DELAYED_SUFFIX = ":delayed";

    /**
     * 运行中队列对应的后缀
     */
    private static final String RESERVED_SUFFIX = ":reserved";

    @Resource(name = "redisTemplate")
    private RedisTemplate<String, JobMessage<T>> redisTemplate;

    @Override
    public Long push(String jobName, T data, String queueName) {
        return this.pushRaw(getRawQueueName(queueName), createPayload(jobName, data));
    }

    @Override
    public Boolean later(String jobName, long delay, T data, String queueName) {
        return this.laterRaw(getRawQueueName(queueName) + DELAYED_SUFFIX, delay, createPayload(jobName, data));
    }

    @Override
    public Job<T> pop(String queueName, int retryAfterSeconds) {

        this.migrate(queueName);

        // 这里pop出来的是已经放入reserved的消息数据
        String reserved = retrieveNextJob(getRawQueueName(queueName), retryAfterSeconds);

        //
        if (reserved != null && !reserved.isEmpty()) {
            JobMessage jobMessage = new GenericJackson2JsonRedisSerializer()
                    .deserialize(reserved.getBytes(Charset.forName("UTF8")), JobMessage.class);
            // 原jobMessage attempts应该少1
            jobMessage.setAttempts(jobMessage.getAttempts() - 1);
            return new RedisJob<T>(this, jobMessage, reserved, queueName);
        } else {
            return null;
        }
    }

    @Override
    public boolean deleteAndRelease(String queueName, Job<T> job, long delay) {
        String rawQueueName = this.getRawQueueName(queueName);

        List<String> keys = new ArrayList<String>();
        keys.add(rawQueueName + DELAYED_SUFFIX);
        keys.add(rawQueueName + RESERVED_SUFFIX);
        return this.redisTemplate.execute(RedisLuaScript.releaseScript(),
                new GenericToStringSerializer<Object>(Object.class),
                new GenericToStringSerializer<Boolean>(Boolean.TYPE),
                keys, job.getJobReserved(), delay);
    }

    @Override
    public Long deleteReserved(String queueName, Job<T> job) {
        return this.redisTemplate.execute(
                new DefaultRedisScript<Long>("return redis.call('zrem', KEYS[1], ARGV[1])", Long.TYPE),
                new StringRedisSerializer(), new GenericToStringSerializer<Long>(Long.TYPE),
                Collections.singletonList(getRawQueueName(queueName) + RESERVED_SUFFIX), job.getJobReserved());
    }

    /**
     * 获取下一个job
     *
     * @param rawQueueName      加了前缀之后的队列名称
     * @param retryAfterSeconds 多少秒之后重试
     * @return
     */
    private String retrieveNextJob(String rawQueueName, int retryAfterSeconds) {
        List<String> keys = new ArrayList<String>();
        keys.add(rawQueueName);
        keys.add(rawQueueName + RESERVED_SUFFIX);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, retryAfterSeconds);

        try {
            return this.redisTemplate.execute(RedisLuaScript.popScript(),
                    new GenericToStringSerializer(Long.TYPE), new StringRedisSerializer(),
                    keys, calendar.getTimeInMillis() / 1000);
        } catch (Exception e) {
            LOGGER.info(e.getMessage(), e);
            return null;
        }

    }

    /**
     * 将延迟或者超时的相关job，汇总到待运行队列
     *
     * @param queueName
     */
    private void migrate(String queueName) {
        this.migrateRawExpiredJobs(queueName + DELAYED_SUFFIX, queueName);
        this.migrateRawExpiredJobs(queueName + RESERVED_SUFFIX, queueName);
    }

    /**
     * 如果fromQueue中有就绪后者过期的元素，直接加到目标队列后面
     *
     * @param fromQueue
     * @param toQueue
     */
    private void migrateRawExpiredJobs(String fromQueue, String toQueue) {
        List<String> keys = new ArrayList<String>();
        keys.add(getRawQueueName(fromQueue));
        keys.add(getRawQueueName(toQueue));

        // 按照当前的时间看是否过期，如果过期就合并到主队列等待运行
        redisTemplate.execute(RedisLuaScript.migrateExpiredJobsScript(),
                keys, System.currentTimeMillis() / 1000);

    }

    /**
     * 返回实际的队列
     *
     * @param queueName
     * @return
     */
    private String getRawQueueName(String queueName) {
        return QUEUE_PREFIX + queueName;
    }

    /**
     * 实际向对应的key中push
     *
     * @param queueName queue名称
     * @param payload   消息体
     * @return
     */
    private Long pushRaw(String queueName, JobMessage<T> payload) {
        return redisTemplate.opsForList().rightPush(queueName, payload);
    }

    /**
     * 实际向对应的zset中添加
     *
     * @param queueName queue名称
     * @param payload   消息体
     * @return
     */
    private Boolean laterRaw(String queueName, long delay, JobMessage<T> payload) {
        return redisTemplate.opsForZSet().add(queueName, payload, delay);
    }
}
