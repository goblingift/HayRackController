/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gift.goblin.HayRackController.database.event;

import gift.goblin.HayRackController.database.event.model.TemperatureDailyMaxMin;
import gift.goblin.HayRackController.database.event.model.TemperatureMeasurement;
import gift.goblin.HayRackController.database.event.repo.TemperatureDailyMaxMinRepository;
import gift.goblin.HayRackController.service.io.dto.TemperatureAndHumidity;
import java.time.LocalDate;
import java.util.List;
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
        
        TemperatureDailyMaxMin dailyEntry = repo.findByDate(LocalDate.now());
        
        // If no entry was found from today, create a new one
        if (dailyEntry == null) {
            dailyEntry = new TemperatureDailyMaxMin(temperatureAndHumidity, temperatureAndHumidity, LocalDate.now());
        } else {
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
