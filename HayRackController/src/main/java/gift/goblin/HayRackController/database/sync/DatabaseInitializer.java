/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.database.sync;

import gift.goblin.HayRackController.service.sync.ApplicationConfigurationSyncService;
import gift.goblin.HayRackController.service.sync.FeedingEventSyncService;
import gift.goblin.HayRackController.service.sync.ScheduledShutterMovementSyncService;
import gift.goblin.HayRackController.service.sync.TareMeasurementSyncService;
import gift.goblin.HayRackController.service.sync.TemperatureDailyMaxMinSyncService;
import gift.goblin.HayRackController.service.sync.TemperatureSyncService;
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

    @Autowired
    TemperatureDailyMaxMinSyncService temperatureDailyMaxMinSyncService;
    
    @Autowired
    ApplicationConfigurationSyncService applicationConfigurationSyncService;

    @Autowired
    TareMeasurementSyncService tareMeasurementSyncService;
    
    @Autowired
    FeedingEventSyncService feedingEventSyncService;
    
    @PostConstruct
    private void afterInit() {
        temperatureSyncService.prefillEmbeddedDatabase();
        scheduledShutterMovementSyncService.prefillEmbeddedDatabase();
        temperatureDailyMaxMinSyncService.prefillEmbeddedDatabase();
        tareMeasurementSyncService.prefillEmbeddedDatabase();
        applicationConfigurationSyncService.prefillEmbeddedDatabase();
        feedingEventSyncService.prefillEmbeddedDatabase();
    }

}
