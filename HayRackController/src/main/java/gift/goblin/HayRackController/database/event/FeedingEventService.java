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
     * @return the primary key of the created entity.
     */
    Long addNewFeedingEvent(int jobId);
    
    /**
     * Searches the latest feeding event entity for the given jobId
     * and set the enddate to current date/time.
     * @return the primary key of the feeding entry entity.
     */
    Long finishFeedingEvent(int jobId);
    
}
