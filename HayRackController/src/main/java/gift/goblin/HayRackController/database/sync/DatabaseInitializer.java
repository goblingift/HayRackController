/*
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.database.sync;

import gift.goblin.HayRackController.database.backup.repo.event.ScheduledShutterMovementBackupRepository;
import gift.goblin.HayRackController.database.backup.repo.event.TemperatureMeasurementBackupRepository;
import gift.goblin.HayRackController.database.embedded.repo.event.ScheduledShutterMovementRepository;
import gift.goblin.HayRackController.database.embedded.repo.event.TemperatureMeasurementRepository;
import gift.goblin.HayRackController.database.model.event.ScheduledShutterMovement;
import gift.goblin.HayRackController.database.model.event.TemperatureMeasurement;
import gift.goblin.HayRackController.service.sync.ScheduledShutterMovementSyncService;
import gift.goblin.HayRackController.service.sync.TemperatureSyncService;
import java.util.List;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Initialize the embedded database, by copying entries from the backup database
 * at startup of the application.
 *
 * @author andre
 */
@Component
public class DatabaseInitializer {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    TemperatureSyncService temperatureSyncService;
    
    @Autowired
    ScheduledShutterMovementSyncService scheduledShutterMovementSyncService;
    
    @PostConstruct
    private void afterInit() {
        temperatureSyncService.prefillEmbeddedDatabase();
        scheduledShutterMovementSyncService.prefillEmbeddedDatabase();
    }

}
