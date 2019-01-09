/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gift.goblin.HayRackController.database.event;

import gift.goblin.HayRackController.database.event.model.TemperatureMeasurement;
import gift.goblin.HayRackController.database.event.repo.TemperatureMeasurementRepository;
import gift.goblin.HayRackController.service.io.dto.TemperatureAndHumidity;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Service bean which implements several methods regarding the temperature and
 * humidity measurement.
 * @author andre
 */
public class TemperatureMeasurementServiceImpl implements TemperatureMeasurementService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    TemperatureMeasurementRepository temperatureMeasurementRepository;
    
    @Override
    public void saveTemperatureMeasurement(TemperatureAndHumidity tempAndHumidity) {
        
        TemperatureMeasurement temperatureMeasurement = new TemperatureMeasurement(tempAndHumidity.getTemperature(), tempAndHumidity.getTemperatureFahrenheit(),
                tempAndHumidity.getHumidity(), LocalDateTime.now());
        
        TemperatureMeasurement savedEntity = temperatureMeasurementRepository.save(temperatureMeasurement);
        logger.info("Temperature-Measurement: Successful created entry: {}", savedEntity);
    }
    
}
