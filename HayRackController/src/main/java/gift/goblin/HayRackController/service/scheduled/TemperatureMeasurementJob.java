/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gift.goblin.HayRackController.service.scheduled;

import gift.goblin.HayRackController.database.event.TemperatureMeasurementService;
import gift.goblin.HayRackController.service.io.IOController;
import gift.goblin.HayRackController.service.io.dto.TemperatureAndHumidity;
import java.util.Optional;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Scheduled job, which measures temp sensors, logs the results
 * and triggers following actions.
 * @author andre
 */
@Component
public class TemperatureMeasurementJob implements Job {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    IOController iOController;
    
    @Autowired
    TemperatureMeasurementService temperatureMeasurementService;
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        logger.debug("Execution of temperature measurement job triggered.");
        
        Optional<TemperatureAndHumidity> optTempAndHumidity = iOController.measureTempAndHumidity();
        if (optTempAndHumidity.isPresent()) {
            temperatureMeasurementService.saveTemperatureMeasurement(optTempAndHumidity.get());
        } else {
            logger.error("Couldnt read temperature and humidity- wont save any values to database!");
        }
    }
    
    /**
     * Measure temperature and humidity, store the data into the
     * database and trigger following tasks.
     */
    private void handleTempSensor() {
        
        Optional<TemperatureAndHumidity> optTempAndHumidity = iOController.measureTempAndHumidity();
        
        if (optTempAndHumidity.isPresent()) {
            
        }
        
    }
    
    /**
     * Measures the brightness and does following tasks.
     * So if its feeding time and its dark, power on the indoor-lights.
     */
    private void handleBrightnessSensor() {
        
        boolean daylightDetected = iOController.daylightDetected();
        
        // todo
        
    }
    
    
    
    
}
