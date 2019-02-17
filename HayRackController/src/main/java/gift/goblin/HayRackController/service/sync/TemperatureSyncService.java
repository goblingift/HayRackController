/*
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.service.sync;

import gift.goblin.HayRackController.database.backup.repo.event.TemperatureMeasurementBackupRepository;
import gift.goblin.HayRackController.database.embedded.repo.event.TemperatureMeasurementRepository;
import gift.goblin.HayRackController.database.model.event.ScheduledShutterMovement;
import gift.goblin.HayRackController.database.model.event.TemperatureMeasurement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Class which contains several methods for syncing the measured temperature
 * values.
 *
 * @author andre
 */
@Component
public class TemperatureSyncService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private TemperatureMeasurementRepository embeddedRepo;

    @Autowired
    private TemperatureMeasurementBackupRepository backupRepo;

    /**
     * Backup the latest measured temperature values from embedded database to
     * the backup database.
     */
    public void backupValues() {

        Optional<TemperatureMeasurement> optLatestEntry = backupRepo.findTop1ByOrderByMeasuredAtDesc();
        if (optLatestEntry.isPresent()) {
            TemperatureMeasurement latestEntry = optLatestEntry.get();
            LocalDateTime latestMeasuredAt = latestEntry.getMeasuredAt();

            List<TemperatureMeasurement> newEntries = embeddedRepo.findAllWithMeasuredAtAfter(latestMeasuredAt);
            List<TemperatureMeasurement> syncedEntries = embeddedRepo.saveAll(newEntries);
            logger.info("Successful synced {} new entries from embedded-db to backup-db.", syncedEntries);
        }
    }

    /**
     * Prefills the embedded-db with all entries of the backup-db.
     */
    public void prefillEmbeddedDatabase() {
        logger.info("Fetch all temperature-measurement entries from backup-db");
        List<TemperatureMeasurement> tempBackupEntries = backupRepo.findAll();

        List<TemperatureMeasurement> importedEntries = embeddedRepo.saveAll(tempBackupEntries);
        logger.info("Successful imported {} temperatureMeasurement entries from backup-db to embedded-db.",
                importedEntries.size());
    }

}
