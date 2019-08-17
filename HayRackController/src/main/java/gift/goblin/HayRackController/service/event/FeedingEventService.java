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
}
