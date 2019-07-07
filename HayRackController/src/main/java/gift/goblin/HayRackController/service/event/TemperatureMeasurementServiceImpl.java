/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.service.event;

import gift.goblin.HayRackController.controller.model.CalendarEvent;
import gift.goblin.HayRackController.database.model.event.TemperatureMeasurement;
import gift.goblin.HayRackController.database.embedded.repo.event.TemperatureMeasurementRepository;
import gift.goblin.HayRackController.service.io.dto.TemperatureAndHumidity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service bean which implements several methods regarding the temperature and
 * humidity measurement.
 * @author andre
 */
@Service
public class TemperatureMeasurementServiceImpl implements TemperatureMeasurementService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    TemperatureMeasurementRepository repo;
    
    @Override
    public TemperatureMeasurement saveTemperatureMeasurement(TemperatureAndHumidity tempAndHumidity) {
        
        TemperatureMeasurement temperatureMeasurement = new TemperatureMeasurement(tempAndHumidity.getTemperature(), tempAndHumidity.getTemperatureFahrenheit(),
                tempAndHumidity.getHumidity(), LocalDateTime.now());
        
        TemperatureMeasurement savedEntity = repo.save(temperatureMeasurement);
        logger.info("Temperature-Measurement: Successful created entry: {}", savedEntity);
        return savedEntity;
    }

    @Override
    public TemperatureAndHumidity getLatestMeasurement() {
        
        Optional<TemperatureMeasurement> optResult = repo.findTop1ByOrderByMeasuredAtDesc();
        logger.debug("Result after calling findTop1ByOrderByMeasuredAtDesc: {}", optResult);
        
        if (optResult.isPresent()) {
            TemperatureAndHumidity returnValue = new TemperatureAndHumidity(optResult.get().getTemperature(),
                    optResult.get().getTemperatureFahrenheit(), optResult.get().getHumidity());
            return returnValue;
        } else {
            return null;
        }
    }

    @Override
    public List<TemperatureMeasurement> getTemperatureMeasurements(LocalDate startDate, LocalDate endDate) {
        
        List<TemperatureMeasurement> result = repo.findByMeasuredAtBetween(startDate.atStartOfDay(), endDate.atStartOfDay());
        logger.info("Result of calling findByMeasuredAtAfterAndMeasuredAtBefore: {}", result);
        
        return result;
    }
    
}
