/*
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.service.scheduled;

import gift.goblin.HayRackController.database.backup.repo.event.ScheduledShutterMovementBackupRepository;
import gift.goblin.HayRackController.database.embedded.repo.event.ScheduledShutterMovementRepository;
import gift.goblin.HayRackController.database.model.event.ScheduledShutterMovement;
import gift.goblin.HayRackController.service.sync.ScheduledShutterMovementSyncService;
import gift.goblin.HayRackController.service.sync.TemperatureSyncService;
import java.util.List;
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
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        temperatureSyncService.backupValues();
        scheduledShutterMovementSyncService.backupValues();
    }
    
}
