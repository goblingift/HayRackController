/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gift.goblin.HayRackController.database.event;

import java.time.LocalDate;

/**
 * Defines several methods regarding the daily max and min temperature values.
 * @author andre
 */
public interface TemperatureDailyMaxMinService {
    
    float getMaxTemperature(LocalDate localDate);
    
    float getMinTemperature(LocalDate localDate);
    
}
