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
    private String openAt;
    private String closeAt;
    private String comment;
    private String createdBy;
    private String createdAt;

    public ScheduledShutterMovementDto() {
    }

    public ScheduledShutterMovementDto(String id, boolean isActive, String openAt, String closeAt, String comment, String createdBy, String createdAt) {
        this.id = id;
        this.isActive = isActive;
        this.openAt = openAt;
        this.closeAt = closeAt;
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
    
    public String getOpenAt() {
        return openAt;
    }
    
    public void setOpenAt(String openAt) {
        this.openAt = openAt;
    }
    
    public String getCloseAt() {
        return closeAt;
    }
    
    public void setCloseAt(String closeAt) {
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
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
//</editor-fold>

    @Override
    public String toString() {
        return "ScheduledShutterMovementDto{" + "id=" + id + ", isActive=" + isActive + ", openAt=" + openAt + ", closeAt=" + closeAt + ", comment=" + comment + ", createdBy=" + createdBy + ", createdAt=" + createdAt + '}';
    }

    
}
