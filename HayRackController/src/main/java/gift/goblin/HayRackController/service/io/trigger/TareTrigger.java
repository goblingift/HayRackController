/*
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.service.io.trigger;

import com.pi4j.io.gpio.GpioPinDigitalInput;
import gift.goblin.HayRackController.database.embedded.repo.weight.TareMeasurementRepository;
import gift.goblin.HayRackController.database.model.weight.TareMeasurement;
import gift.goblin.HayRackController.service.io.ApplicationState;
import gift.goblin.HayRackController.service.io.interfaces.MaintenanceManager;
import gift.goblin.HayRackController.service.io.interfaces.WeightManager;
import java.time.LocalDateTime;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Trigger for setting the tare of the load-cells.
 *
 * @author andre
 */
public class TareTrigger extends AbstractTrigger implements Callable<Void> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private MaintenanceManager maintenanceManager;
    private WeightManager weightManager;

//    @Autowired
//    private TareMeasurementRepository tareRepo;

    public TareTrigger(MaintenanceManager maintenanceManager, WeightManager weightManager, GpioPinDigitalInput pinButtonTare) {
        super(pinButtonTare);

        this.maintenanceManager = maintenanceManager;
        this.weightManager = weightManager;
    }

    @Override
    public Void call() throws Exception {

        logger.info("Tare button pressed");

        boolean longPressed = buttonWasPressed(3_000);

        if (maintenanceManager.getApplicationState() == ApplicationState.MAINTENANCE) {
            
            TareMeasurement tareMeasurement = createTareEntity();
//            tareRepo.save(tareMeasurement);
            logger.info("Successful saved tare-measurement entity: {}", tareMeasurement);
            
        } else {
            logger.warn("Maintenance mode not active- cant set tare!");
        }

        logger.info("Tare button released");
        return null;
    }

    /**
     * Sets the tare of all load-cells and returns an entity with the tare
     * values.
     *
     * @return database entity.
     */
    private TareMeasurement createTareEntity() {
        long tareLoadCell1 = weightManager.setTareLoadCell1();
        logger.info("TARE of load-cell #1 set: {}", tareLoadCell1);

        long tareLoadCell2 = weightManager.setTareLoadCell2();
        logger.info("TARE of load-cell #2 set: {}", tareLoadCell2);

        long tareLoadCell3 = weightManager.setTareLoadCell3();
        logger.info("TARE of load-cell #3 set: {}", tareLoadCell3);

        long tareLoadCell4 = weightManager.setTareLoadCell4();
        logger.info("TARE of load-cell #4 set: {}", tareLoadCell4);

        TareMeasurement tareMeasurement = new TareMeasurement(tareLoadCell1,
                tareLoadCell2, tareLoadCell3, tareLoadCell4, LocalDateTime.now());
        return tareMeasurement;
    }

}
