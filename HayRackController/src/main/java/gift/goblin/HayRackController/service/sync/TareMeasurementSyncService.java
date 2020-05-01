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
    public int backupValues() {
        
        int syncedEntitiesCount = 0;
        
        List<TareMeasurement> syncEntities;
        Optional<TareMeasurement> optLastEntry = backupRepo.findTop1ByOrderByMeasuredAtDesc();
        if (optLastEntry.isPresent()) {
            TareMeasurement latestEntry = optLastEntry.get();
            LocalDateTime from = latestEntry.getMeasuredAt();
            
            syncEntities = embeddedRepo.findByMeasuredAtAfter(from);
        } else {
            syncEntities = embeddedRepo.findAll();
        }
        
        for (TareMeasurement actSyncEntity : syncEntities) {
            logger.info(actSyncEntity.toString());
        }
        
        List<TareMeasurement> syncedEntities = backupRepo.saveAll(syncEntities);
        if (!syncedEntities.isEmpty()) {
            syncedEntitiesCount = syncEntities.size();
            logger.info("Successful synced {} new tare-measurement entries from embedded-db to backup-db.",
                    syncedEntities.size());
        }
        
        return syncedEntitiesCount;
    }

    @Override
    public int prefillEmbeddedDatabase() {
        logger.info("Fetch all tare-measurement entries from backup-db");
        List<TareMeasurement> tareBackupEntries = backupRepo.findAll();

        List<TareMeasurement> importedEntries = embeddedRepo.saveAll(tareBackupEntries);
        logger.info("Successful imported {} tare-measurement entries from backup-db to embedded-db.",
                importedEntries.size());
        
        return importedEntries.size();
    }
    
}
