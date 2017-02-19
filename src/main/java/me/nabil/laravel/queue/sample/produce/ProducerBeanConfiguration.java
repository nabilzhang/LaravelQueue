package me.nabil.laravel.queue.sample.produce;

import me.nabil.laravel.queue.common.Queue;
import me.nabil.laravel.queue.common.RedisQueue;
import me.nabil.laravel.queue.producer.QueueProducer;
import me.nabil.laravel.queue.producer.QueueProducerSimpleImpl;
import me.nabil.laravel.queue.sample.DemoData;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * producer的配置
 *
 * @author nabilzhang
 */
@Configuration
public class ProducerBeanConfiguration {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(factory);
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setKeySerializer(new StringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public Queue redisQueue() {
        Queue queue = new RedisQueue();
        return queue;
    }

    /**
     * DemoQueue 生产
     *
     * @param queue
     * @return
     */
    @Bean
    public QueueProducer<DemoData> demoDataQueueProducer(@Qualifier("redisQueue") Queue<DemoData> queue) {
        QueueProducerSimpleImpl<DemoData> demoDataQueueProducer =
                new QueueProducerSimpleImpl<DemoData>("demoqueue", "jobA");
        demoDataQueueProducer.setQueue(queue);
        return demoDataQueueProducer;
    }

}
