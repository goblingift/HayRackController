/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.service.sync;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import gift.goblin.HayRackController.database.backup.repo.configuration.ApplicationConfigurationBackupRepository;
import gift.goblin.HayRackController.database.embedded.repo.configuration.ApplicationConfigurationRepository;
import gift.goblin.HayRackController.database.model.configuration.ApplicationConfiguration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Synchronizes the application configuration entries between the backup-db and
 * the embedded-db.
 * @author andre
 */
@Component
public class ApplicationConfigurationSyncService implements DatabaseSynchronizer {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ApplicationConfigurationBackupRepository backupRepo;
    
    @Autowired
    private ApplicationConfigurationRepository embeddedRepo;
    
    
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
        
        return synchronizedEntities.size();
    }

}
