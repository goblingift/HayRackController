/*
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.database.model.weight;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * This class represents a measurement of the tare-value for the connected
 * load-cells. These values will be used for calculating the calculated amount
 * of hay in the hayrack.
 *
 * @author andre
 */
@Entity
@Table
public class TareMeasurement implements Serializable {

    private Long tareMeasurementId;

    private long tareLoadCell1;
    private long tareLoadCell2;
    private long tareLoadCell3;
    private long tareLoadCell4;
    private LocalDateTime measuredAt;

    public TareMeasurement() {
    }

    public TareMeasurement(long tareLoadCell1, long tareLoadCell2, long tareLoadCell3, long tareLoadCell4, LocalDateTime measuredAt) {
        this.tareLoadCell1 = tareLoadCell1;
        this.tareLoadCell2 = tareLoadCell2;
        this.tareLoadCell3 = tareLoadCell3;
        this.tareLoadCell4 = tareLoadCell4;
        this.measuredAt = measuredAt;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getTareMeasurementId() {
        return tareMeasurementId;
    }

    public void setTareMeasurementId(Long tareMeasurementId) {
        this.tareMeasurementId = tareMeasurementId;
    }

    public long getTareLoadCell1() {
        return tareLoadCell1;
    }

    public void setTareLoadCell1(long tareLoadCell1) {
        this.tareLoadCell1 = tareLoadCell1;
    }

    public long getTareLoadCell2() {
        return tareLoadCell2;
    }

    public void setTareLoadCell2(long tareLoadCell2) {
        this.tareLoadCell2 = tareLoadCell2;
    }

    public long getTareLoadCell3() {
        return tareLoadCell3;
    }

    public void setTareLoadCell3(long tareLoadCell3) {
        this.tareLoadCell3 = tareLoadCell3;
    }

    public long getTareLoadCell4() {
        return tareLoadCell4;
    }

    public void setTareLoadCell4(long tareLoadCell4) {
        this.tareLoadCell4 = tareLoadCell4;
    }

    public LocalDateTime getMeasuredAt() {
        return measuredAt;
    }

    public void setMeasuredAt(LocalDateTime measuredAt) {
        this.measuredAt = measuredAt;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.measuredAt);
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
        final TareMeasurement other = (TareMeasurement) obj;
        if (this.tareLoadCell1 != other.tareLoadCell1) {
            return false;
        }
        if (this.tareLoadCell2 != other.tareLoadCell2) {
            return false;
        }
        if (this.tareLoadCell3 != other.tareLoadCell3) {
            return false;
        }
        if (this.tareLoadCell4 != other.tareLoadCell4) {
            return false;
        }
        if (!Objects.equals(this.tareMeasurementId, other.tareMeasurementId)) {
            return false;
        }
        if (!Objects.equals(this.measuredAt, other.measuredAt)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TareMeasurement{" + "temperateMeasurementId=" + tareMeasurementId + ", tareLoadCell1=" + tareLoadCell1 + ", tareLoadCell2=" + tareLoadCell2 + ", tareLoadCell3=" + tareLoadCell3 + ", tareLoadCell4=" + tareLoadCell4 + ", measuredAt=" + measuredAt + '}';
    }

}
