package me.nabil.pa.queue.producer;

import java.util.concurrent.TimeUnit;

/**
 * 消息生产者
 *
 * @author nabilzhang
 */
public interface QueueProducer<T> {

    /**
     * push数据到队列
     *
     * @param data 数据
     */
    void push(T data);

    /**
     * push任务数据，延迟执行
     *
     * @param data      数据
     * @param timeUnit  延迟时间单位
     * @param delayTime 延迟
     */
    void later(T data, TimeUnit timeUnit, long delayTime);
}
