package me.nabil.laravel.queue.producer;

import me.nabil.laravel.queue.common.Queue;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * 消息的生产实现
 *
 * @author nabilzhang
 */
public class QueueProducerSimpleImpl<T> implements QueueProducer<T> {

    /**
     * 操作的消息队列的名称
     */
    private String queueName;

    /**
     * 任务名称
     */
    private String jobName;

    /**
     * 构造函数
     *
     * @param queueName 需要推送的消息队列
     */
    public QueueProducerSimpleImpl(String queueName, String jobName) {
        this.queueName = queueName;
        this.jobName = jobName;
    }

    /**
     * 要push的消息队列
     */
    private Queue queue;

    @Override
    public void push(T data) {
        queue.push(this.jobName, data, this.queueName);
    }

    @Override
    public void later(T data, TimeUnit timeUnit, long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, (int) timeUnit.toSeconds(time));

        queue.later(this.jobName, calendar.getTimeInMillis() / 1000, data, this.queueName);
    }

    /**
     * 设置相应队列
     *
     * @param queue
     */
    public void setQueue(Queue queue) {
        this.queue = queue;
    }
}
