/*
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.service.sync;

import gift.goblin.HayRackController.database.backup.repo.weight.TareMeasurementBackupRepository;
import gift.goblin.HayRackController.database.embedded.repo.weight.TareMeasurementRepository;
import gift.goblin.HayRackController.database.model.weight.TareMeasurement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Synchronizes the tare-measurement entries of the embedded and backup db.
 * @author andre
 */
@Component
public class TareMeasurementSyncService implements DatabaseSynchronizer {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    TareMeasurementRepository embeddedRepo;
    
    @Autowired
    TareMeasurementBackupRepository backupRepo;
    
    @Override
    public void backupValues() {
        
        List<TareMeasurement> syncEntries;
        Optional<TareMeasurement> optLastEntry = backupRepo.findTop1ByOrderByMeasuredAtDesc();
        if (optLastEntry.isPresent()) {
            TareMeasurement latestEntry = optLastEntry.get();
            LocalDateTime from = latestEntry.getMeasuredAt();
            
            syncEntries = embeddedRepo.findByMeasuredAtAfter(from);
        } else {
            syncEntries = embeddedRepo.findAll();
        }
        
        List<TareMeasurement> syncedEntries = backupRepo.saveAll(syncEntries);
        if (!syncedEntries.isEmpty()) {
            logger.info("Successful synced {} new tare-measurement entries from embedded-db to backup-db.",
                    syncedEntries.size());
        }
        
    }

    @Override
    public void prefillEmbeddedDatabase() {
        logger.info("Fetch all tare-measurement entries from backup-db");
        List<TareMeasurement> tareBackupEntries = backupRepo.findAll();

        List<TareMeasurement> importedEntries = embeddedRepo.saveAll(tareBackupEntries);
        logger.info("Successful imported {} tare-measurement entries from backup-db to embedded-db.",
                importedEntries.size());
    }
    
}
