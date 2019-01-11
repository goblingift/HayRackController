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
        
        // null-check: Will only be null, if executed in NO-RASPBERRY environment
        if (optTempAndHumidity == null) {
            logger.info("Temperature measurement returns null value- fake data, cause its no RASPBERRY-machine.");
            
            TemperatureAndHumidity fakeData = new TemperatureAndHumidity(36.0F, 96.8F, 8.5F);
            temperatureMeasurementService.saveTemperatureMeasurement(fakeData);
        } else {
            if (optTempAndHumidity.isPresent()) {
                temperatureMeasurementService.saveTemperatureMeasurement(optTempAndHumidity.get());
            } else {
                logger.error("Couldnt read temperature and humidity- wont save any values to database!");
            }
        }
        
    }
    
}
