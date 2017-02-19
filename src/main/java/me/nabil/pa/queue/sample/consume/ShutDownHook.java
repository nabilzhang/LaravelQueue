package me.nabil.pa.queue.sample.consume;

import me.nabil.pa.queue.consumer.ConsumerWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 关闭订阅程序的钩子
 *
 * @author nabilzhang
 */
public class ShutDownHook extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShutDownHook.class);

    private List<ConsumerWorker> consumerWorkers;

    private CountDownLatch workersCountDownLatch;

    private CountDownLatch shutDownCountDownLatch;

    public ShutDownHook(List<ConsumerWorker> consumerWorkers,
                        CountDownLatch workersCountDownLatch,
                        CountDownLatch shutDownCountDownLatch) {
        if (consumerWorkers == null || consumerWorkers.isEmpty()) {
            throw new RuntimeException("param queueConsumerHandlers should not be null or empty");
        }
        this.consumerWorkers = consumerWorkers;
        this.setName("ShutDownHook");
        this.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                LOGGER.info(e.getMessage(), e);
            }
        });

        this.shutDownCountDownLatch = shutDownCountDownLatch;
        this.workersCountDownLatch = workersCountDownLatch;
    }

    @Override
    public void run() {
        LOGGER.info("shutdown hook start, stop consumer workers");

        // 逐一发出停止命令
        for (ConsumerWorker consumerWorker : this.consumerWorkers) {
            consumerWorker.shutdown();
        }

        // 等待所有worker将处理中的任务完成
        try {
            this.workersCountDownLatch.await();
        } catch (InterruptedException e) {
            LOGGER.warn(e.getMessage(), e);
        }

        LOGGER.info("All workers stopped");

        // 所有运行中work都已经结束，触发主线程结束
        shutDownCountDownLatch.countDown();

    }
}
