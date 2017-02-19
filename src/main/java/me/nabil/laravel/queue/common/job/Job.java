package me.nabil.laravel.queue.common.job;

import java.util.concurrent.TimeUnit;

/**
 * 任务
 *
 * @author nabilzhang
 */
public interface Job<T> {

    /**
     * 获取job的body对象
     *
     * @return
     */
    JobMessage<T> getJobMessage();

    /**
     * 获取运行中的队列数据
     *
     * @return
     */
    String getJobReserved();

    /**
     * 已经重试的次数
     *
     * @return
     */
    int attempts();

    /**
     * 删除该任务
     *
     * @return
     */
    void delete();

    /**
     * 释放该任务
     *
     * @param timeUnit
     * @param delayTime
     * @return
     */
    void release(TimeUnit timeUnit, long delayTime);

    /**
     * 是否已经释放
     *
     * @return
     */
    boolean isReleased();

    /**
     * 是否已经删除
     *
     * @return
     */
    boolean isDeleted();


}
