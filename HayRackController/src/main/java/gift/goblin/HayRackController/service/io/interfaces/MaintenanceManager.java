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
    
    ApplicationState getApplicationState();
    
    void startMaintenanceMode();
    
    void endMaintenanceMode();
}
