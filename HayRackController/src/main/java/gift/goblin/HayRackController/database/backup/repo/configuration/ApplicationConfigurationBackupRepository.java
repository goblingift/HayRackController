/*
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.database.backup.repo.configuration;

import gift.goblin.HayRackController.database.model.configuration.ApplicationConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author andre
 */
public interface ApplicationConfigurationBackupRepository extends JpaRepository<ApplicationConfiguration, Long>{
    
}
