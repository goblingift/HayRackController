/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.database.model.event;

import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entity which stores informations for measured temperature and humidity.
 *
 * @author andre
 */
@Entity
@Table
public class TemperatureMeasurement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long temperateMeasurementId;

    private float temperature;
    private float temperatureFahrenheit;
    private float humidity;
    private LocalDateTime measuredAt;

    public TemperatureMeasurement() {
    }

    public TemperatureMeasurement(float temperature, float temperatureFahrenheit, float humidity, LocalDateTime measuredAt) {
        this.temperature = temperature;
        this.temperatureFahrenheit = temperatureFahrenheit;
        this.humidity = humidity;
        this.measuredAt = measuredAt;
    }

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

    public LocalDateTime getMeasuredAt() {
        return measuredAt;
    }

    public void setMeasuredAt(LocalDateTime measuredAt) {
        this.measuredAt = measuredAt;
    }

    public Long getTemperateMeasurementId() {
        return temperateMeasurementId;
    }

    public void setTemperateMeasurementId(Long temperateMeasurementId) {
        this.temperateMeasurementId = temperateMeasurementId;
    }
    
//<editor-fold defaultstate="collapsed" desc="hashCodeEquals">
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (int) (this.temperateMeasurementId ^ (this.temperateMeasurementId >>> 32));
        return hash;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TemperatureMeasurement other = (TemperatureMeasurement) obj;
        if (this.temperateMeasurementId != other.temperateMeasurementId) {
            return false;
        }
        if (Float.floatToIntBits(this.temperature) != Float.floatToIntBits(other.temperature)) {
            return false;
        }
        if (Float.floatToIntBits(this.temperatureFahrenheit) != Float.floatToIntBits(other.temperatureFahrenheit)) {
            return false;
        }
        if (Float.floatToIntBits(this.humidity) != Float.floatToIntBits(other.humidity)) {
            return false;
        }
        if (!Objects.equals(this.measuredAt, other.measuredAt)) {
            return false;
        }
        return true;
    }
//</editor-fold>

    @Override
    public String toString() {
        return "TemperatureMeasurement{" + "temperateMeasurementId=" + temperateMeasurementId + ", temperature=" + temperature + ", temperatureFahrenheit=" + temperatureFahrenheit + ", humidity=" + humidity + ", measuredAt=" + measuredAt + '}';
    }
    
}
