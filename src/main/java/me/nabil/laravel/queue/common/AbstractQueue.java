package me.nabil.laravel.queue.common;

import me.nabil.laravel.queue.common.job.JobMessage;

import java.util.UUID;

/**
 * 任务队列公用方法
 *
 * @author nabilzhang
 */
public abstract class AbstractQueue<T> {

    /**
     * 构建redis实际存储的任务消息体
     *
     * @return
     */
    protected JobMessage<T> createPayload(String jobName, T data) {
        JobMessage<T> jobMessage = new JobMessage<T>();
        jobMessage.setJob(jobName);
        jobMessage.setData(data);
        jobMessage.setAttempts(0);
        jobMessage.setId(UUID.randomUUID().toString());
        return jobMessage;
    }


}
