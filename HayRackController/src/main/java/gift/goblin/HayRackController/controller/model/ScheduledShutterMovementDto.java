/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.controller.model;

/**
 * Form dto, which contains information about scheduled shutter movements
 * from controller to view.
 * @author andre
 */
public class ScheduledShutterMovementDto {
    
    private String id;
    private String feedingStartTime;
    /**
     * Feeding duration in minutes.
     */
    private String feedingDuration;
    private String createdBy;
    private String createdAt;

    public ScheduledShutterMovementDto() {
    }

    public ScheduledShutterMovementDto(String feedingStartTime, String feedingDuration) {
        this.feedingStartTime = feedingStartTime;
        this.feedingDuration = feedingDuration;
    }

    public ScheduledShutterMovementDto(String id, String feedingStartTime, String feedingDuration, String createdBy, String createdAt) {
        this.id = id;
        this.feedingStartTime = feedingStartTime;
        this.feedingDuration = feedingDuration;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

//<editor-fold defaultstate="collapsed" desc="getterSetter">

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
   
    public String getFeedingStartTime() {
        return feedingStartTime;
    }

    public void setFeedingStartTime(String feedingStartTime) {
        this.feedingStartTime = feedingStartTime;
    }
    
    public String getFeedingDuration() {
        return feedingDuration;
    }
    
    public void setFeedingDuration(String feedingDuration) {
        this.feedingDuration = feedingDuration;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
//</editor-fold>

    @Override
    public String toString() {
        return "ScheduledShutterMovementDto{" + "id=" + id + ", feedingStartTime=" + feedingStartTime + ", feedingDuration=" + feedingDuration + ", createdBy=" + createdBy + ", createdAt=" + createdAt + '}';
    }

}
