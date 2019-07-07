/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.database.model.event;

import java.time.LocalDate;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Contains the daily temperature max and min values.
 * @author andre
 */
@Entity
@Table
public class TemperatureDailyMaxMin {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @OneToOne(optional = false)
    @JoinColumn(name = "fk_temperateMeasurementId_min", nullable = false)
    private TemperatureMeasurement min;
    
    @OneToOne(optional = false)
    @JoinColumn(name = "fk_temperateMeasurementId_max", nullable = false)
    private TemperatureMeasurement max;
    
    private LocalDate date;

    public TemperatureDailyMaxMin() {
    }

    public TemperatureDailyMaxMin(LocalDate date) {
        this.date = date;
    }
    
    public TemperatureDailyMaxMin(TemperatureMeasurement min, TemperatureMeasurement max, LocalDate date) {
        this.min = min;
        this.max = max;
        this.date = date;
    }
    
    //<editor-fold defaultstate="collapsed" desc="getterSetter">
    public TemperatureMeasurement getMin() {
        return min;
    }
    
    public void setMin(TemperatureMeasurement min) {
        this.min = min;
    }
    
    public TemperatureMeasurement getMax() {
        return max;
    }
    
    public void setMax(TemperatureMeasurement max) {
        this.max = max;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
//</editor-fold>

    @Override
    public int hashCode() {
        int hash = 7;
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
        final TemperatureDailyMaxMin other = (TemperatureDailyMaxMin) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.min, other.min)) {
            return false;
        }
        if (!Objects.equals(this.max, other.max)) {
            return false;
        }
        if (!Objects.equals(this.date, other.date)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TemperatureDailyMaxMin{" + "id=" + id + ", min=" + min + ", max=" + max + ", date=" + date + '}';
    }
    
}
