/*
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.service.io.trigger;

import com.pi4j.io.gpio.GpioPinDigitalInput;
import gift.goblin.HayRackController.service.io.interfaces.WeightManager;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Trigger for displaying the remaining food in the hay-rack.
 * @author andre
 */
public class ShowRemainingFoodTrigger extends AbstractTrigger implements Callable<Void> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private WeightManager weightManager;
    
    public ShowRemainingFoodTrigger(WeightManager weightManager, GpioPinDigitalInput button) {
        super(button);
        
        this.weightManager = weightManager;
    }
    
    @Override
    public Void call() throws Exception {
    
        if (buttonWasPressed(3_000)) {
            long measuredWeight = weightManager.measureWeight();
            logger.info("Show remaining food: {}", measuredWeight);
        }
    
        return null;
    }
    
}
