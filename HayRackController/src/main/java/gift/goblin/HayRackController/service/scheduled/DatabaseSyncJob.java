/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.service.scheduled;

import gift.goblin.HayRackController.service.sync.ApplicationConfigurationSyncService;
import gift.goblin.HayRackController.service.sync.FeedingEventSyncService;
import gift.goblin.HayRackController.service.sync.ScheduledShutterMovementSyncService;
import gift.goblin.HayRackController.service.sync.TareMeasurementSyncService;
import gift.goblin.HayRackController.service.sync.TemperatureDailyMaxMinSyncService;
import gift.goblin.HayRackController.service.sync.TemperatureSyncService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author andre
 */
public class DatabaseSyncJob implements Job{

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
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        temperatureSyncService.backupValues();
        scheduledShutterMovementSyncService.backupValues();
        temperatureDailyMaxMinSyncService.backupValues();
        applicationConfigurationSyncService.backupValues();
        tareMeasurementSyncService.backupValues();
        feedingEventSyncService.backupValues();
    }
    
}
