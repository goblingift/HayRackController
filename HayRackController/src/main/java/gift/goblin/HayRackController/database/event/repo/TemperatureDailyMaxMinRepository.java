/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gift.goblin.HayRackController.database.event.repo;

import gift.goblin.HayRackController.database.event.model.TemperatureDailyMaxMin;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author andre
 */
public interface TemperatureDailyMaxMinRepository extends JpaRepository<TemperatureDailyMaxMin, Long> {
    
    TemperatureDailyMaxMin findByDate(LocalDate localDate);
    
}
