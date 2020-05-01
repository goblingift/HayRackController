/*
 * Copyright (C) 2020 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.service.io;

import gift.goblin.HayRackController.aop.RequiresRaspberry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Bean which triggers and handles sound and light related methods.
 *
 * @author andre
 */
@Component
public class LightAndSoundService {

    @Autowired
    IOController ioController;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Triggers the relay to power on the light.
     *
     * @param turnOn true if you wanna turn the light on, false if otherwise.
     * @return true if execution was successful- false if otherwise.
     */
    public boolean triggerRelayLight(boolean turnOn) {

        boolean success = false;

        if (ioController.isRaspberryInitialized()) {
            if (turnOn) {
                ioController.getPinRelayLight().high();
                logger.info("Triggered relay light to: ON");
                success = true;
            } else {
                ioController.getPinRelayLight().low();
                logger.info("Triggered relay light to: OFF");
                success = true;
            }
        } else {
            logger.warn("raspberry isnt initialized- return false.");
        }

        return success;
    }

    /**
     * Evaluates if the light (Inside light) is on.
     *
     * @return true if its on, false if its off.
     */
    public boolean isLightOn() {

        if (ioController.isRaspberryInitialized()) {
            return ioController.getPinRelayLight().getState().isHigh();
        } else {
            logger.warn("raspberry isnt initialized- return false.");
            return false;
        }
    }

}
