/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gift.goblin.HayRackController.database.backup.repo.event;

import gift.goblin.HayRackController.database.model.event.TemperatureMeasurement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author andre
 */
public interface TemperatureMeasurementBackupRepository extends JpaRepository<TemperatureMeasurement, Long> {
    
    Optional<TemperatureMeasurement> findTop1ByOrderByMeasuredAtDesc();
    
    List<TemperatureMeasurement> findAllWithMeasuredAtAfter(LocalDateTime after); 
}
