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
    public void saveTemperatureMeasurement(TemperatureAndHumidity tempAndHumidity) {
        
        TemperatureMeasurement temperatureMeasurement = new TemperatureMeasurement(tempAndHumidity.getTemperature(), tempAndHumidity.getTemperatureFahrenheit(),
                tempAndHumidity.getHumidity(), LocalDateTime.now());
        
        TemperatureMeasurement savedEntity = repo.save(temperatureMeasurement);
        logger.info("Temperature-Measurement: Successful created entry: {}", savedEntity);
    }

    @Override
    public TemperatureAndHumidity getLatestMeasurement() {
        
        List<TemperatureMeasurement> resultList = repo.findTop1ByOrderByMeasuredAtDesc();
        TemperatureMeasurement firstResult = resultList.get(resultList.size()-1);
        logger.debug("Result after calling findTop1ByOrderByMeasuredAtDesc: {}", firstResult);
        
        TemperatureAndHumidity returnValue = new TemperatureAndHumidity(firstResult.getTemperature(), firstResult.getTemperatureFahrenheit(), firstResult.getHumidity());
        
        return returnValue;
    }

    @Override
    public List<TemperatureMeasurement> getTemperatureMeasurements(LocalDate startDate, LocalDate endDate) {
        
        List<TemperatureMeasurement> result = repo.findByMeasuredAtBetween(startDate.atStartOfDay(), endDate.atStartOfDay());
        logger.info("Result of calling findByMeasuredAtAfterAndMeasuredAtBefore: {}", result);
        
        return result;
    }
    
}
