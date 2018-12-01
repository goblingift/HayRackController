/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gift.goblin.HayRackController.database.event;

import gift.goblin.HayRackController.database.event.model.ScheduledShutterMovement;
import java.time.LocalDateTime;

/**
 *
 * @author andre
 */
public interface FeedingEventService {
    
    /**
     * Create a new feeding event entry in database.
     * @param feedingStart the start-time for the feeding event.
     * @return the primary key of the created entity.
     */
    Long addNewFeedingEvent(LocalDateTime feedingStart, ScheduledShutterMovement scheduledShutterMovement);
    
    /**
     * Searches the latest feeding event entity for the given scheduled shutter movement
     * entry and set the enddate to current date/time.
     * @param scheduledShutterMovement the scheduled shutter movement entity.
     * @return the primary key of the feeding entry entity.
     */
    Long finishFeedingEvent(ScheduledShutterMovement scheduledShutterMovement);
    
}
