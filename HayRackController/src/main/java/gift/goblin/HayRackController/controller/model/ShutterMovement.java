/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gift.goblin.HayRackController.controller.model;

/**
 *
 * @author andre
 */
public class ShutterMovement {
    
    public static final String DIRECTION_UP = "UP";
    public static final String DIRECTION_DOWN = "DOWN";
    
    private String direction;
    private int duration;

    public ShutterMovement() {
    }

    public ShutterMovement(String direction, int duration) {
        this.direction = direction;
        this.duration = duration;
    }

    //<editor-fold defaultstate="collapsed" desc="getterSetter">
    public String getDirection() {
        return direction;
    }
    
    public void setDirection(String direction) {
        this.direction = direction;
    }
    
    public int getDuration() {
        return duration;
    }
    
    public void setDuration(int duration) {
        this.duration = duration;
    }
//</editor-fold>
    
    public boolean directionIsUp() {
        return direction.equals(DIRECTION_UP);
    }
    
    public boolean directionIsDown() {
        return direction.equals(DIRECTION_DOWN);
    }

    @Override
    public String toString() {
        return "ShutterMovement{" + "direction=" + direction + ", duration=" + duration + '}';
    }
    
}
