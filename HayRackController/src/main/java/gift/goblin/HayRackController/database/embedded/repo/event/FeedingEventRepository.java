/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.database.embedded.repo.event;

import gift.goblin.HayRackController.database.model.event.FeedingEvent;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Contains basic methods for accessing the feeding events from database.
 * @author andre
 */
public interface FeedingEventRepository extends JpaRepository<FeedingEvent, Long> {
    
}
