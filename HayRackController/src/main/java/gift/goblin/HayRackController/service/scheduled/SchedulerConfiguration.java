/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gift.goblin.HayRackController.service.scheduled;

import java.io.IOException;
import java.util.Date;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import static org.springframework.web.server.adapter.WebHttpHandlerBuilder.applicationContext;

/**
 *
 * @author andre
 */
@Configuration
public class SchedulerConfiguration {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public JobDetail jobDetailShutterDownJob() {

        logger.info("called jobDetailShutterDownJob!");

        return JobBuilder.newJob().ofType(ShutterDownJob.class)
                .storeDurably()
                .withIdentity("Quartz_shutter_down_job")
                .withDescription("Scheduler for closing shutters.")
                .build();
    }

    @Bean
    public Trigger trigger(JobDetail job) {

        logger.info("called trigger!");

        return TriggerBuilder.newTrigger().forJob(job)
                .withIdentity("Quartz_Trigger")
                .withDescription("Daily trigger")
                .startAt(new Date())
                .withSchedule(simpleSchedule().repeatForever().withIntervalInHours(24))
                .build();
    }

    @Bean
    public SpringBeanJobFactory springBeanJobFactory() {
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    @Bean
    public Scheduler scheduler(Trigger trigger, JobDetail job) throws IOException, SchedulerException {
        StdSchedulerFactory factory = new StdSchedulerFactory();
        factory.initialize(new ClassPathResource("quartz.properties").getInputStream());

        Scheduler scheduler = factory.getScheduler();
        scheduler.setJobFactory(springBeanJobFactory());

        scheduler.start();
        logger.info("scheduler started!");
        
        return scheduler;
    }

}
