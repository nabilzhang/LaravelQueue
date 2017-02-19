package me.nabil.pa.queue.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 消费Queue的配置相
 *
 * @author nabilzhang
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConsumerOptions {
    /**
     * 消费监听的Queue的名称
     *
     * @return
     */
    String queueName();

    /**
     * 空闲时等待的秒数
     *
     * @return
     */
    int idleSleepSeconds() default 0;

    /**
     * 多久之后重试，默认60秒
     *
     * @return
     */
    int retryAfterSeconds() default 60;

    /**
     * 重试次数
     *
     * @return
     */
    int maxRetrys() default 0;
}
