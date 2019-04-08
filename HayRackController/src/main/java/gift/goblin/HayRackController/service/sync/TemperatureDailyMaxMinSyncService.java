/*
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.service.sync;

import gift.goblin.HayRackController.database.backup.repo.event.TemperatureDailyMaxMinBackupRepository;
import gift.goblin.HayRackController.database.embedded.repo.event.TemperatureDailyMaxMinRepository;
import gift.goblin.HayRackController.database.model.event.TemperatureDailyMaxMin;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Synchronizes the daily max and min entries between the embedded and the
 * backup database.
 * @author andre
 */
@Component
public class TemperatureDailyMaxMinSyncService implements SynchronizedDatabase {

    @Autowired
    private TemperatureDailyMaxMinBackupRepository backupRepo;
    
    @Autowired
    private TemperatureDailyMaxMinRepository embeddedRepo;
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    /**
     * Backup the last entry in the embedded database to the backup database.
     * Will create a new entry in the backup database or just update, if there
     * is already an entry for that date in the backup database.
     */
    @Override
    public void backupValues() {
        
        Optional<TemperatureDailyMaxMin> lastEntry = embeddedRepo.findTop1ByOrderByDateDesc();
        if (lastEntry.isPresent()) {
            Optional<TemperatureDailyMaxMin> optBackupEntry = backupRepo.findByDate(lastEntry.get().getDate());
            if (optBackupEntry.isPresent()) {
                TemperatureDailyMaxMin backupEntry = optBackupEntry.get();
                backupEntry.setMax(lastEntry.get().getMax());
                backupEntry.setMin(lastEntry.get().getMin());
                backupRepo.save(backupEntry);
                logger.info("Successful synced TemperatureDailyMaxMin from embedded-db to backup-db: {}", backupEntry);
            } else {
                backupRepo.save(lastEntry.get());
                logger.info("Successful synced TemperatureDailyMaxMin from embedded-db to backup-db: {}", lastEntry);
            }
        }
        
    }

    @Override
    public void prefillEmbeddedDatabase() {
        
        List<TemperatureDailyMaxMin> allEntries = backupRepo.findAll();
        
        List<TemperatureDailyMaxMin> importedEntries = embeddedRepo.saveAll(allEntries);
        logger.info("Successful imported {} TemperatureDailyMaxMin entries from backup-db to embedded-db.",
                importedEntries.size());
    }
    
}
