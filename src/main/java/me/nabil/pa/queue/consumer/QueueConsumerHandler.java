package me.nabil.pa.queue.consumer;

import me.nabil.pa.queue.common.job.Job;

/**
 * 消费处理
 *
 * @author nabilzhang
 */
public interface QueueConsumerHandler<T> {

    /**
     * 处理一个job
     *
     * @param job  任务对象
     * @param data 任务数据
     * @return
     */
    boolean fire(Job job, T data);
}
