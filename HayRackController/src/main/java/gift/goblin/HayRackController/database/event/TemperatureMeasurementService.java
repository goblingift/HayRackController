/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gift.goblin.HayRackController.database.event;

import gift.goblin.HayRackController.service.io.dto.TemperatureAndHumidity;

/**
 * Defines several methods regarding the temperature measurements
 * @author andre
 */
public interface TemperatureMeasurementService {
    
    /**
     * Stores the temperature and humidity with an auto-generated timestamp into
     * the database.
     * @param tempAndHumidity 
     */
    void saveTemperatureMeasurement(TemperatureAndHumidity tempAndHumidity);
    
    /**
     * Reads the latest temperature measurement from the database.
     * @return the latest temperature measurement.
     */
    TemperatureAndHumidity getLatestMeasurement();
}
