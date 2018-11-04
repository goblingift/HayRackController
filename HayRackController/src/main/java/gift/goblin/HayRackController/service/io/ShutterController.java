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
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
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

    private static final int OPENING_CLOSING_TIME_MS = 30_000;

    /**
     * Pin for the 230V to 12V transformator
     */
    private GpioPinDigitalOutput pin12VTransformator;

    /**
     * Controls the first onboard relais (Which triggers 12V adapter for light &
     * sound)
     */
    private GpioPinDigitalOutput pinLightAndSound;

//<editor-fold defaultstate="collapsed" desc="setup pins">
    @PostConstruct
    private void setupPins() {
        try {
            gpioController = GpioFactory.getInstance();

            setupVisualAndAudioOutputs();
            setupOpenShutter();
            setupCloseShutter();
            setup12VTransformator();

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
        gpioController.unprovisionPin(pinOpenMotor);
        gpioController.unprovisionPin(pin12VTransformator);
        gpioController.unprovisionPin(pinLightAndSound);
    }

    /**
     * Setup for the 12V transformator.
     */
    @RequiresRaspberry
    private void setup12VTransformator() {
        pin12VTransformator = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_24, "Relay #1, 12V transformator", PinState.HIGH);
        pinLightAndSound.setShutdownOptions(true, PinState.HIGH);
    }

    @RequiresRaspberry
    private void setupVisualAndAudioOutputs() {
        pinLightAndSound = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_25, "Relay #2, Light and Sound", PinState.HIGH);
        pinLightAndSound.setShutdownOptions(true, PinState.HIGH);
    }

    /**
     * Initialize all required pins for the open shutter functionality.
     */
    @RequiresRaspberry
    private void setupOpenShutter() {
        pinOpenMotor = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_28, "Relay #3, Open motor", PinState.HIGH);
        pinOpenMotor.setShutdownOptions(true, PinState.HIGH);
        pinOpenMotor.addListener(new GpioPinListenerDigital()  {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                logger.info("Changed pinstate of pin# {} to state: {}", event.getPin().getName(), event.getState().getValue());
            }
        });
    }

    /**
     * Initialize all required pins for the close shutter functionality.
     */
    @RequiresRaspberry
    private void setupCloseShutter() {
        pinCloseMotor = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_29, "Relay #4, Close motor", PinState.HIGH);
        pinCloseMotor.setShutdownOptions(true, PinState.HIGH);
        pinCloseMotor.addListener(new GpioPinListenerDigital()  {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                logger.info("Changed pinstate of pin# {} to state: {}", event.getPin().getName(), event.getState().getValue());
            }
        });
    }

//</editor-fold>
    /**
     * Triggers the opening logic, which powers the motor to open the shutters.
     *
     * @param ms the duration, how long the motor will get powered.
     * @throws InterruptedException Dont wake me up!
     */
    @RequiresRaspberry
    public void openShutter() throws InterruptedException {
        logger.info("Open shutters triggered! Power on 12V transformator for light and sound warnings! Motors will be triggered immediately!");

        // power on 12v transformator
        pin12VTransformator.low();

        for (int i = 0; i < 5; i++) {
            pinLightAndSound.low();
            Thread.sleep(500);
            pinLightAndSound.high();
            Thread.sleep(500);
        }
        
        // trigger shutter motors after the sound and lights was played
        openShutter(OPENING_CLOSING_TIME_MS);

        // power off 12v transformator
        pin12VTransformator.high();
    }

    /**
     * Triggers the closing logic, which powers the motor to close the shutters.
     * Including warn lights and warn sounds.
     *
     * @param ms the duration, how long the motor will get powered.
     * @throws InterruptedException Dont wake me up!
     */
    @RequiresRaspberry
    public void closeShutter() throws InterruptedException {
        logger.info("Close shutters triggered! Relay will be triggered in 5 seconds! Warn lights & sounds will be activated!");

        // power on 12v transformator
        pin12VTransformator.low();

        // trigger shutter motors to the same time as the sound and lights
        closeShutter(OPENING_CLOSING_TIME_MS);

        for (int i = 0; i < (OPENING_CLOSING_TIME_MS / 1000 / 2); i++) {
            pinLightAndSound.low();
            Thread.sleep(500);
            pinLightAndSound.high();
            Thread.sleep(500);

            pinLightAndSound.low();
            Thread.sleep(150);
            pinLightAndSound.high();
            Thread.sleep(150);
        }

        // power off 12v transformator
        pin12VTransformator.high();

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
        logger.info("Trigger closing shutter motors. Give em power for {} milliseconds", ms);
        
        pinCloseMotor.pulse(ms, PinState.LOW);
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
        logger.info("Trigger opening shutter motors. Give em power for {} milliseconds", ms);
        
        pinOpenMotor.pulse(ms, PinState.LOW);
    }

}
