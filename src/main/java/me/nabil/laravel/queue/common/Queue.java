package me.nabil.laravel.queue.common;

import me.nabil.laravel.queue.common.job.Job;

/**
 * Queue
 *
 * @author nabilzhang
 */
public interface Queue {
    /**
     * push消息到队列中
     *
     * @param jobName   任务名称
     * @param data      数据
     * @param queueName 消息队列
     * @return
     */
    <T> Long push(String jobName, T data, String queueName);


    /**
     * push消息到延迟队列
     *
     * @param jobName   jobName
     * @param delay     延迟
     * @param data      数据
     * @param queueName 消息队列名称
     * @return 是否成功
     */
    <T> Boolean later(String jobName, long delay, T data, String queueName);

    /**
     * 从队列总弹出一个元素
     *
     * @param queueName         队列名称
     * @param retryAfterSeconds 多久之后重试
     * @return job
     */
    <T> Job<T> pop(String queueName, int retryAfterSeconds);

    /**
     * 删除运行中的数据
     *
     * @param queueName 队列名称
     * @param job       job
     */
    <T> Long deleteReserved(String queueName, Job<T> job);

    /**
     * 删除运行中(reserved)队列中的数据并且release到延迟队列
     *
     * @param queueName 队列名称
     * @param job       job
     * @param delay     延迟时间
     */
    <T> boolean deleteAndRelease(String queueName, Job<T> job, long delay);
}
