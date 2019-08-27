/*
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.service.io.interfaces;

import gift.goblin.HayRackController.service.io.ApplicationState;

/**
 * Interface which contains required methods to enter or exit maintenance mode.
 * @author andre
 */
public interface MaintenanceManager {
    
    /**
     * Get the application state of the application.
     * @return the enum of the state, this could be MAINTENANCE or DEFAULT.
     */
    ApplicationState getApplicationState();
    
    /**
     * Checks if the maintenance mode is currently activated.
     * @return true if maintenance is active, false if otherwise.
     */
    boolean isMaintenanceModeActive();
    
    /**
     * Starts the maintenance mode.
     * Will light up the maintenance lights and prevent upcoming
     * feeding-events to get executed.
     */
    void startMaintenanceMode();
    
    /**
     * Stops the maintenance mode.
     */
    void endMaintenanceMode();
}
