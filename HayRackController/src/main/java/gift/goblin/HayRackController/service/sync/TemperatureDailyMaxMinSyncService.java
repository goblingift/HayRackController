/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.service.sync;

import gift.goblin.HayRackController.database.backup.repo.event.TemperatureDailyMaxMinBackupRepository;
import gift.goblin.HayRackController.database.backup.repo.event.TemperatureMeasurementBackupRepository;
import gift.goblin.HayRackController.database.embedded.repo.event.TemperatureDailyMaxMinRepository;
import gift.goblin.HayRackController.database.embedded.repo.event.TemperatureMeasurementRepository;
import gift.goblin.HayRackController.database.model.event.TemperatureDailyMaxMin;
import gift.goblin.HayRackController.database.model.event.TemperatureMeasurement;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Synchronizes the daily max and min entries between the embedded and the
 * backup database.
 *
 * @author andre
 */
@Component
public class TemperatureDailyMaxMinSyncService implements SynchronizedDatabase {

    @Autowired
    private TemperatureDailyMaxMinBackupRepository backupRepo;

    @Autowired
    private TemperatureDailyMaxMinRepository embeddedRepo;

    @Autowired
    private TemperatureMeasurementBackupRepository tempBackupRepo;
    
    @Autowired
    private TemperatureMeasurementRepository tempEmbeddedRepo;

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
            TemperatureDailyMaxMin backupEntry;

            if (optBackupEntry.isPresent()) {
                // There´s a TemperatureDailyMaxMin entry for that date
                backupEntry = optBackupEntry.get();
            } else {
                // No TemperatureDailyMaxMin entry for that date, create em
                backupEntry = new TemperatureDailyMaxMin(lastEntry.get().getDate());
            }

            TemperatureMeasurement min = lastEntry.get().getMin();
            TemperatureMeasurement backupEntityMin = findOrCreateTempMeasurementBackupEntry(min);

            TemperatureMeasurement max = lastEntry.get().getMax();
            TemperatureMeasurement backupEntityMax = findOrCreateTempMeasurementBackupEntry(max);

            backupEntry.setMin(backupEntityMin);
            backupEntry.setMax(backupEntityMax);
            
            backupRepo.save(backupEntry);
            logger.info("Successful synced TemperatureDailyMaxMin from embedded-db to backup-db: {}", backupEntry);
        }
    }

    /**
     * Looks for an TemperatureMeasurement entry in the backup database with
     * same id and values. Otherwise create ones.
     *
     * @param original
     * @return the same entity in the backup database.
     */
    private TemperatureMeasurement findOrCreateTempMeasurementBackupEntry(TemperatureMeasurement original) {
        
        Optional<TemperatureMeasurement> optResult = tempBackupRepo
                .findByTemperatureAndMeasuredAt(original.getTemperature(), original.getMeasuredAt());
        if (optResult.isPresent()) {
            // The TemperatureMeasurement entity already exists in the backup-db, just return em
            return optResult.get();
        } else {
            // The TemperatureMeasurement entity doesnt exist in the backup-db, create em and return
            TemperatureMeasurement newEntity = new TemperatureMeasurement(original.getTemperature(),
                    original.getTemperatureFahrenheit(), original.getHumidity(), original.getMeasuredAt());
            TemperatureMeasurement createdEntity = tempBackupRepo.save(newEntity);
            return createdEntity;
        }
    }

    /**
     * Looks for an TemperatureMeasurement entry in the backup database with
     * same id and values. Otherwise create ones.
     *
     * @param original
     * @return the same entity in the backup database.
     */
    private TemperatureMeasurement findOrCreateTempMeasurementEmbeddedEntry(TemperatureMeasurement original) {
        
        Optional<TemperatureMeasurement> optResult = tempEmbeddedRepo
                .findByTemperatureAndMeasuredAt(original.getTemperature(), original.getMeasuredAt());
        if (optResult.isPresent()) {
            // The TemperatureMeasurement entity already exists in the backup-db, just return em
            return optResult.get();
        } else {
            // The TemperatureMeasurement entity doesnt exist in the backup-db, create em and return
            TemperatureMeasurement newEntity = new TemperatureMeasurement(original.getTemperature(),
                    original.getTemperatureFahrenheit(), original.getHumidity(), original.getMeasuredAt());
            TemperatureMeasurement createdEntity = tempEmbeddedRepo.save(newEntity);
            return createdEntity;
        }
    }
    
    @Override
    public void prefillEmbeddedDatabase() {
        logger.info("Start prefilling the TemperatureDailyMaxMin-table from backup-db to embedded-db.");
        List<TemperatureDailyMaxMin> allEntries = backupRepo.findAll();

        for (TemperatureDailyMaxMin actEntry : allEntries) {
            TemperatureMeasurement embeddedMin = findOrCreateTempMeasurementEmbeddedEntry(actEntry.getMin());
            TemperatureMeasurement embeddedMax = findOrCreateTempMeasurementEmbeddedEntry(actEntry.getMax());
            
            actEntry.setMin(embeddedMin);
            actEntry.setMax(embeddedMax);
            embeddedRepo.save(actEntry);
        }
        
        logger.info("Successful imported {} TemperatureDailyMaxMin entries from backup-db to embedded-db.",
                allEntries.size());
    }

}
