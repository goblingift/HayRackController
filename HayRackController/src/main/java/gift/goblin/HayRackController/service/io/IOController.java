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
import gift.goblin.HayRackController.service.io.dto.TemperatureAndHumidity;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Has access to the GPIO pins of the raspberry device.
 *
 * @author andre
 */
@Component
public class IOController {

    private static final int OPENING_CLOSING_TIME_MS = 30_000;

    private static final int PIN_NO_TEMP_SENSOR = 21;
    private static final int PIN_NO_BRIGHTNESS_SENSOR = 22;
    private static final int PIN_NO_EXTERNAL_RELAY_LIGHT = 23;
    private static final int PIN_NO_12V_TRANSFORMATOR = 24;
    private static final int PIN_NO_LIGHT_AND_SOUND = 25;
    private static final int PIN_NO_RELAY_OPEN_MOTOR = 28;
    private static final int PIN_NO_RELAY_CLOSE_MOTOR = 29;

    @Autowired
    private TempSensorReader tempSensorReader;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private GpioController gpioController;
    private boolean raspberryInitialized;

    private GpioPinDigitalOutput pinCloseMotor;

    private GpioPinDigitalOutput pinOpenMotor;

    /**
     * Pin which access the brightness sensor.
     */
    private GpioPinDigitalInput pinBrightnessSensor;

    /**
     * Pin for the 230V to 12V transformator
     */
    private GpioPinDigitalOutput pin12VTransformator;

    /**
     * Controls the first onboard relais (Which triggers 12V adapter for light &
     * sound)
     */
    private GpioPinDigitalOutput pinLightAndSound;

    /**
     * Pin for the external relay, which switches the indoor light.
     */
    private GpioPinDigitalOutput pinRelayLight;

//<editor-fold defaultstate="collapsed" desc="setup pins">
    @PostConstruct
    private void setupPins() {
        try {
            gpioController = GpioFactory.getInstance();

            setupVisualAndAudioOutputs();
            setupOpenShutter();
            setupCloseShutter();
            setup12VTransformator();
            setupRelayLight();
            setupBrightnessSensor();

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
        gpioController.unprovisionPin(pinRelayLight);
        gpioController.unprovisionPin(pinBrightnessSensor);
    }

    /**
     * Setup for the 12V transformator.
     */
    @RequiresRaspberry
    private void setup12VTransformator() {
        pin12VTransformator = gpioController.provisionDigitalOutputPin(RaspiPin.getPinByAddress(PIN_NO_12V_TRANSFORMATOR),
                "Relay #1, 12V transformator", PinState.HIGH);
        pinLightAndSound.setShutdownOptions(true, PinState.HIGH);
    }

    @RequiresRaspberry
    private void setupRelayLight() {
        pinRelayLight = gpioController.provisionDigitalOutputPin(RaspiPin.getPinByAddress(PIN_NO_EXTERNAL_RELAY_LIGHT),
                "External Relay, Light", PinState.LOW);
        pinRelayLight.setShutdownOptions(true, PinState.LOW);
    }

    @RequiresRaspberry
    private void setupBrightnessSensor() {
        pinBrightnessSensor = gpioController.provisionDigitalInputPin(RaspiPin.getPinByAddress(PIN_NO_BRIGHTNESS_SENSOR));
        pinBrightnessSensor.setShutdownOptions(true, PinState.LOW);
    }

    @RequiresRaspberry
    private void setupVisualAndAudioOutputs() {
        pinLightAndSound = gpioController.provisionDigitalOutputPin(RaspiPin.getPinByAddress(PIN_NO_LIGHT_AND_SOUND),
                "Relay #2, Light and Sound", PinState.HIGH);
        pinLightAndSound.setShutdownOptions(true, PinState.HIGH);
    }

    /**
     * Initialize all required pins for the open shutter functionality.
     */
    @RequiresRaspberry
    private void setupOpenShutter() {
        pinOpenMotor = gpioController.provisionDigitalOutputPin(RaspiPin.getPinByAddress(PIN_NO_RELAY_OPEN_MOTOR),
                "Relay #3, Open motor", PinState.HIGH);
        pinOpenMotor.setShutdownOptions(true, PinState.HIGH);
        pinOpenMotor.addListener(new GpioPinListenerDigital() {
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
        pinCloseMotor = gpioController.provisionDigitalOutputPin(RaspiPin.getPinByAddress(PIN_NO_RELAY_CLOSE_MOTOR),
                "Relay #4, Close motor", PinState.HIGH);
        pinCloseMotor.setShutdownOptions(true, PinState.HIGH);
        pinCloseMotor.addListener(new GpioPinListenerDigital() {
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
     * Triggers the relay to power on the light.
     *
     * @param turnOn true if you wanna turn the light on, false if otherwise.
     */
    @RequiresRaspberry
    public void triggerRelayLight(boolean turnOn) {

        if (turnOn) {
            logger.info("Triggered relay light to: ON");
            pinRelayLight.high();
        } else {
            pinRelayLight.low();
            logger.info("Triggered relay light to: OFF");
        }
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

    /**
     * Measures the brightness with the brightness sensor.
     *
     * @return true if its bright, false if dark.
     */
    @RequiresRaspberry
    public boolean daylightDetected() {
        boolean daylightDetected = pinBrightnessSensor.isHigh();
        logger.debug("Measured pin state of brighness-sensor- is bright: {}", daylightDetected);

        return daylightDetected;
    }

    /**
     * Measures the temperature and humidity.
     *
     * @return Optional, cause the measurement of this values isnt guaranteed.
     */
    @RequiresRaspberry
    public Optional<TemperatureAndHumidity> measureTempAndHumidity() {
        
        Optional<TemperatureAndHumidity> returnValue = Optional.empty();
        
        try {
            Optional<Map<String, Float>> optTempAndHumidityMap = tempSensorReader.getTempAndHumidity(PIN_NO_TEMP_SENSOR);
            if (optTempAndHumidityMap.isPresent()) {
                returnValue = Optional.of(new TemperatureAndHumidity(optTempAndHumidityMap.get()));
            }
        } catch (InterruptedException ex) {
            logger.error("InterruptedException thrown while measureTempAndHumidity!", ex);
            return Optional.empty();
        }
        
        return returnValue;
    }

}
