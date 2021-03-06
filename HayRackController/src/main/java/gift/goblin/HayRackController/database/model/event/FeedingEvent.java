/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.database.model.event;

import gift.goblin.HayRackController.database.sync.LongIdentifier;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

/**
 * Entity which stores informations for planned feeding events.
 * @author andre
 */
@Entity
@Table
public class FeedingEvent implements Serializable, LongIdentifier {

    private Long id;
    private LocalDateTime feedingStart;
    private LocalDateTime feedingEnd;
    
    /**
     * The measured weight at the start of the feeding-event.
     */
    private long weightGramStart;
    
    /**
     * The measured weight at the end of the feeding-event.
     */
    private long weightGramEnd;
    
    /**
     * The eaten food in this feeding-event.
     */
    private long foodConsumptionGram;
   
    private ScheduledShutterMovement scheduledShutterMovement;
        
    /**
     * Contains the feeding duration. Will be automatically
     * calculated when the feedingEnd was written.
     */
    private Long feedingDurationMs;

    public FeedingEvent() {
    }

    public FeedingEvent(LocalDateTime feedingStart, ScheduledShutterMovement scheduledShutterMovement) {
        this.feedingStart = feedingStart;
        this.scheduledShutterMovement = scheduledShutterMovement;
    }
    
    
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "fk_scheduled_shutter_movement")
    public ScheduledShutterMovement getScheduledShutterMovement() {
        return scheduledShutterMovement;
    }

    public void setScheduledShutterMovement(ScheduledShutterMovement scheduledShutterMovement) {
        this.scheduledShutterMovement = scheduledShutterMovement;
    }
    
    @Id
    @GenericGenerator(
        name = "assigned-sequence",
        strategy = "gift.goblin.HayRackController.database.sync.IntelligentSequenceGenerator")
    @GeneratedValue(generator = "assigned-sequence", strategy = GenerationType.SEQUENCE)
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFeedingStart() {
        return feedingStart;
    }

    public void setFeedingStart(LocalDateTime feedingStart) {
        this.feedingStart = feedingStart;
    }

    public LocalDateTime getFeedingEnd() {
        return feedingEnd;
    }

    public void setFeedingEnd(LocalDateTime feedingEnd) {
        this.feedingEnd = feedingEnd;
    }

    public Long getFeedingDurationMs() {
        return feedingDurationMs;
    }

    public void setFeedingDurationMs(Long feedingDurationMs) {
        this.feedingDurationMs = feedingDurationMs;
    }

    public long getWeightGramStart() {
        return weightGramStart;
    }

    public void setWeightGramStart(long weightGramStart) {
        this.weightGramStart = weightGramStart;
    }

    public long getWeightGramEnd() {
        return weightGramEnd;
    }

    public void setWeightGramEnd(long weightGramEnd) {
        this.weightGramEnd = weightGramEnd;
    }

    public long getFoodConsumptionGram() {
        return foodConsumptionGram;
    }

    public void setFoodConsumptionGram(long foodConsumptionGram) {
        this.foodConsumptionGram = foodConsumptionGram;
    }
    
    

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.id);
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
        final FeedingEvent other = (FeedingEvent) obj;
        if (this.weightGramStart != other.weightGramStart) {
            return false;
        }
        if (this.weightGramEnd != other.weightGramEnd) {
            return false;
        }
        if (this.foodConsumptionGram != other.foodConsumptionGram) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.feedingStart, other.feedingStart)) {
            return false;
        }
        if (!Objects.equals(this.feedingEnd, other.feedingEnd)) {
            return false;
        }
        if (!Objects.equals(this.feedingDurationMs, other.feedingDurationMs)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "FeedingEvent{" + "feedingEventId=" + id + ", feedingStart=" + feedingStart + ", feedingEnd=" + feedingEnd + ", weightGramStart=" + weightGramStart + ", weightGramEnd=" + weightGramEnd + ", foodConsumptionGram=" + foodConsumptionGram + ", feedingDurationMs=" + feedingDurationMs + '}';
    }

}
