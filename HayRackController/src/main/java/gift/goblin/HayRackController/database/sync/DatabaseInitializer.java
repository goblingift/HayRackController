/*
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.database.sync;

import gift.goblin.HayRackController.database.backup.repo.event.ScheduledShutterMovementBackupRepository;
import gift.goblin.HayRackController.database.embedded.repo.event.ScheduledShutterMovementRepository;
import gift.goblin.HayRackController.database.model.event.ScheduledShutterMovement;
import java.util.List;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Initialize the embedded database, by copying entries from the
 * backup database at startup of the application.
 * @author andre
 */
@Component
public class DatabaseInitializer {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    ScheduledShutterMovementBackupRepository scheduledShutterBackupRepo;
    
    @Autowired
    ScheduledShutterMovementRepository scheduledShutterRepo;
    
    @PostConstruct
    private void afterInit() {
        
        logger.info("Fetch all ScheduledShutterMovement entries in backup-db...");
        List<ScheduledShutterMovement> backupedScheduledShutterEntries = scheduledShutterBackupRepo.findAll();
        
        scheduledShutterRepo.saveAll(backupedScheduledShutterEntries);
        logger.info("Successful imported {} ScheduledShutterMovement entries from backup-db to embedded-db.",
                backupedScheduledShutterEntries.size());
    }
    
}
