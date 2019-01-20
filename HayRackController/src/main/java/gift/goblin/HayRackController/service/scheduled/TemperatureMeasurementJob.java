/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gift.goblin.HayRackController.service.scheduled;

import gift.goblin.HayRackController.database.event.TemperatureDailyMaxMinService;
import gift.goblin.HayRackController.database.event.TemperatureMeasurementService;
import gift.goblin.HayRackController.database.event.model.TemperatureMeasurement;
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
 * Scheduled job, which measures temp sensors, logs the results and triggers
 * following actions.
 *
 * @author andre
 */
@Component
public class TemperatureMeasurementJob implements Job {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    IOController iOController;

    @Autowired
    TemperatureMeasurementService temperatureMeasurementService;
    
    @Autowired
    TemperatureDailyMaxMinService temperatureDailyMaxMinService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        Optional<TemperatureAndHumidity> measuredResult = iOController.getTempAndHumidity();
        TemperatureAndHumidity temperatureAndHumidity = null;

        // null-check: Will only be null, if executed in NO-RASPBERRY environment
        if (measuredResult == null) {
            logger.info("Temperature measurement returns null value- fake data, cause its no RASPBERRY-machine.");
            temperatureAndHumidity = new TemperatureAndHumidity(99.0F, 210.2F, 99.0F);
        } else {
            temperatureAndHumidity = measuredResult.get();
        }
        
        TemperatureMeasurement savedTemperature = temperatureMeasurementService.saveTemperatureMeasurement(temperatureAndHumidity);
        temperatureDailyMaxMinService.tryToStoreTemperature(savedTemperature);
    }

}
