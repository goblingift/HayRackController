/*
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.service.io.trigger;

import com.pi4j.io.gpio.GpioPinDigitalInput;
import gift.goblin.HayRackController.service.io.ApplicationState;
import gift.goblin.HayRackController.service.io.interfaces.MaintenanceManager;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author andre
 */
public class MaintenanceTrigger extends AbstractTrigger implements Callable<Void> {

    private MaintenanceManager maintenanceManager;
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public MaintenanceTrigger(MaintenanceManager maintenanceManager, GpioPinDigitalInput pinButtonMaintenance) {
        super(pinButtonMaintenance);
        
        this.maintenanceManager = this.maintenanceManager;
    }

    @Override
    public Void call() throws Exception {

        logger.info("Maintenance button pressed");
        
        boolean longPressed = buttonWasPressed(3_000);

        if (longPressed && maintenanceManager.getApplicationState() == ApplicationState.DEFAULT) {
            maintenanceManager.startMaintenanceMode();
        } else if (longPressed && maintenanceManager.getApplicationState() == ApplicationState.MAINTENANCE) {
            maintenanceManager.endMaintenanceMode();
        }
        
        logger.info("Maintenance button released");
        return null;
    }

}
