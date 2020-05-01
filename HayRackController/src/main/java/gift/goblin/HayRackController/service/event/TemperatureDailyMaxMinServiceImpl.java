/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.service.event;

import gift.goblin.HayRackController.database.model.event.TemperatureDailyMaxMin;
import gift.goblin.HayRackController.database.model.event.TemperatureMeasurement;
import gift.goblin.HayRackController.database.embedded.repo.event.TemperatureDailyMaxMinRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author andre
 */
@Component
public class TemperatureDailyMaxMinServiceImpl implements TemperatureDailyMaxMinService {

    @Autowired
    TemperatureDailyMaxMinRepository repo;

    @Override
    public float getMaxTemperature(LocalDate localDate) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public float getMinTemperature(LocalDate localDate) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<TemperatureMeasurement> getMaxTemperatures(LocalDate from, LocalDate to) {
        return getDailyValues(from, to, true);
    }
    
    @Override
    public List<TemperatureMeasurement> getMinTemperatures(LocalDate from, LocalDate to) {
        return getDailyValues(from, to, false);
    }
    
    private List<TemperatureMeasurement> getDailyValues(LocalDate from, LocalDate to, boolean findMaxValues) {
        
        List<TemperatureDailyMaxMin> temperatureMeasurements = repo.findByDateBetween(from, to);
        
        List<TemperatureMeasurement> results = temperatureMeasurements.stream()
                .map(td -> findMaxValues ? td.getMax() : td.getMin())
                .collect(Collectors.toList());
        
        return results;
    }

    @Override
    public void tryToStoreTemperature(TemperatureMeasurement temperatureAndHumidity) {
        
        Optional<TemperatureDailyMaxMin> optDailyEntry = repo.findByDate(LocalDate.now());
        TemperatureDailyMaxMin dailyEntry;
        
        // If no entry was found from today, create a new one
        if (!optDailyEntry.isPresent()) {
            dailyEntry = new TemperatureDailyMaxMin(temperatureAndHumidity, temperatureAndHumidity, LocalDate.now());
        } else {
            dailyEntry = optDailyEntry.get();
            float oldTemperature = dailyEntry.getMin().getTemperature();
            
            if (temperatureAndHumidity.getTemperature() > oldTemperature) {
                dailyEntry.setMax(temperatureAndHumidity);
            }
            if (temperatureAndHumidity.getTemperature() < oldTemperature) {
                dailyEntry.setMin(temperatureAndHumidity);
            }
        }
        repo.saveAndFlush(dailyEntry);
    }
    
}
