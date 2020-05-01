/*
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.database.backup.repo.event;

import gift.goblin.HayRackController.database.model.event.FeedingEvent;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Backup repository for FeedingEvent-entities.
 * @author andre
 */
public interface FeedingEventBackupRepository extends JpaRepository<FeedingEvent, Long>{
    
}
