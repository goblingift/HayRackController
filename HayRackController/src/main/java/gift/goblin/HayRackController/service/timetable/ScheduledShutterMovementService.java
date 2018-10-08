/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gift.goblin.HayRackController.service.timetable;

import gift.goblin.HayRackController.database.security.model.ScheduledShutterMovement;
import java.time.LocalTime;
import java.util.List;

/**
 * Service bean to add and access the scheduled shutter movements.
 * @author andre
 */
public interface ScheduledShutterMovementService {
    
    /**
     * Adds a new planned shutter movement, which will be used by the cronjob.
     * @param openAt time, when the shutters should get opened.
     * @param closeAt time, when the shutters should get closed.
     * @param comment comment of the user, which created this schedule, e.g. 'lunch'.
     */
    void addNewShutterMovement(LocalTime openAt, LocalTime closeAt, String comment);
    
    /**
     * Reads all stored planned shutter movement.
     * Default order is from earliest to latest movement.
     * @return sorted list with all scheduled shutter movements.
     */
    List<ScheduledShutterMovement> readAllStoredShutterMovementSchedules();
    
}