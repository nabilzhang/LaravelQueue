package me.nabil.laravel.queue.sample.produce;

import me.nabil.laravel.queue.producer.QueueProducer;
import me.nabil.laravel.queue.sample.DemoData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import java.util.concurrent.TimeUnit;


/**
 * 生产的Demo
 *
 * @author nabilzhang
 */
@SpringBootApplication
@Import(ProducerBeanConfiguration.class)
public class ProduceDemoMain implements CommandLineRunner {

    @Autowired
    private QueueProducer<DemoData> demoDataQueueProducer;

    public static void main(String[] args) {
        SpringApplication.run(ProduceDemoMain.class, args).close();
    }

    @Override
    public void run(String... strings) throws Exception {
        DemoData demoData = new DemoData();
        demoData.setId(8490248);
        demoDataQueueProducer.push(demoData);
        demoDataQueueProducer.later(demoData, TimeUnit.HOURS, 1);
        demoDataQueueProducer.later(demoData, TimeUnit.SECONDS, 1);
    }
}
