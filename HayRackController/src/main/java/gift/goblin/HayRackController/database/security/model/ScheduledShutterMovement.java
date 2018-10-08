/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gift.goblin.HayRackController.database.security.model;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entity for a scheduled shutter movement.
 * @author andre
 */
@Entity
@Table(name = "ScheduledShutterMovement")
public class ScheduledShutterMovement implements Comparable<ScheduledShutterMovement>{
    
    private Long id;
    private boolean isActive;
    private LocalTime openAt;
    private LocalTime closeAt;
    private String comment;
    private String createdBy;
    private LocalDateTime createdAt;

    public ScheduledShutterMovement() {
    }

    public ScheduledShutterMovement(LocalTime openAt, LocalTime closeAt, String comment) {
        this.openAt = openAt;
        this.closeAt = closeAt;
        this.comment = comment;
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public LocalTime getOpenAt() {
        return openAt;
    }

    public void setOpenAt(LocalTime openAt) {
        this.openAt = openAt;
    }

    public LocalTime getCloseAt() {
        return closeAt;
    }

    public void setCloseAt(LocalTime closeAt) {
        this.closeAt = closeAt;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.id);
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
        if (this.isActive != other.isActive) {
            return false;
        }
        if (!Objects.equals(this.comment, other.comment)) {
            return false;
        }
        if (!Objects.equals(this.createdBy, other.createdBy)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.openAt, other.openAt)) {
            return false;
        }
        if (!Objects.equals(this.closeAt, other.closeAt)) {
            return false;
        }
        if (!Objects.equals(this.createdAt, other.createdAt)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ScheduledShutterMovement{" + "id=" + id + ", isActive=" + isActive + ", openAt=" + openAt + ", closeAt=" + closeAt + ", comment=" + comment + ", createdBy=" + createdBy + ", createdAt=" + createdAt + '}';
    }

    @Override
    public int compareTo(ScheduledShutterMovement o) {
        
        return this.getOpenAt().isBefore(o.getOpenAt()) ? -1 : 1;
    }
    
}
