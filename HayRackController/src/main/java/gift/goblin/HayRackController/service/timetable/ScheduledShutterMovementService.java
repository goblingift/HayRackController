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
 * Service bean to add and access the scheduled shutter movement entities.
 * @author andre
 */
public interface ScheduledShutterMovementService {
    
    /**
     * Adds a new planned shutter movement, which will be used by the cronjob.
     * @param openAt time, when the shutters should get opened.
     * @param feedingDuration duration of the feeding time in minutes.
     * @param comment comment of the user, which created this schedule, e.g. 'lunch'.
     * @return the id of the created entity
     */
    Long addNewShutterMovement(LocalTime openAt, Integer feedingDuration, String comment);
    
    /**
     * Reads all stored planned shutter movement.
     * Default order is from earliest to latest movement.
     * @return sorted list with all scheduled shutter movements.
     */
    List<ScheduledShutterMovement> readAllStoredShutterMovementSchedules();
    
    /**
     * Delete a scheduled movement from database.
     * @param id the PK of this entry.
     */
    void deleteScheduledMovement(Long id);
    
}