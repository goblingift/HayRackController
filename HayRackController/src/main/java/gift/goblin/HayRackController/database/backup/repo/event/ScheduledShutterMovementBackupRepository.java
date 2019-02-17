/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gift.goblin.HayRackController.database.backup.repo.event;

import gift.goblin.HayRackController.database.embedded.repo.event.*;
import gift.goblin.HayRackController.database.model.event.ScheduledShutterMovement;
import gift.goblin.HayRackController.database.model.event.TemperatureMeasurement;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Contains basic methods for accessing the saved shutter movements from database.
 * @author andre
 */
public interface ScheduledShutterMovementBackupRepository extends JpaRepository<ScheduledShutterMovement, Long> {
    
    Optional<TemperatureMeasurement> findTop1ByOrderByCreatedAtDesc();
    
}
