package me.nabil.pa.queue.sample.consume;

import me.nabil.pa.queue.common.annotation.ConsumerOptions;
import me.nabil.pa.queue.common.job.Job;
import me.nabil.pa.queue.consumer.QueueConsumerHandler;
import me.nabil.pa.queue.sample.DemoData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * demo queue 任务处理器
 *
 * @author nabilzhang
 */
@Service
@ConsumerOptions(queueName = "demoqueue", idleSleepSeconds = 5)
public class DemoQueueConsumerHandler implements QueueConsumerHandler<DemoData> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DemoQueueConsumerHandler.class);

    @Override
    public boolean fire(Job job, DemoData data) {
        LOGGER.info("handle job {}", job.getJobMessage());

        // job.release(TimeUnit.SECONDS, 30);
        return true;
    }
}
