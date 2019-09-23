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
     * Starts the maintenance mode, sets internal variables.
     * Please note: Call triggerRelayLightMaintenance() also!
     */
    void startMaintenanceMode();
    
    /**
     * Stops the maintenance mode.
     * Please note: Call triggerRelayLightMaintenance() also!
     */
    void endMaintenanceMode();
    
    /**
     * Power on or off the maintenance lights.
     * @param turnOn true if you wanna light it up, false if otherwise.
     */
    void triggerRelayLightMaintenance(boolean turnOn);
}
