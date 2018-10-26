/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gift.goblin.HayRackController.service.scheduled;

import java.util.Date;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Offers different methods to deliver objects for the creation of schedulers.
 *
 * @author andre
 */
@Service
public class SchedulerJobFactory {
    
    public static final String PREFIX_START_FEEDING_JOB = "start_feeding_job_";
    public static final String PREFIX_STOP_FEEDING_JOB = "stop_feeding_job_";
    public static final String PREFIX_START_FEEDING_TRIGGER = "start_feeding_trigger_";
    public static final String PREFIX_STOP_FEEDING_TRIGGER = "stop_feeding_trigger_";
    public static final String GROUP_START_TRIGGERS = "start_feeding_triggers";
    public static final String GROUP_STOP_TRIGGERS = "stop_feeding_triggers";

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    
    public JobDetail createStartFeedingJob(int id) {
        JobDetail newjobDetail = JobBuilder.newJob().ofType(StartFeedingJob.class)
                .storeDurably()
                .withIdentity(PREFIX_START_FEEDING_JOB + id)
                .withDescription("Job for starting the feeding.")
                .build();
        
        return newjobDetail;
    }
    
    public JobDetail createStopFeedingJob(int id) {
        JobDetail newjobDetail = JobBuilder.newJob().ofType(StopFeedingJob.class)
                .storeDurably()
                .withIdentity(PREFIX_STOP_FEEDING_JOB + id)
                .withDescription("Job for stopping the feeding.")
                .build();
        
        return newjobDetail;
    }
    
    /**
     * Creates a new trigger to start the feeding on the hay-rack.
     * This trigger will repeat itself every 24 hours!
     * @param id unique id for this trigger.
     * @param description description of this trigger, like breakfast, dinner.
     * @param nextExecutionDate the next Date when this trigger will be scheduled.
     * @param jobDetail for which job this trigger shall be used.
     * @return 
     */
    public SimpleTrigger createStartFeedingTrigger(int id, String description, Date nextExecutionDate, JobDetail jobDetail) {
        SimpleTrigger trigger = TriggerBuilder.newTrigger().forJob(jobDetail)
                .withIdentity(PREFIX_START_FEEDING_TRIGGER + id, GROUP_START_TRIGGERS)
                .withDescription(description)
                .startAt(nextExecutionDate)
                .withSchedule(simpleSchedule().repeatForever().withIntervalInMinutes(3))
                .build();
        
        return trigger;
    }

    /**
     * Creates a new trigger to stop the feeding on the hay-rack.
     * This is a non-repetitive trigger, which will be executed only one time.
     * @param id unique id for this trigger.
     * @param description description of this trigger, like breakfast, dinner.
     * @param nextExecutionDate the execution Date.
     * @param jobDetail for which job this trigger shall be used.
     * @return 
     */
    public Trigger createStopFeedingTrigger(int id, String description, Date nextExecutionDate, JobDetail jobDetail) {
        Trigger trigger = TriggerBuilder.newTrigger().forJob(jobDetail)
                .withIdentity(PREFIX_STOP_FEEDING_TRIGGER + id, GROUP_STOP_TRIGGERS)
                .withDescription(description)
                .startAt(nextExecutionDate)
                .build();
        
        return trigger;
    }
}
