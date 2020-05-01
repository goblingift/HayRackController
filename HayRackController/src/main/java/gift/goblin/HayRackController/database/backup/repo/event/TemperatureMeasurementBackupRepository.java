/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
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
    
    List<TemperatureMeasurement> findByMeasuredAtAfter(LocalDateTime after);
    
    Optional<TemperatureMeasurement> findByTemperatureAndMeasuredAt(float temperature, LocalDateTime ldt);
    
}
