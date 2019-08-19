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
import java.util.List;

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
    public void backupValues() {
        
        List<ApplicationConfiguration> embeddedEntities = embeddedRepo.findAll();
        if (!embeddedEntities.isEmpty()) {
            ApplicationConfiguration embeddedEntity = embeddedEntities.get(0);
            
            List<ApplicationConfiguration> backupEntities = backupRepo.findAll();
            if (backupEntities.isEmpty()) {
                ApplicationConfiguration savedEntity = backupRepo.save(embeddedEntity);
                logger.info("Successful synced new entry in backup-db: {}", savedEntity);
            } else {
                ApplicationConfiguration backupEntity = backupEntities.get(0);
                if (backupEntity.getSoundId() != embeddedEntity.getSoundId()) {
                    backupEntity.setSoundId(embeddedEntity.getSoundId());
                    ApplicationConfiguration savedEntity = backupRepo.save(backupEntity);
                    logger.info("Successful overwrote entry in backup-db: {}", savedEntity);
                }
            }
        }
    }

    @Override
    public void prefillEmbeddedDatabase() {
        
        logger.info("Fetch all application-configuration entries from backup-db");
        List<ApplicationConfiguration> backupEntities = backupRepo.findAll();
        
        List<ApplicationConfiguration> synchronizedEntities = embeddedRepo.saveAll(backupEntities);
        logger.info("Successful synchronized {} entities from backup-db to embeded-db.", synchronizedEntities.size());
        
    }

}
