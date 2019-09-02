/*
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.service.sync;

import gift.goblin.HayRackController.database.backup.repo.event.FeedingEventBackupRepository;
import gift.goblin.HayRackController.database.embedded.repo.event.FeedingEventRepository;
import gift.goblin.HayRackController.database.model.event.FeedingEvent;
import gift.goblin.HayRackController.database.model.event.ScheduledShutterMovement;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Synchronizes the FeedingEvent-entities between embedded and backup db.
 *
 * @author andre
 */
@Component
public class FeedingEventSyncService implements DatabaseSynchronizer {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FeedingEventRepository embeddedRepo;

    @Autowired
    private FeedingEventBackupRepository backupRepo;

    @Override
    public int backupValues() {

        int syncedEntitiesCount = 0;

        List<FeedingEvent> embeddedEntries = embeddedRepo.findAll();
        List<FeedingEvent> backupedEntries = backupRepo.findAll();

        List<FeedingEvent> differentEntries = embeddedEntries.stream()
                .filter(s -> !backupedEntries.contains(s)).collect(Collectors.toList());
        
        if (!differentEntries.isEmpty()) {
            List<FeedingEvent> syncedEntries = backupRepo.saveAll(differentEntries);
            syncedEntitiesCount = syncedEntries.size();
            logger.info("Successful backuped {} new FeedingEvent entries from embedded-db to backup-db.", syncedEntries.size());
        } else {
            logger.debug("No new FeedingEvent entries to backup.");
        }

        return syncedEntitiesCount;
    }

    @Override
    public int prefillEmbeddedDatabase() {

        int syncedEntitiesCount = 0;

        logger.info("DatabaseSyncJob starts syncing the FeedingEvent entries.");
        
        List<FeedingEvent> backupEntities = backupRepo.findAll();
        List<FeedingEvent> savedEntities = embeddedRepo.saveAll(backupEntities);
        syncedEntitiesCount = savedEntities.size();
        
        logger.info("Successful synced {} FeedingEvent entries from backup-db to embedded-db.", 
                syncedEntitiesCount);
        
        return syncedEntitiesCount;
    }

}
