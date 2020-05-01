/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.service.sync;

import gift.goblin.HayRackController.database.backup.repo.event.ScheduledShutterMovementBackupRepository;
import gift.goblin.HayRackController.database.embedded.repo.event.ScheduledShutterMovementRepository;
import gift.goblin.HayRackController.database.model.event.ScheduledShutterMovement;
import gift.goblin.HayRackController.service.scheduled.SchedulerJobService;
import gift.goblin.HayRackController.service.tools.DateAndTimeUtil;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Syncs the scheduled shutter movement entries between embedded-db and
 * backup-db.
 *
 * @author andre
 */
@Component
public class ScheduledShutterMovementSyncService implements DatabaseSynchronizer {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ScheduledShutterMovementBackupRepository backupRepo;

    @Autowired
    ScheduledShutterMovementRepository embeddedRepo;

    @Autowired
    private DateAndTimeUtil dateAndTimeUtil;

    @Autowired
    private SchedulerJobService schedulerJobService;

    @Autowired
    private Scheduler scheduler;

    /**
     * Prefills the embedded database, by removing all entries in the
     * embedded-db and copying all entries from the backup-db.
     */
    @Override
    public int prefillEmbeddedDatabase() {
        
        int syncedEntitiesCount = 0;
        
        logger.info("DatabaseSyncJob starts syncing the scheduled shutter movements");
        List<ScheduledShutterMovement> backupEntries = backupRepo.findAll();

        embeddedRepo.deleteAll();
        List<ScheduledShutterMovement> newEntities = embeddedRepo.saveAll(backupEntries);
        syncedEntitiesCount = newEntities.size();
        logger.info("Finished restore {} scheduledShutterMovements from backup-db to the embedded-db, register schedulers next...", newEntities.size());

        for (ScheduledShutterMovement actEntry : newEntities) {
            Date nextExecutionDate = dateAndTimeUtil.getNextExecutionDate(actEntry.getFeedingStartTime());
            JobDetail jobDetail = schedulerJobService.createStartFeedingJob(actEntry.getId());
            SimpleTrigger newTrigger = schedulerJobService.createStartFeedingTrigger(actEntry.getId(), nextExecutionDate, jobDetail);
            try {
                scheduler.scheduleJob(jobDetail, newTrigger);
                logger.info("Created scheduler for ScheduledShutterMovement entry with id ({}), next execution at: {}", actEntry.getId(), nextExecutionDate);
            } catch (SchedulerException ex) {
                logger.error("Couldnt register new scheduled job!", ex);
            }
        }
        
        return syncedEntitiesCount;
    }

    /**
     * Adds new entries of the embedded database to backup database. Also
     * removes entries which exists only at backup database.
     */
    @Override
    public int backupValues() {
        
        int syncedEntitiesCount = 0;

        List<ScheduledShutterMovement> embeddedEntries = embeddedRepo.findAll();
        List<ScheduledShutterMovement> backupedEntries = backupRepo.findAll();

        List<ScheduledShutterMovement> newEntries = embeddedEntries.stream().filter(s -> !backupedEntries.contains(s)).collect(Collectors.toList());
        List<ScheduledShutterMovement> toDeleteEntries = backupedEntries.stream().filter(s -> !embeddedEntries.contains(s)).collect(Collectors.toList());

        List<ScheduledShutterMovement> syncedEntries = backupRepo.saveAll(newEntries);
        if (!syncedEntries.isEmpty()) {
            syncedEntitiesCount = syncedEntries.size();
            logger.info("Successful backuped {} ScheduledShutterMovement entries from embedded-db to backup-db.", syncedEntries.size());
        }
        
        if (!toDeleteEntries.isEmpty()) {
            backupRepo.deleteAll(toDeleteEntries);
            logger.info("Successful deleted {} ScheduledShutterMovement entries in backup-db.", toDeleteEntries.size());
        }
        
        return syncedEntitiesCount;
    }

}
