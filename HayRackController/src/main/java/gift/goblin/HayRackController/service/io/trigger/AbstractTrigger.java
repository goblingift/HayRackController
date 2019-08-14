/*
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.service.io.trigger;

import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinState;

/**
 * Implements default behavior for buttons.
 * @author andre
 */
public class AbstractTrigger {
    
    private GpioPinDigitalInput button;
    
    public AbstractTrigger(GpioPinDigitalInput button) {
        this.button = button;
    }

    public GpioPinDigitalInput getButton() {
        return button;
    }
    
    /**
     * Tests if the button was pressed at least X miliseconds.
     *
     * @param mS the duration how long the button should be pressed.
     * @return true if button was hold at least the given miliseconds. False if
     * otherwise.
     */
    public boolean buttonWasPressed(int durationMs) {

        boolean holdLongEnough = false;

        // If button is pressed, start measuring how long
        if (button.getState() == PinState.HIGH) {

            long duration;
            for (duration = 0; duration < durationMs; duration += 50) {
                if (button.getState() == PinState.LOW) {
                    break;
                } else {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ex) {
                        System.out.println("ERROR! " + ex.getMessage());
                    }
                }
            }

            if (duration >= durationMs) {
                holdLongEnough = true;
            }

        }
        return holdLongEnough;

    }
    
}
