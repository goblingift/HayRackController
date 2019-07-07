/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.database.backup.repo.event;

import gift.goblin.HayRackController.database.model.event.TemperatureDailyMaxMin;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author andre
 */
public interface TemperatureDailyMaxMinBackupRepository extends JpaRepository<TemperatureDailyMaxMin, Long> {
    
    Optional<TemperatureDailyMaxMin> findByDate(LocalDate localDate);
    
    List<TemperatureDailyMaxMin> findByDateBetween(LocalDate firstDate, LocalDate lastDate);
    
}
