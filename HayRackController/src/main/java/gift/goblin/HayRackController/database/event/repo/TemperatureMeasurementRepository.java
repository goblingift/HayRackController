/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gift.goblin.HayRackController.database.event.repo;

import gift.goblin.HayRackController.database.event.model.TemperatureMeasurement;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author andre
 */
public interface TemperatureMeasurementRepository extends JpaRepository<TemperatureMeasurement, Long>{
    
    List<TemperatureMeasurement> findTop1ByOrderByMeasuredAtDesc();
    
    List<TemperatureMeasurement> findByMeasuredAtAfterAndMeasuredAtBefore(LocalDateTime after, LocalDateTime before);
    
}
