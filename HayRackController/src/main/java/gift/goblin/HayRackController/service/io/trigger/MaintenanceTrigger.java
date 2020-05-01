/*
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.service.io.trigger;

import com.pi4j.io.gpio.GpioPinDigitalInput;
import gift.goblin.HayRackController.service.io.ApplicationState;
import gift.goblin.HayRackController.service.io.interfaces.MaintenanceManager;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;
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
        
        this.maintenanceManager = maintenanceManager;
    }

    /**
     * If the button was pressed for at least 3seconds, enter or exit maintenance mode.
     * @return
     * @throws Exception 
     */
    @Override
    public Void call() throws Exception {
        
        if (buttonWasPressed(3_000)) {
            logger.info("button maintenance hold for 3s");
            
            if (maintenanceManager.getApplicationState() == ApplicationState.DEFAULT) {
                maintenanceManager.startMaintenanceMode();
                maintenanceManager.triggerRelayLightMaintenance(true);
            } else if (maintenanceManager.getApplicationState() == ApplicationState.MAINTENANCE) {
                maintenanceManager.endMaintenanceMode();
                maintenanceManager.triggerRelayLightMaintenance(false);
            }
            
        }
        return null;
    }

}
