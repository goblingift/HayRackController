/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.service.event;

import gift.goblin.HayRackController.database.model.event.ScheduledShutterMovement;
import java.time.LocalTime;
import java.util.List;

/**
 * Service bean to add and access the scheduled shutter movement entities.
 * @author andre
 */
public interface ScheduledShutterMovementService {
    
    /**
     * Adds a new planned shutter movement, which will be used by the cronjob.
     * @param openAt time, when the shutters should get opened.
     * @param feedingDuration duration of the feeding time in minutes.
     * @return the id of the created entity
     */
    Long addNewShutterMovement(LocalTime openAt, Integer feedingDuration);
    
    /**
     * Reads all stored planned shutter movement.
     * Default order is from earliest to latest movement.
     * @return sorted list with all scheduled shutter movements.
     */
    List<ScheduledShutterMovement> readAllStoredShutterMovementSchedules();
    
    /**
     * Delete a scheduled movement from database.
     * @param id the PK of this entry.
     */
    void deleteScheduledMovement(Long id);
    
}