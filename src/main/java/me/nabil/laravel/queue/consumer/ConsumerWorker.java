package me.nabil.laravel.queue.consumer;

import me.nabil.laravel.queue.common.annotation.ConsumerOptions;
import me.nabil.laravel.queue.common.Queue;
import me.nabil.laravel.queue.common.job.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.concurrent.CountDownLatch;

/**
 * 消费工作线程
 *
 * @author nabilzhang
 */
public class ConsumerWorker<T> extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerWorker.class);

    private QueueConsumerHandler queueConsumerHandler;

    /**
     * 消费队列的选项
     */
    private ConsumerOptions consumerOptions;

    private Queue queue;

    private boolean run = true;

    private CountDownLatch countDownLatch;

    public ConsumerWorker(QueueConsumerHandler queueConsumerHandler, CountDownLatch countDownLatch) {
        this.queueConsumerHandler = queueConsumerHandler;
        this.consumerOptions = queueConsumerHandler.getClass().getAnnotation(ConsumerOptions.class);
        Assert.hasText(consumerOptions.queueName(), "queueName of consumerOptions must be not blank");

        this.countDownLatch = countDownLatch;

        this.setName("w:" + this.consumerOptions.queueName());
        this.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                LOGGER.info(e.getMessage(), e);
            }
        });
    }

    @Override
    public void run() {
        LOGGER.info("consume worker for {} started", consumerOptions.queueName());
        while (true) {

            // 获取下一个需要处理的job
            Job job = this.getNextJob();

            // 如果没有则继续循环等待
            if (job != null) {
                this.runJob(job);
            } else {
                try {
                    LOGGER.debug("The queue is empty, sleep {} second", consumerOptions.idleSleepSeconds());
                    Thread.sleep(consumerOptions.idleSleepSeconds() * 1000);
                } catch (InterruptedException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }

            // 是否可以停止线程
            if (!this.run) {
                LOGGER.info("stopping worker {}", this);
                countDownLatch.countDown();
                break;
            }
        }
        LOGGER.info("consume worker for [{}] stopped", consumerOptions.queueName());
    }

    /**
     * 运行job
     *
     * @param job
     */
    private void runJob(Job<T> job) {
        try {
            this.process(job);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    /**
     * 处理job
     *
     * @param job
     */
    private void process(Job<T> job) {
        // 如果已经超过重试次数了，就打印一条日志
        if (job.attempts() > consumerOptions.maxRetrys()) {
            LOGGER.warn("Job {} has retry {} times, attempts over max retry times",
                    job.getJobMessage(), job.attempts());
        }

        boolean result = queueConsumerHandler.fire(job, job.getJobMessage().getData());
        LOGGER.debug("job fire success, {}", job.getJobMessage());

        if (result) {
            // 如果运行成功，就删除掉
            if (job.isDeleted() || job.isReleased()) {
                LOGGER.debug("Job {} is deleted or released, ignore delete", job.getJobReserved());
            } else {
                Long ret = this.queue.deleteReserved(consumerOptions.queueName(), job);
                if (ret <= 0L) {
                    throw new RuntimeException("delete job failed, job:" + job.getJobReserved());
                }
            }

        }
    }

    /**
     * 获取下一个
     *
     * @return
     */
    private Job getNextJob() {
        return this.queue.pop(this.consumerOptions.queueName(), consumerOptions.retryAfterSeconds());
    }

    /**
     * 停止worker
     */
    public void shutdown() {
        this.run = false;
    }

    public void setQueue(Queue queue) {
        this.queue = queue;
    }
}
