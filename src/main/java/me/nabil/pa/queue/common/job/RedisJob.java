package me.nabil.pa.queue.common.job;

import me.nabil.pa.queue.common.RedisQueue;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * redis job
 *
 * @author nabilzhang
 */
public class RedisJob<T> extends AbstractJob<T> {

    private RedisQueue<T> redisQueue;

    private JobMessage<T> jobMessage;

    private String reserved;

    private String queueName;

    @Override
    public JobMessage<T> getJobMessage() {
        return this.jobMessage;
    }

    @Override
    public String getJobReserved() {
        return this.reserved;
    }

    @Override
    public int attempts() {
        return this.jobMessage.getAttempts();
    }

    @Override
    public void delete() {
        this.setDeleted(this.redisQueue.deleteReserved(this.queueName, this) > 0);
    }

    @Override
    public void release(TimeUnit timeUnit, long delayTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, (int) timeUnit.toSeconds(delayTime));
        this.setReleased(this.redisQueue.deleteAndRelease(this.queueName, this,
                calendar.getTimeInMillis() / 1000));
    }

    /**
     * 构造方法
     *
     * @param redisQueue 操作类
     * @param jobMessage 原job信息
     * @param reserved   运行job信息
     * @param queueName  队列名称
     */
    public RedisJob(RedisQueue<T> redisQueue, JobMessage<T> jobMessage, String reserved, String queueName) {
        this.redisQueue = redisQueue;
        this.jobMessage = jobMessage;
        this.reserved = reserved;
        this.queueName = queueName;
    }
}
