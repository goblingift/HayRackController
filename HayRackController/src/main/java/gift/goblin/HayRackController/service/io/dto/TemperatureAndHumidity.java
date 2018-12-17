/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gift.goblin.HayRackController.service.io.dto;

import gift.goblin.HayRackController.service.io.TempSensorReader;
import java.util.Map;

/**
 * Dto which contains temperature and humidity values.
 * @author andre
 */
public class TemperatureAndHumidity {
    
    private float temperature;
    private float temperatureFahrenheit;
    private float humidity;
    private int readAttempts;

    public TemperatureAndHumidity() {
    }

    public TemperatureAndHumidity(float temperature, float temperatureFahrenheit, float humidity, int readAttempts) {
        this.temperature = temperature;
        this.temperatureFahrenheit = temperatureFahrenheit;
        this.humidity = humidity;
        this.readAttempts = readAttempts;
    }
    
    /**
     * Creates a dto object from a map which includes the required fields in there.
     * @param map map which includes the following keys: temp, tempFahrenheit, humidity.
     */
    public TemperatureAndHumidity(Map<String, Float> map) {
        
        this.temperature = map.getOrDefault(TempSensorReader.KEY_TEMPERATURE, 0.0F);
        this.temperatureFahrenheit = map.getOrDefault(TempSensorReader.KEY_TEMPERATURE_FAHRENHEIT, 0.0F);
        this.humidity = map.getOrDefault(TempSensorReader.KEY_HUMIDITY, 0.0F);
        this.readAttempts = Float.valueOf(map.getOrDefault("attempts", 0.0F)).intValue();
    }

    //<editor-fold defaultstate="collapsed" desc="getterSetter">
    public float getTemperature() {
        return temperature;
    }
    
    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }
    
    public float getTemperatureFahrenheit() {
        return temperatureFahrenheit;
    }
    
    public void setTemperatureFahrenheit(float temperatureFahrenheit) {
        this.temperatureFahrenheit = temperatureFahrenheit;
    }
    
    public float getHumidity() {
        return humidity;
    }
    
    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }
//</editor-fold>

    @Override
    public String toString() {
        return "TemperatureAndHumidity{" + "temperature=" + temperature + ", temperatureFahrenheit=" + temperatureFahrenheit + ", humidity=" + humidity + '}';
    }
    
}
