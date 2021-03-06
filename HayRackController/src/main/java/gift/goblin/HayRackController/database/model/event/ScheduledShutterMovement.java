/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.database.model.event;

import gift.goblin.HayRackController.database.sync.LongIdentifier;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

/**
 * Entity for a scheduled shutter movement, which only defines the time and
 * duration how long the shutter will be opened. This entity will be generated
 * once- if you want to look at a specific event, look at the FeedingEvent
 * entity.
 *
 * @author andre
 */
@Entity
@Table(name = "scheduled_shutter_movement")
public class ScheduledShutterMovement implements Comparable<ScheduledShutterMovement>, Serializable, LongIdentifier {

    private Long id;
    private LocalTime feedingStartTime;
    /**
     * Feeding duration in minutes.
     */
    private Integer feedingDuration;
    private String createdBy;
    private LocalDateTime createdAt;

    private List<FeedingEvent> feedingEvents;

    public ScheduledShutterMovement() {
    }

    public ScheduledShutterMovement(LocalTime feedingStartTime, Integer feedingDuration) {
        this.feedingStartTime = feedingStartTime;
        this.feedingDuration = feedingDuration;
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

    public LocalTime getFeedingStartTime() {
        return feedingStartTime;
    }

    public void setFeedingStartTime(LocalTime feedingStartTime) {
        this.feedingStartTime = feedingStartTime;
    }

    public Integer getFeedingDuration() {
        return feedingDuration;
    }

    public void setFeedingDuration(Integer feedingDuration) {
        this.feedingDuration = feedingDuration;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "scheduledShutterMovement", targetEntity = FeedingEvent.class)
    @ElementCollection(targetClass = FeedingEvent.class)
    public List<FeedingEvent> getFeedingEvents() {
        return feedingEvents;
    }

    public void setFeedingEvents(List<FeedingEvent> feedingEvents) {
        this.feedingEvents = feedingEvents;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.feedingStartTime);
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
        final ScheduledShutterMovement other = (ScheduledShutterMovement) obj;
        if (!Objects.equals(this.feedingStartTime, other.feedingStartTime)) {
            return false;
        }
        if (!Objects.equals(this.feedingDuration, other.feedingDuration)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ScheduledShutterMovement{" + "id=" + id + ", feedingStartTime=" + feedingStartTime + ", feedingDuration=" + feedingDuration + ", createdBy=" + createdBy + ", createdAt=" + createdAt + '}';
    }

    @Override
    public int compareTo(ScheduledShutterMovement o) {
        return this.getFeedingStartTime().isBefore(o.getFeedingStartTime()) ? -1 : 1;
    }

}
