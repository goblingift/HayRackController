/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gift.goblin.HayRackController.database.event;

import gift.goblin.HayRackController.database.event.model.TemperatureMeasurement;
import java.time.LocalDate;
import java.util.List;

/**
 * Defines several methods regarding the daily max and min temperature values.
 * @author andre
 */
public interface TemperatureDailyMaxMinService {
    
    float getMaxTemperature(LocalDate localDate);
    
    float getMinTemperature(LocalDate localDate);
    
    List<TemperatureMeasurement> getMaxTemperatures(LocalDate from, LocalDate to);
    
    List<TemperatureMeasurement> getMinTemperatures(LocalDate from, LocalDate to);
    
}
