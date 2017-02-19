package me.nabil.laravel.queue.sample.consume;

import me.nabil.laravel.queue.common.Queue;
import me.nabil.laravel.queue.consumer.ConsumerWorker;
import me.nabil.laravel.queue.consumer.QueueConsumerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * 消费DemoMain
 *
 * @author nabilzhang
 */
@SpringBootApplication
@Import(ConsumerBeanConfiguration.class)
@ComponentScan("me.nabil.pa.queue.sample")
public class ConsumerDemoMain implements ApplicationRunner, ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerDemoMain.class);

    private ApplicationContext applicationContext;

    private Map<String, QueueConsumerHandler> queueConsumerHandlerMap;

    @Autowired
    private Queue queue;

    public static void main(String[] args) {
        SpringApplication.run(ConsumerDemoMain.class, args).close();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        this.queueConsumerHandlerMap = applicationContext.getBeansOfType(QueueConsumerHandler.class);

        if (this.queueConsumerHandlerMap == null || this.queueConsumerHandlerMap.size() == 0) {
            LOGGER.info("No QueueConsumerHandlers ,return");
            return;
        }

        int handlerSize = queueConsumerHandlerMap.size();

        // 启动所有的consumer worker
        CountDownLatch workersCountDownLatch = new CountDownLatch(handlerSize);
        List<ConsumerWorker> consumerWorkerList = new ArrayList<ConsumerWorker>(queueConsumerHandlerMap.size());
        for (Map.Entry<String, QueueConsumerHandler> entry : queueConsumerHandlerMap.entrySet()) {
            LOGGER.info("Initializing consumer worker for {}", entry.getKey());
            ConsumerWorker consumerWorker = new ConsumerWorker(entry.getValue(), workersCountDownLatch);
            consumerWorker.setQueue(queue);
            consumerWorker.start();
            consumerWorkerList.add(consumerWorker);
        }

        CountDownLatch shutDownLatch = new CountDownLatch(1);

        // 添加关闭程序的钩子
        Runtime.getRuntime().addShutdownHook(new ShutDownHook(consumerWorkerList,
                workersCountDownLatch, shutDownLatch));

        // 等待所有的worker执行完成
        shutDownLatch.await();
        LOGGER.info("Consumer Stop successful");
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
