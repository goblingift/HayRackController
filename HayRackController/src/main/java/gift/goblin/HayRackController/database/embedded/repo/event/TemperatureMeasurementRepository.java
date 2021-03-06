/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.database.embedded.repo.event;

import gift.goblin.HayRackController.database.model.event.TemperatureMeasurement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author andre
 */
public interface TemperatureMeasurementRepository extends JpaRepository<TemperatureMeasurement, Long>{
    
    Optional<TemperatureMeasurement> findTop1ByOrderByMeasuredAtDesc();
    
    List<TemperatureMeasurement> findByMeasuredAtBetween(LocalDateTime after, LocalDateTime before);
    
    List<TemperatureMeasurement> findByMeasuredAtAfter(LocalDateTime after); 
    
    List<TemperatureMeasurement> findByTemperatureAndMeasuredAt(float temperature, LocalDateTime ldt);
}
