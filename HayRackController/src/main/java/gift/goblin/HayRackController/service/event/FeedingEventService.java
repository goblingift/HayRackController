/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.service.event;

import gift.goblin.HayRackController.database.model.event.ScheduledShutterMovement;
import java.time.LocalDateTime;

/**
 *
 * @author andre
 */
public interface FeedingEventService {

    /**
     * Create a new feeding event entry in database.
     *
     * @return the primary key of the created entity.
     */
    Long addNewFeedingEvent(int jobId);

    /**
     * Searches the latest feeding event entity for the given jobId and set the
     * enddate to current date/time.
     *
     * @return the primary key of the feeding entry entity.
     */
    Long finishFeedingEvent(int jobId);
    
    /**
     * Measures the start-weight and saves the value to the given feedingEvent-entity.
     * @param feedingEntryId PK of feedingEvent-entity.
     */
    void measureStartWeight(Long feedingEntryId);
    
    /**
     * Measures the end-weight and saves the value to the given feedingEvent-entity.
     * Also calculates the amount of eaten food while this feedingEvent.
     * @param feedingEntryId PK of feedingEvent-entity.
     */
    void measureEndWeight(Long feedingEntryId);
    
}
