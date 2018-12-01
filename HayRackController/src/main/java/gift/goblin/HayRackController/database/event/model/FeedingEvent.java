/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gift.goblin.HayRackController.database.event.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Entity which stores informations for planned feeding events.
 * @author andre
 */
@Entity
@Table
public class FeedingEvent {

    private Long feedingEventId;
    private LocalDateTime feedingStart;
    private LocalDateTime feedingEnd;
   
    private ScheduledShutterMovement scheduledShutterMovement;
        
    /**
     * Contains the feeding duration. Will be automatically
     * calculated when the feedingEnd was written.
     */
    private Long feedingDurationMs;

    public FeedingEvent(LocalDateTime feedingStart, ScheduledShutterMovement scheduledShutterMovement) {
        this.feedingStart = feedingStart;
        this.scheduledShutterMovement = scheduledShutterMovement;
    }

    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_scheduled_shutter_movement")
    public ScheduledShutterMovement getScheduledShutterMovement() {
        return scheduledShutterMovement;
    }

    public void setScheduledShutterMovement(ScheduledShutterMovement scheduledShutterMovement) {
        this.scheduledShutterMovement = scheduledShutterMovement;
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getFeedingEventId() {
        return feedingEventId;
    }

    public void setFeedingEventId(Long feedingEventId) {
        this.feedingEventId = feedingEventId;
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
        setFeedingDurationMs(this.feedingStart.until(feedingEnd, ChronoUnit.MILLIS));
    }

    public Long getFeedingDurationMs() {
        return feedingDurationMs;
    }

    private void setFeedingDurationMs(Long feedingDurationMs) {
        this.feedingDurationMs = feedingDurationMs;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.feedingEventId);
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
        if (!Objects.equals(this.feedingEventId, other.feedingEventId)) {
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
        return "FeedingEvent{" + "feedingEventId=" + feedingEventId + ", feedingStart=" + feedingStart + ", feedingEnd=" + feedingEnd + ", feedingDurationMs=" + feedingDurationMs + '}';
    }
    
}
