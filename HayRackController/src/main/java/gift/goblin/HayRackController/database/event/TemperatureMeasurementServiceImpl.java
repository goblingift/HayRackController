/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gift.goblin.HayRackController.database.event;

import gift.goblin.HayRackController.controller.dto.CalendarEvent;
import gift.goblin.HayRackController.database.event.model.TemperatureMeasurement;
import gift.goblin.HayRackController.database.event.repo.TemperatureMeasurementRepository;
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
