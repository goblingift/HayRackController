/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.service.event;

import gift.goblin.HayRackController.controller.model.CalendarEvent;
import gift.goblin.HayRackController.database.model.event.TemperatureMeasurement;
import gift.goblin.HayRackController.service.io.dto.TemperatureAndHumidity;
import java.time.LocalDate;
import java.util.List;

/**
 * Defines several methods regarding the temperature measurements
 * @author andre
 */
public interface TemperatureMeasurementService {
    
    /**
     * Stores the temperature and humidity with an auto-generated timestamp into
     * the database.
     * @param tempAndHumidity 
     * @return the created entity.
     */
    TemperatureMeasurement saveTemperatureMeasurement(TemperatureAndHumidity tempAndHumidity);
    
    /**
     * Reads the latest temperature measurement from the database.
     * @return the latest temperature measurement.
     */
    TemperatureAndHumidity getLatestMeasurement();
    
    /**
     * Reads all temperature events from the database, which were measured
     * between the given dates.
     * @return List with all found temperature measurement events. Or empty list,
     * if none were found by the parameters.
     */
    List<TemperatureMeasurement> getTemperatureMeasurements(LocalDate startDate, LocalDate endDate);
    
}
