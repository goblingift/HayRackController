/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.service.event;

import gift.goblin.HayRackController.database.model.event.TemperatureMeasurement;
import gift.goblin.HayRackController.service.io.dto.TemperatureAndHumidity;
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
    
    /**
     * Compares the measured temperature with the daily min and max temperature values
     * and try to override them with the new measurement, if its a new high/low value.
     * Implicite takes the current LocalDate as measure-date.
     * @param temperatureAndHumidity The measured temperature entry.
     */
    void tryToStoreTemperature(TemperatureMeasurement temperatureAndHumidity);
}
