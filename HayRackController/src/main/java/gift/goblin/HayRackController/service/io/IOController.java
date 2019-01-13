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
import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.GpioUtil;
import gift.goblin.HayRackController.aop.RequiresRaspberry;
import gift.goblin.HayRackController.service.io.dto.TemperatureAndHumidity;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

/**
 * Has access to the GPIO pins of the raspberry device.
 *
 * @author andre
 */
@Component
public class IOController {

    private static final int OPENING_CLOSING_TIME_MS = 30_000;

    public static final int PIN_NO_TEMP_SENSOR = 21;
    private static final int PIN_NO_BRIGHTNESS_SENSOR = 22;
    private static final int PIN_NO_EXTERNAL_RELAY_LIGHT = 23;
    private static final int PIN_NO_12V_TRANSFORMATOR = 24;
    private static final int PIN_NO_LIGHT_AND_SOUND = 25;
    private static final int PIN_NO_RELAY_OPEN_MOTOR = 28;
    private static final int PIN_NO_RELAY_CLOSE_MOTOR = 29;

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
    
    private static final int TEMPSENSOR_MAX_TIMINGS = 85;
    private final int[] dht22_dat = {0, 0, 0, 0, 0};
    private static final int TEMPSENSOR_MAX_READ_ATTEMPTS = 20;

    public static final String KEY_TEMPERATURE = "temp";
    public static final String KEY_TEMPERATURE_FAHRENHEIT = "tempFahrenheit";
    public static final String KEY_HUMIDITY = "humidity";

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

    private void setupPinsTemperatureSensor() {

        // setup wiringPi
        try {
            if (Gpio.wiringPiSetup() == -1) {
                logger.warn("WiringPI initialization in TempSensorReader failed!");
                return;
            }

            GpioUtil.export(3, GpioUtil.DIRECTION_OUT);
        } catch (java.lang.UnsatisfiedLinkError e) {
            logger.warn("Couldnt initialize TempSensorReader: {}", e.getMessage());
        }
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
     * Measure the temperature and humidity.
     * @return Optional with the result, empty optional if measurement failure or
     * null value, if no raspberry was initialized.
     */
    public Optional<TemperatureAndHumidity> getTempAndHumidity() {

        // If raspberry isnt initialized, just return null
        if (!raspberryInitialized) {
            return null;
        }
        
        Optional<TemperatureAndHumidity> measuredResult = Optional.empty();

        for (int i = 1; i <= TEMPSENSOR_MAX_READ_ATTEMPTS && !measuredResult.isPresent(); i++) {
            measuredResult = measureTempSensorValues(PIN_NO_TEMP_SENSOR);
            if (!measuredResult.isPresent()) {
                logger.debug("Couldnt read values from temperature sensor, try again!");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    logger.error("Exception thrown while try to sleep for temp-sensor!", ex);
                }
            } else {
                logger.info("Successful read values from temperature sensor: {}", measuredResult.get());
            }
        }

        if (!measuredResult.isPresent()) {
            logger.error("Couldnt read values from temperature sensor! Failed attempts: {}", TEMPSENSOR_MAX_READ_ATTEMPTS);
        }

        return measuredResult;
    }

    private Optional<TemperatureAndHumidity> measureTempSensorValues(final int pin) {

        Optional<TemperatureAndHumidity> returnValue = Optional.empty();
        int laststate = Gpio.HIGH;
        int j = 0;
        dht22_dat[0] = dht22_dat[1] = dht22_dat[2] = dht22_dat[3] = dht22_dat[4] = 0;

        Gpio.pinMode(pin, Gpio.OUTPUT);
        Gpio.digitalWrite(pin, Gpio.LOW);
        Gpio.delay(18);

        Gpio.digitalWrite(pin, Gpio.HIGH);
        Gpio.delayMicroseconds(7);
        Gpio.pinMode(pin, Gpio.INPUT);

        for (int i = 0; i < TEMPSENSOR_MAX_TIMINGS; i++) {
            int counter = 0;
            while (Gpio.digitalRead(pin) == laststate) {
                counter++;
                Gpio.delayMicroseconds(1);
                if (counter == 255) {
                    break;
                }
            }

            laststate = Gpio.digitalRead(pin);

            if (counter == 255) {
                break;
            }

            /* ignore first 3 transitions */
            if (i >= 4 && i % 2 == 0) {
                /* shove each bit into the storage bytes */
                dht22_dat[j / 8] <<= 1;
                if (counter > 30) {
                    dht22_dat[j / 8] |= 1;
                }
                j++;
            }
        }
        // check we read 40 bits (8bit x 5 ) + verify checksum in the last
        // byte
        if (j >= 40 && checkParityTempSensor()) {
            float h = (float) ((dht22_dat[0] << 8) + dht22_dat[1]) / 10;
            if (h > 100) {
                h = dht22_dat[0]; // for DHT11
            }
            float c = (float) (((dht22_dat[2] & 0x7F) << 8) + dht22_dat[3]) / 10;
            if (c > 125) {
                c = dht22_dat[2]; // for DHT11
            }
            if ((dht22_dat[2] & 0x80) != 0) {
                c = -c;
            }
            final float f = c * 1.8f + 32;

            TemperatureAndHumidity temperatureAndHumidity = new TemperatureAndHumidity(c, f, h);
            returnValue = Optional.of(temperatureAndHumidity);
        } else {
            System.out.println("Data not good, skip");
        }

        return returnValue;
    }

    private boolean checkParityTempSensor() {
        return dht22_dat[4] == (dht22_dat[0] + dht22_dat[1] + dht22_dat[2] + dht22_dat[3] & 0xFF);
    }

}
