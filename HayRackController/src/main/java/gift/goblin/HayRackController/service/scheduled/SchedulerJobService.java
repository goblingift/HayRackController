/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.service.scheduled;

import java.util.Date;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Offers different methods to deliver objects for the creation of schedulers.
 *
 * @author andre
 */
@Service
public class SchedulerJobService {

    public static final String PREFIX_START_FEEDING_JOB = "start_feeding_job_";
    public static final String PREFIX_STOP_FEEDING_JOB = "stop_feeding_job_";
    public static final String PREFIX_START_FEEDING_TRIGGER = "start_feeding_trigger_";
    public static final String PREFIX_STOP_FEEDING_TRIGGER = "stop_feeding_trigger_";
    public static final String GROUP_START_TRIGGERS = "start_feeding_triggers";
    public static final String GROUP_STOP_TRIGGERS = "stop_feeding_triggers";
    public static final String ID_TEMP_MEASUREMENT_JOB = "temperature_measurement_job";
    public static final String ID_DB_SYNC_JOB = "db_sync_job";
    public static final String GROUP_SENSORS = "sensors";
    
    public static final int INTERVAL_TEMP_MEASUREMENT = 10;
    
    @Value("${backup.datasource.synchronize-interval}")
    public int INTERVAL_DATABASE_SYNC;
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private Scheduler scheduler;

    /**
     * Will start all schedulers, which shall get started automatically.
     */
    @PostConstruct
    private void startImpliciteSchedulers() {
        
        JobDetail temperatureMeasurementJob = createTemperatureMeasurementJob();
        SimpleTrigger temperatureMeasurementTrigger = createTemperatureMeasurementTrigger(temperatureMeasurementJob);
        try {
            scheduler.scheduleJob(temperatureMeasurementJob, temperatureMeasurementTrigger);
            logger.info("Successful registered scheduler for temperature measurement.");
        } catch (SchedulerException ex) {
            logger.error("Couldnt register new scheduled job for temperature measurement!", ex);
        }

        JobDetail databaseSyncJob = createDatabaseSyncJob();
        SimpleTrigger databaseSyncTrigger = createDatabaseSyncTrigger(databaseSyncJob);
        try {
            scheduler.scheduleJob(databaseSyncJob, databaseSyncTrigger);
            logger.info("Successful registered scheduler for database sync.");
        } catch (SchedulerException ex) {
            logger.error("Couldnt register new scheduled job for database sync!", ex);
        }
        
    }
    
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
    
    private  JobDetail createDatabaseSyncJob() {
        JobDetail jobDetail = JobBuilder.newJob().ofType(DatabaseSyncJob.class)
                .storeDurably()
                .withIdentity(ID_DB_SYNC_JOB)
                .withDescription("Job for syncing entries from embedded database to backup database.")
                .build();

        return jobDetail;
    }
    
    private SimpleTrigger createDatabaseSyncTrigger(JobDetail jobDetail) {
        SimpleTrigger trigger = TriggerBuilder.newTrigger().forJob(jobDetail)
                .withIdentity(ID_DB_SYNC_JOB)
                .startNow()
                .withSchedule(simpleSchedule().repeatForever().withIntervalInMinutes(INTERVAL_DATABASE_SYNC))
                .build();

        return trigger;
    }
    
    private JobDetail createTemperatureMeasurementJob() {
        JobDetail jobDetail = JobBuilder.newJob().ofType(TemperatureMeasurementJob.class)
                .storeDurably()
                .withIdentity(ID_TEMP_MEASUREMENT_JOB)
                .withDescription("Job for measurement of temperature and humidity.")
                .build();

        return jobDetail;
    }

    /**
     * Creates a trigger for the temperature measurement.
     * By default, it will repeat itself every 15 minutes.
     * @param jobDetail - just give the JobDetail object of the temp-measurement.
     * @return trigger object
     */
    private SimpleTrigger createTemperatureMeasurementTrigger(JobDetail jobDetail) {
        SimpleTrigger trigger = TriggerBuilder.newTrigger().forJob(jobDetail)
                .withIdentity(ID_TEMP_MEASUREMENT_JOB, GROUP_SENSORS)
                .startNow()
                .withSchedule(simpleSchedule().repeatForever().withIntervalInMinutes(INTERVAL_TEMP_MEASUREMENT))
                .build();

        return trigger;
    }
    
    /**
     * Creates a new trigger to start the feeding on the hay-rack. This trigger
     * will repeat itself every 24 hours!
     *
     * @param id unique id for this trigger.
     * @param nextExecutionDate the next Date when this trigger will be
     * scheduled.
     * @param jobDetail for which job this trigger shall be used.
     * @return
     */
    public SimpleTrigger createStartFeedingTrigger(int id, Date nextExecutionDate, JobDetail jobDetail) {
        SimpleTrigger trigger = TriggerBuilder.newTrigger().forJob(jobDetail)
                .withIdentity(PREFIX_START_FEEDING_TRIGGER + id, GROUP_START_TRIGGERS)
                .startAt(nextExecutionDate)
                .withSchedule(simpleSchedule().repeatForever().withIntervalInHours(24))
                .build();

        return trigger;
    }

    /**
     * Creates a new trigger to stop the feeding on the hay-rack. This is a
     * non-repetitive trigger, which will be executed only one time.
     *
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

    /**
     * Deletes a scheduled start feeding job.
     * @param id id of the job.
     */
    public void deleteStartFeedingJob(int id) {

        try {
            scheduler.pauseTrigger(TriggerKey.triggerKey(PREFIX_START_FEEDING_TRIGGER + id, GROUP_START_TRIGGERS));
            scheduler.deleteJob(JobKey.jobKey(PREFIX_START_FEEDING_JOB + id, GROUP_START_TRIGGERS));
            logger.info("Successful deleted scheduled job: {}", id);
        } catch (SchedulerException ex) {
            logger.error("Couldnt delete scheduled job: {}", id);
        }
    }

}
