/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.service.scheduled;

import gift.goblin.HayRackController.service.event.FeedingEventService;
import gift.goblin.HayRackController.database.model.event.ScheduledShutterMovement;
import gift.goblin.HayRackController.database.embedded.repo.event.ScheduledShutterMovementRepository;
import gift.goblin.HayRackController.service.io.IOController;
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
    private IOController ioController;

    @Autowired
    private SchedulerJobService schedulerJobService;

    @Autowired
    private StringUtils stringUtils;

    @Autowired
    private DateAndTimeUtil dateAndTimeUtil;

    @Autowired
    ScheduledShutterMovementRepository scheduledShutterMovementRepo;

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private FeedingEventService feedingEventService;

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {

        String jobKey = jec.getJobDetail().getKey().getName().toString();
        long jobId = stringUtils.getJobId(jobKey);

        if (ioController.isMaintenanceModeActive()) {
            logger.warn("Will skip start feeding job {}, cause maintenance mode is active!", jobId);
        } else {
            logger.info("Start of feeding scheduled! Job-Id: {}", jobKey);
            try {
                ioController.openShutter();
                ioController.triggerRelayLight(true);
                Long feedingEventId = feedingEventService.addNewFeedingEvent(jobId);
                if (ioController.isLoadCellsActivated()) {
                    feedingEventService.measureStartWeight(feedingEventId);
                }
            } catch (Exception ex) {
                logger.error("Exception thrown while try to open shutters!", ex);
            }

            createNewStopFeedingScheduler(jobId, jobKey);
        }
    }

    /**
     * Creates a scheduler for stopping the feeding (Closing the shutters
     * again).
     *
     * @param nextExecutionDateTime DateTime when the shutters will be closed.
     * @param jobId id of the job.
     * @param description description, like 'dinner*.
     */
    private void createNewStopFeedingScheduler(long jobId, String description) {

        Optional<ScheduledShutterMovement> optEntity = scheduledShutterMovementRepo.findById(jobId);
        if (optEntity.isPresent()) {
            ScheduledShutterMovement entity = optEntity.get();
            Integer feedingDuration = entity.getFeedingDuration();

            LocalDateTime stopFeedingDateTime = dateAndTimeUtil.getCalculatedDateTime(feedingDuration);

            ZonedDateTime zdt = stopFeedingDateTime.atZone(ZoneId.systemDefault());
            Date nextExecutionDate = Date.from(zdt.toInstant());

            JobDetail stopFeedingJob = schedulerJobService.createStopFeedingJob(jobId);
            Trigger stopTrigger = schedulerJobService.createStopFeedingTrigger(jobId, description, nextExecutionDate, stopFeedingJob);

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
