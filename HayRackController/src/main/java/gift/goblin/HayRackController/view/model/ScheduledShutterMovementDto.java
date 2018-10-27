/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gift.goblin.HayRackController.view.model;

/**
 * Form dto, which contains information about scheduled shutter movements
 * from controller to view.
 * @author andre
 */
public class ScheduledShutterMovementDto {
    
    private String id;
    private boolean isActive;
    private String feedingStartTime;
    /**
     * Feeding duration in minutes.
     */
    private String feedingDuration;
    private String comment;
    private String createdBy;
    private String createdAt;

    public ScheduledShutterMovementDto() {
    }

    public ScheduledShutterMovementDto(String feedingStartTime, String feedingDuration, String comment) {
        this.feedingStartTime = feedingStartTime;
        this.feedingDuration = feedingDuration;
        this.comment = comment;
    }

    public ScheduledShutterMovementDto(String id, boolean isActive, String feedingStartTime, String feedingDuration, String comment, String createdBy, String createdAt) {
        this.id = id;
        this.isActive = isActive;
        this.feedingStartTime = feedingStartTime;
        this.feedingDuration = feedingDuration;
        this.comment = comment;
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
   
    public boolean isIsActive() {
        return isActive;
    }
    
    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
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
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
//</editor-fold>

    @Override
    public String toString() {
        return "ScheduledShutterMovementDto{" + "id=" + id + ", isActive=" + isActive + ", feedingStartTime=" + feedingStartTime + ", feedingDuration=" + feedingDuration + ", comment=" + comment + ", createdBy=" + createdBy + ", createdAt=" + createdAt + '}';
    }

}
