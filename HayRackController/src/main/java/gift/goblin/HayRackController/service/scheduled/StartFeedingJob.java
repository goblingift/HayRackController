/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gift.goblin.HayRackController.service.scheduled;

import gift.goblin.HayRackController.database.security.model.ScheduledShutterMovement;
import gift.goblin.HayRackController.database.security.repo.ScheduledShutterMovementRepository;
import gift.goblin.HayRackController.service.io.ShutterController;
import gift.goblin.HayRackController.service.tools.DateAndTimeUtil;
import gift.goblin.HayRackController.service.tools.StringUtils;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.logging.Level;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Job which includes logic for start the feeding (shutters up).
 *
 * @author andre
 */
@Component
public class StartFeedingJob implements Job {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ShutterController shutterController;

    @Autowired
    private SchedulerJobService schedulerJobFactory;

    @Autowired
    private StringUtils stringUtils;

    @Autowired
    private DateAndTimeUtil dateAndTimeUtil;

    @Autowired
    ScheduledShutterMovementRepository repo;

    @Autowired
    private Scheduler scheduler;

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        logger.info("Start of feeding scheduled! Description: {}", jec.getTrigger().getDescription());

        try {
            shutterController.openShutter();
        } catch (InterruptedException ex) {
            logger.error("Exception thrown while closing shutters!", ex);
        }
        
        String jobKey = jec.getJobDetail().getKey().getName().toString();
        int jobId = stringUtils.getJobId(jobKey);

        createNewStopFeedingScheduler(jobId, jobKey);
    }

    /**
     * Creates a scheduler for stopping the feeding (Closing the shutters again).
     * @param nextExecutionDateTime DateTime when the shutters will be closed.
     * @param jobId id of the job.
     * @param description description, like 'dinner*.
     */
    private void createNewStopFeedingScheduler(int jobId, String description) {

        Optional<ScheduledShutterMovement> optEntity = repo.findById(new Long(jobId));
        if (optEntity.isPresent()) {
            ScheduledShutterMovement entity = optEntity.get();
            Integer feedingDuration = entity.getFeedingDuration();

            LocalDateTime stopFeedingDateTime = dateAndTimeUtil.getCalculatedDateTime(feedingDuration);

            ZonedDateTime zdt = stopFeedingDateTime.atZone(ZoneId.systemDefault());
            Date nextExecutionDate = Date.from(zdt.toInstant());

            JobDetail stopFeedingJob = schedulerJobFactory.createStopFeedingJob(jobId);
            Trigger stopTrigger = schedulerJobFactory.createStopFeedingTrigger(jobId, description, nextExecutionDate, stopFeedingJob);

            try {
                if (scheduler.checkExists(stopFeedingJob.getKey())) {
                    scheduler.deleteJob(stopFeedingJob.getKey());
                }
                
                scheduler.scheduleJob(stopFeedingJob, stopTrigger);
                logger.info("Successful created new scheduler for stop feeding. Next execution: {}", stopFeedingDateTime);
            } catch (SchedulerException ex) {
                logger.error("Exception while creating stop-feeding scheduler!", ex);
            }
        } else {
            logger.warn("Couldnt find an entity in the ScheduledShutterMovement table with id: {}", jobId);
        }

    }

}