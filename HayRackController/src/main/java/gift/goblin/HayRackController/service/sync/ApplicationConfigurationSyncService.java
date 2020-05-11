/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.service.sync;

import gift.goblin.HayRackController.controller.model.LoadCellSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import gift.goblin.HayRackController.database.backup.repo.configuration.ApplicationConfigurationBackupRepository;
import gift.goblin.HayRackController.database.embedded.repo.configuration.ApplicationConfigurationRepository;
import gift.goblin.HayRackController.database.embedded.repo.weight.TareMeasurementRepository;
import gift.goblin.HayRackController.database.model.configuration.ApplicationConfiguration;
import gift.goblin.HayRackController.database.model.configuration.ConfigurationType;
import gift.goblin.HayRackController.database.model.weight.TareMeasurement;
import gift.goblin.HayRackController.service.configuration.ConfigurationService;
import gift.goblin.HayRackController.service.io.IOController;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Synchronizes the application configuration entries between the backup-db and
 * the embedded-db.
 *
 * @author andre
 */
@Component
public class ApplicationConfigurationSyncService implements DatabaseSynchronizer {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ApplicationConfigurationBackupRepository backupRepo;

    @Autowired
    private ApplicationConfigurationRepository embeddedRepo;

    @Autowired
    private IOController iOController;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private TareMeasurementRepository tareMeasurementRepository;

    @Override
    public int backupValues() {
        int syncedEntitiesCount = 0;
        List<ApplicationConfiguration> embeddedEntities = embeddedRepo.findAll();
        if (!embeddedEntities.isEmpty()) {

            List<ApplicationConfiguration> backupEntities = backupRepo.findAll();

            for (ApplicationConfiguration actEmbeddedEntity : embeddedEntities) {
                Optional<ApplicationConfiguration> optBackupEntity = backupEntities.stream()
                        // filter for entities with the correct config-id
                        .filter(e -> e.getConfigurationId() == actEmbeddedEntity.getConfigurationId())
                        // filter for entities which have older modification-date than our embedded entity
                        .filter(e -> e.getLastModified().isBefore(actEmbeddedEntity.getLastModified()))
                        .findFirst();
                if (optBackupEntity.isPresent()) {
                    // Update backup entity
                    ApplicationConfiguration backupEntity = optBackupEntity.get();
                    backupEntity.setValue(actEmbeddedEntity.getValue());
                    backupEntity.setLastModified(LocalDateTime.now());
                    backupRepo.save(backupEntity);
                    syncedEntitiesCount++;
                } else {
                    // No backup entity, just create
                    backupRepo.save(actEmbeddedEntity);
                    syncedEntitiesCount++;
                }
            }
        }

        if (syncedEntitiesCount > 0) {
            logger.info("Successfull synced {} Application-Config entities from embedded-db to backup-db.", syncedEntitiesCount);
        }

        return syncedEntitiesCount;
    }

    @Override
    public int prefillEmbeddedDatabase() {

        logger.info("Fetch all application-configuration entries from backup-db");
        List<ApplicationConfiguration> backupEntities = backupRepo.findAll();

        List<ApplicationConfiguration> synchronizedEntities = embeddedRepo.saveAll(backupEntities);
        logger.info("Successful synchronized {} entities from backup-db to embeded-db.", synchronizedEntities.size());
        embeddedRepo.flush();

        // If load-cells were activated in the config, initialize them
        Optional<ApplicationConfiguration> optLoadCellActivatedConfig = synchronizedEntities.stream()
                .filter(c -> c.getConfigurationId() == ConfigurationType.LOADCELLS_ACTIVATED.getId()).findFirst();

        if (optLoadCellActivatedConfig.isPresent() && optLoadCellActivatedConfig.get().getValue().equals(Boolean.TRUE.toString())) {
            initializeLoadCells();
        } else {
            logger.info("Load cells werent activated in config- dont initialize them.");
        }

        return synchronizedEntities.size();
    }

    /**
     * Initialize the load-cells. Will read the responsible entries of the
     * config-Entities to get the required values.
     *
     * @param configEntities
     */
    private void initializeLoadCells() {

        LoadCellSettings loadCellSettings = configurationService.getLoadCellSettings();
        logger.info("Start initializing load cells now with following settings: {}", loadCellSettings);
        if (loadCellSettings.isEnabled()) {
            logger.info("Load-cells were activated in the loaded configuration, initialize load-cells now...");

            Optional<TareMeasurement> optTareMeasurement = Optional.empty();
            try {
                logger.info("Try to read the last tare-measurement, to initialize load-cells correctly...");
                optTareMeasurement = tareMeasurementRepository.findTop1ByOrderByMeasuredAtDesc();
            } catch (Exception e) {
                logger.error("Exception while try to read the last tare measurement entry!", e);
            }

            if (optTareMeasurement.isPresent()) {
                if (loadCellSettings.getAmount() >= 4) {
                    iOController.initializeLoadCell4(loadCellSettings);
                    if (optTareMeasurement.isPresent()) {
                        iOController.setTareValueLoadCell4(optTareMeasurement.get().getTareLoadCell4());
                    }
                }
                if (loadCellSettings.getAmount() >= 3) {
                    iOController.initializeLoadCell3(loadCellSettings);
                    if (optTareMeasurement.isPresent()) {
                        iOController.setTareValueLoadCell3(optTareMeasurement.get().getTareLoadCell3());
                    }
                }
                if (loadCellSettings.getAmount() >= 2) {
                    iOController.initializeLoadCell2(loadCellSettings);
                    if (optTareMeasurement.isPresent()) {
                        iOController.setTareValueLoadCell2(optTareMeasurement.get().getTareLoadCell2());
                    }
                }
                if (loadCellSettings.getAmount() >= 1) {
                    iOController.initializeLoadCell1(loadCellSettings);
                    if (optTareMeasurement.isPresent()) {
                        iOController.setTareValueLoadCell1(optTareMeasurement.get().getTareLoadCell1());
                    }
                }

                iOController.setLoadCellAmount(loadCellSettings.getAmount());
                iOController.setLoadCellsActivated(true);
            } else {
                logger.warn("No tare-measurement entry found, cant initialize load-cells.");
            }

        }

    }

}
