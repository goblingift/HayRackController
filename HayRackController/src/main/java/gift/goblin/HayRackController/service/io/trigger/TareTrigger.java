/*
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.service.io.trigger;

import com.pi4j.io.gpio.GpioPinDigitalInput;
import gift.goblin.HayRackController.database.embedded.repo.event.TemperatureMeasurementRepository;
import gift.goblin.HayRackController.database.embedded.repo.weight.TareMeasurementRepository;
import gift.goblin.HayRackController.database.model.event.TemperatureMeasurement;
import gift.goblin.HayRackController.database.model.weight.TareMeasurement;
import gift.goblin.HayRackController.service.io.ApplicationState;
import gift.goblin.HayRackController.service.io.WeightMeasurementService;
import gift.goblin.HayRackController.service.io.interfaces.MaintenanceManager;
import gift.goblin.HayRackController.service.io.interfaces.WeightManager;
import gift.goblin.HayRackController.util.BeanInjectionUtil;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Trigger for setting the tare of the load-cells.
 *
 * @author andre
 */
public class TareTrigger extends AbstractTrigger implements Callable<Void> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private MaintenanceManager maintenanceManager;
    private WeightManager weightManager;

    public TareTrigger(MaintenanceManager maintenanceManager, WeightManager weightManager, GpioPinDigitalInput pinButtonTare) {
        super(pinButtonTare);
        this.maintenanceManager = maintenanceManager;
        this.weightManager = weightManager;
    }

    @Override
    public Void call() throws Exception {

        WeightMeasurementService weightService = BeanInjectionUtil.getSpringBean(WeightMeasurementService.class);

        if (maintenanceManager.getApplicationState() == ApplicationState.MAINTENANCE && buttonWasPressed(3_000)) {
            weightService.measureAndSaveTare();
        }

        return null;
    }

}
