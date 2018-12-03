/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gift.goblin.HayRackController.database.event.repo;

import gift.goblin.HayRackController.database.event.model.ScheduledShutterMovement;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Contains basic methods for accessing the saved shutter movements from database.
 * @author andre
 */
public interface ScheduledShutterMovementRepository extends JpaRepository<ScheduledShutterMovement, Long> {
    
}
