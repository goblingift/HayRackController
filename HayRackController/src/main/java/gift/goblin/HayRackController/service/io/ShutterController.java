/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gift.goblin.HayRackController.service.io;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.trigger.GpioBlinkStateTrigger;
import com.pi4j.io.gpio.trigger.GpioBlinkStopStateTrigger;
import com.pi4j.io.gpio.trigger.GpioSetStateTrigger;
import gift.goblin.HayRackController.aop.RequiresRaspberry;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author andre
 */
@Component
public class ShutterController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private GpioController gpioController;
    private boolean raspberryInitialized;

    private GpioPinDigitalOutput pinCloseMotor;
    private GpioPinDigitalOutput pinOpenMotor;
    private GpioPinDigitalOutput pinBlueLed;
    private GpioPinDigitalOutput pinYellowLed;
    private GpioPinDigitalOutput pinSiren;

    private GpioPinDigitalInput pinManualDown;
    private GpioPinDigitalInput pinManualUp;

    @PostConstruct
    private void setupPins() {
        try {
            gpioController = GpioFactory.getInstance();

            setupVisualAndAudioOutputs();
            setupOpenShutter();
            setupCloseShutter();
            setupManualInputs();
            
            raspberryInitialized = true;
            logger.info("Raspberry PI successful initialized!");
        } catch (UnsatisfiedLinkError e) {
            logger.warn("Couldnt initialize Raspberry PI.");
            raspberryInitialized = false;
        }
    }

    public boolean isRaspberryInitialized() {
        return raspberryInitialized;
    }

    @PreDestroy
    private void releasePins() {
        logger.info("Shutdown bean- unprovision Raspberry PI pins!");
        gpioController.shutdown();
        gpioController.unprovisionPin(pinCloseMotor);
    }

    @RequiresRaspberry
    private void setupManualInputs() {
        pinManualDown = gpioController.provisionDigitalInputPin(RaspiPin.GPIO_00, PinPullResistance.PULL_DOWN);
        pinManualUp = gpioController.provisionDigitalInputPin(RaspiPin.GPIO_01, PinPullResistance.PULL_DOWN);

        // Add triggers to power on relay, if manual switch is triggered
        pinManualDown.addTrigger(new GpioSetStateTrigger(PinState.HIGH, pinCloseMotor, PinState.LOW));
        pinManualDown.addTrigger(new GpioSetStateTrigger(PinState.LOW, pinCloseMotor, PinState.HIGH));
        pinManualUp.addTrigger(new GpioSetStateTrigger(PinState.HIGH, pinOpenMotor, PinState.LOW));
        pinManualUp.addTrigger(new GpioSetStateTrigger(PinState.LOW, pinOpenMotor, PinState.HIGH));

        // Add triggers for the siren and leds
        pinManualDown.addTrigger(new GpioBlinkStateTrigger(PinState.HIGH, pinYellowLed, 250));
        pinManualDown.addTrigger(new GpioBlinkStateTrigger(PinState.HIGH, pinSiren, 250));
        pinManualDown.addTrigger(new GpioBlinkStopStateTrigger(PinState.LOW, pinYellowLed));
        pinManualDown.addTrigger(new GpioBlinkStopStateTrigger(PinState.LOW, pinSiren));
        
        pinManualUp.addTrigger(new GpioBlinkStateTrigger(PinState.HIGH, pinBlueLed, 250));
        pinManualUp.addTrigger(new GpioBlinkStateTrigger(PinState.HIGH, pinSiren, 250));
        pinManualUp.addTrigger(new GpioBlinkStopStateTrigger(PinState.LOW, pinBlueLed));
        pinManualUp.addTrigger(new GpioBlinkStopStateTrigger(PinState.LOW, pinSiren));
    }

    @RequiresRaspberry
    private void setupVisualAndAudioOutputs() {
        pinYellowLed = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_26, "Yellow-LED", PinState.LOW);
        pinYellowLed.setShutdownOptions(true, PinState.LOW);

        pinBlueLed = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_03, "Blue-LED", PinState.LOW);
        pinBlueLed.setShutdownOptions(true, PinState.LOW);

        pinSiren = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_07, "Siren", PinState.LOW);
        pinSiren.setShutdownOptions(true, PinState.LOW);
    }

    /**
     * Initialize all required pins for the shutdown shutter functionality.
     */
    @RequiresRaspberry
    private void setupCloseShutter() {
        pinCloseMotor = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_24, "Relay Channel 1", PinState.HIGH);
        pinCloseMotor.setShutdownOptions(true, PinState.HIGH);
    }

    /**
     * Initialize all required pins for the shutdown shutter functionality.
     */
    @RequiresRaspberry
    private void setupOpenShutter() {
        pinOpenMotor = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_25, "Relay Channel 2", PinState.HIGH);
        pinOpenMotor.setShutdownOptions(true, PinState.HIGH);
    }

    /**
     * Triggers the closing logic, which powers the motor to close the shutters.
     *
     * @param ms the duration, how long the motor will get powered.
     * @throws InterruptedException Dont wake me up!
     */
    @RequiresRaspberry
    public void closeShutter() throws InterruptedException {
        logger.info("Close shutters triggered! Relay will be triggered in 5 seconds! Warn lights on!");

        for (int i = 0; i < 5; i++) {
            pinYellowLed.high();
            Thread.sleep(500);
            pinYellowLed.low();
            Thread.sleep(500);
        }

        closeShutter(10_000);
    }

    /**
     * Closes the shutter for x milliseconds.
     *
     * @param ms the duration how long the motor will get powered, to drive the
     * shutter down.
     * @throws InterruptedException Dont wake me up!
     */
    @RequiresRaspberry
    public void closeShutter(int ms) throws InterruptedException {
        logger.info("Triggering closing shutter. Give em power for {} milliseconds", ms);
        pinCloseMotor.low();
        Thread.sleep(ms);
        pinCloseMotor.high();

        logger.info("Close shutter process done.");
    }

    /**
     * Opens the shutter for x milliseconds.
     *
     * @param ms the duration how long the motor will get powered, to drive the
     * shutter up.
     * @throws InterruptedException Dont wake me up!
     */
    @RequiresRaspberry
    public void openShutter(int ms) throws InterruptedException {
        logger.info("Triggering custom opening shutter. Give em power for {} milliseconds", ms);
        pinOpenMotor.low();
        Thread.sleep(ms);
        pinOpenMotor.high();

        logger.info("Open shutter process done.");
    }

    /**
     * Triggers the opening logic, which powers the motor to open the shutters.
     *
     * @param ms the duration, how long the motor will get powered.
     * @throws InterruptedException Dont wake me up!
     */
    @RequiresRaspberry
    public void openShutter() throws InterruptedException {
        logger.info("Open shutters triggered! Relay will be triggered in 5 seconds! Warn lights on!");

        for (int i = 0; i < 5; i++) {
            pinBlueLed.high();
            Thread.sleep(500);
            pinBlueLed.low();
            Thread.sleep(500);
        }

        openShutter(10_000);
    }

}
