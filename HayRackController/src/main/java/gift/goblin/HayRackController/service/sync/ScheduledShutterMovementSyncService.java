/*
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.service.sync;

import gift.goblin.HayRackController.database.backup.repo.event.ScheduledShutterMovementBackupRepository;
import gift.goblin.HayRackController.database.embedded.repo.event.ScheduledShutterMovementRepository;
import gift.goblin.HayRackController.database.model.event.ScheduledShutterMovement;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Syncs the scheduled shutter movement entries between embedded-db and backup-db.
 * @author andre
 */
@Component
public class ScheduledShutterMovementSyncService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ScheduledShutterMovementBackupRepository backupRepo;

    @Autowired
    ScheduledShutterMovementRepository embeddedRepo;

    /**
     * Prefills the embedded database, by removing all entries in the embedded-db
     * and copying all entries from the backup-db.
     */
    public void prefillEmbeddedDatabase() {
        logger.info("DatabaseSyncJob starts syncing the scheduled shutter movements");
        List<ScheduledShutterMovement> backupEntries = backupRepo.findAll();
        
        embeddedRepo.deleteAll();
        embeddedRepo.saveAll(backupEntries);
        logger.info("Finished prefill scheduledShutterMovements from backup-db ({} entries)"
                + " to the backup-db.", backupEntries.size());
    }
    
    /**
     * Just remove all entries of the backup-db with these from the embedded-db.
     */
    public void backupValues() {
        
        backupRepo.deleteAll();
        
        List<ScheduledShutterMovement> embeddedEntries = embeddedRepo.findAll();
        List<ScheduledShutterMovement> backupedEntries = backupRepo.saveAll(embeddedEntries);
        logger.info("Successful backuped {} ScheduledShutterMovement entries from embedded-db to backup-db.", backupedEntries.size());
    }

}
