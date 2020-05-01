/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.database.embedded.repo.event;

import gift.goblin.HayRackController.database.model.event.ScheduledShutterMovement;
import gift.goblin.HayRackController.database.model.event.TemperatureMeasurement;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Contains basic methods for accessing the saved shutter movements from database.
 * @author andre
 */
public interface ScheduledShutterMovementRepository extends JpaRepository<ScheduledShutterMovement, Long> {
    
    Optional<TemperatureMeasurement> findTop1ByOrderByCreatedAtDesc();
    
}
