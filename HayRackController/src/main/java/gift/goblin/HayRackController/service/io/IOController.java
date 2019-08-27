/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.service.io;

import gift.goblin.HayRackController.service.io.interfaces.MaintenanceManager;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.io.gpio.trigger.GpioCallbackTrigger;
import com.pi4j.wiringpi.Gpio;
import gift.goblin.HayRackController.aop.RequiresRaspberry;
import gift.goblin.HayRackController.service.io.dto.TemperatureAndHumidity;
import gift.goblin.HayRackController.service.io.interfaces.WeightManager;
import gift.goblin.HayRackController.service.io.model.Playlist;
import gift.goblin.HayRackController.service.io.trigger.MaintenanceTrigger;
import gift.goblin.HayRackController.service.io.trigger.ShowRemainingFoodTrigger;
import gift.goblin.HayRackController.service.io.trigger.TareTrigger;
import gift.goblin.hx711.GainFactor;
import gift.goblin.hx711.Hx711;
import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Has access to the GPIO pins of the raspberry device.
 *
 * @author andre
 */
@Component
public class IOController implements MaintenanceManager, WeightManager {

    private static final int OPENING_CLOSING_TIME_MS = 30_000;

    private static final int PIN_NO_TEMP_SENSOR = 7;
    private static final int PIN_NO_TEMP_VOLTAGE = 0;
    private static final int PIN_NO_BRIGHTNESS_SENSOR = 22;
    private static final int PIN_NO_EXTERNAL_RELAY_LIGHT = 23;
    private static final int PIN_NO_12V_TRANSFORMATOR = 24;
    private static final int PIN_NO_LIGHT_AND_SOUND = 25;
    private static final int PIN_NO_RELAY_OPEN_MOTOR = 28;
    private static final int PIN_NO_RELAY_CLOSE_MOTOR = 29;
    private static final int PIN_NO_LOAD_CELL_1_DAT = 15;
    private static final int PIN_NO_LOAD_CELL_1_SCK = 16;
    private static final int PIN_NO_LOAD_CELL_2_DAT = 4;
    private static final int PIN_NO_LOAD_CELL_2_SCK = 5;
    private static final int PIN_NO_LOAD_CELL_3_DAT = 6;
    private static final int PIN_NO_LOAD_CELL_3_SCK = 10;
    private static final int PIN_NO_LOAD_CELL_4_DAT = 11;
    private static final int PIN_NO_LOAD_CELL_4_SCK = 31;
    private static final int PIN_NO_BUTTON_MAINTENANCE = 1;
    private static final int PIN_NO_RELAY_LIGHT_MAINTENANCE = 21;
    private static final int PIN_NO_BUTTON_TARE = 2;
    private static final int PIN_NO_BUTTON_SHOW_REMAINING_FOOD = 3;

    private ApplicationState applicationState = ApplicationState.UNINITIALIZED;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private GpioController gpioController;
    private boolean raspberryInitialized;

    private Hx711 hx711LoadCell1;
    private Hx711 hx711LoadCell2;
    private Hx711 hx711LoadCell3;
    private Hx711 hx711LoadCell4;

    //<editor-fold defaultstate="collapsed" desc="pinDefinitions">
    /**
     * Pin which triggers the motor for closing shutters.
     */
    private GpioPinDigitalOutput pinCloseMotor;

    /**
     * Pin which triggers the motor for opening shutters.
     */
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

    /**
     * Pin for powering the temperature sensor.
     */
    private GpioPinDigitalOutput pinTempSensorVoltage;

    /**
     * Pin for the external relay, which switches the maintenance light.
     */
    private GpioPinDigitalOutput pinRelayLightMaintenance;

    // Pins for the load cells
    private GpioPinDigitalInput pinLoadCell1Dat;
    private GpioPinDigitalOutput pinLoadCell1Sck;
    private GpioPinDigitalInput pinLoadCell2Dat;
    private GpioPinDigitalOutput pinLoadCell2Sck;
    private GpioPinDigitalInput pinLoadCell3Dat;
    private GpioPinDigitalOutput pinLoadCell3Sck;
    private GpioPinDigitalInput pinLoadCell4Dat;
    private GpioPinDigitalOutput pinLoadCell4Sck;

    // Pins for the external buttons
    private GpioPinDigitalInput pinButtonMaintenance;
    private GpioPinDigitalInput pinButtonTare;
    private GpioPinDigitalInput pinButtonShowRemainingFood;

//</editor-fold>
    private static final int TEMPSENSOR_MAX_TIMINGS = 85;
    private final int[] dht22_dat = {0, 0, 0, 0, 0};
    private static final int TEMPSENSOR_MAX_READ_ATTEMPTS = 5;

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
            setupRelayLightMaintenance();
            setupBrightnessSensor();
            setupTemperatureSensor();
            setupLoadCells();
            setupButtons();

            raspberryInitialized = true;
            applicationState = applicationState.DEFAULT;
            logger.info("Raspberry PI successful initialized!");
        } catch (UnsatisfiedLinkError e) {
            logger.warn("Couldnt initialize Raspberry PI.");
            raspberryInitialized = false;
        }
    }

    public boolean isRaspberryInitialized() {
        return raspberryInitialized;
    }

    /**
     * Returns the current state of the application.
     *
     * @return DEFAULT when application works as expected.
     */
    @Override
    public ApplicationState getApplicationState() {
        return applicationState;
    }

    /**
     * Starts the maintenance mode- every action on the shutters will be
     * skipped, so every scheduler also. The feeding light will pulse to notice
     * you about the active maintenance mode.
     */
    @Override
    public void startMaintenanceMode() {
        logger.info("Button held long enough- entering maintenance mode now!");
        this.applicationState = ApplicationState.MAINTENANCE;
        triggerRelayLightMaintenance(true);
    }

    /**
     * Application will continue to normal mode.
     */
    @Override
    public void endMaintenanceMode() {
        logger.info("Button held long enough- end maintenance mode now!");
        this.applicationState = ApplicationState.DEFAULT;
        triggerRelayLightMaintenance(false);
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
        gpioController.unprovisionPin(pinTempSensorVoltage);
    }

    @RequiresRaspberry
    private void setupTemperatureSensor() {

        pinTempSensorVoltage = gpioController.provisionDigitalOutputPin(RaspiPin.getPinByAddress(PIN_NO_TEMP_VOLTAGE),
                PinState.LOW);
    }

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
    private void setupRelayLightMaintenance() {
        pinRelayLightMaintenance = gpioController.provisionDigitalOutputPin(RaspiPin.getPinByAddress(PIN_NO_RELAY_LIGHT_MAINTENANCE),
                "External Relay, Light Maintenance", PinState.LOW);
        pinRelayLightMaintenance.setShutdownOptions(true, PinState.LOW);
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

    @RequiresRaspberry
    private void setupLoadCells() {
        pinLoadCell1Dat = gpioController.provisionDigitalInputPin(RaspiPin.getPinByAddress(PIN_NO_LOAD_CELL_1_DAT),
                "Load-cell 1 DAT", PinPullResistance.OFF);
        pinLoadCell1Sck = gpioController.provisionDigitalOutputPin(RaspiPin.getPinByAddress(PIN_NO_LOAD_CELL_1_SCK),
                "Load-cell 1 SCK", PinState.LOW);
        hx711LoadCell1 = new Hx711(pinLoadCell1Dat, pinLoadCell1Sck, 500, 2.0, GainFactor.GAIN_128);

        pinLoadCell2Dat = gpioController.provisionDigitalInputPin(RaspiPin.getPinByAddress(PIN_NO_LOAD_CELL_2_DAT),
                "Load-cell 2 DAT", PinPullResistance.OFF);
        pinLoadCell2Sck = gpioController.provisionDigitalOutputPin(RaspiPin.getPinByAddress(PIN_NO_LOAD_CELL_2_SCK),
                "Load-cell 2 SCK", PinState.LOW);
        hx711LoadCell2 = new Hx711(pinLoadCell2Dat, pinLoadCell2Sck, 500, 2.0, GainFactor.GAIN_128);

        pinLoadCell3Dat = gpioController.provisionDigitalInputPin(RaspiPin.getPinByAddress(PIN_NO_LOAD_CELL_3_DAT),
                "Load-cell 3 DAT", PinPullResistance.OFF);
        pinLoadCell3Sck = gpioController.provisionDigitalOutputPin(RaspiPin.getPinByAddress(PIN_NO_LOAD_CELL_3_SCK),
                "Load-cell 3 SCK", PinState.LOW);
        hx711LoadCell3 = new Hx711(pinLoadCell3Dat, pinLoadCell3Sck, 500, 2.0, GainFactor.GAIN_128);

        pinLoadCell4Dat = gpioController.provisionDigitalInputPin(RaspiPin.getPinByAddress(PIN_NO_LOAD_CELL_4_DAT),
                "Load-cell 4 DAT", PinPullResistance.OFF);
        pinLoadCell4Sck = gpioController.provisionDigitalOutputPin(RaspiPin.getPinByAddress(PIN_NO_LOAD_CELL_4_SCK),
                "Load-cell 4 SCK", PinState.LOW);
        hx711LoadCell4 = new Hx711(pinLoadCell4Dat, pinLoadCell4Sck, 500, 2.0, GainFactor.GAIN_128);
    }

    @RequiresRaspberry
    private void setupButtons() {

        pinButtonMaintenance = gpioController.provisionDigitalInputPin(RaspiPin.getPinByAddress(PIN_NO_BUTTON_MAINTENANCE),
                "Button maintenance", PinPullResistance.PULL_DOWN);

        pinButtonTare = gpioController.provisionDigitalInputPin(RaspiPin.getPinByAddress(PIN_NO_BUTTON_TARE),
                "Button tare", PinPullResistance.PULL_DOWN);

        pinButtonShowRemainingFood = gpioController.provisionDigitalInputPin(RaspiPin.getPinByAddress(PIN_NO_BUTTON_SHOW_REMAINING_FOOD),
                "Button show remaining food", PinPullResistance.PULL_DOWN);

        pinButtonMaintenance.addTrigger(new GpioCallbackTrigger(new MaintenanceTrigger(this, pinButtonMaintenance)));
        pinButtonTare.addTrigger(new GpioCallbackTrigger(new TareTrigger(this, this, pinButtonTare)));
        pinButtonShowRemainingFood.addTrigger(new GpioCallbackTrigger(new ShowRemainingFoodTrigger(this, pinButtonShowRemainingFood)));
    }

//</editor-fold>
    /**
     * Triggers the opening logic, which powers the motor to open the shutters.
     *
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
     * @param track Contains the optional track. If empty, will play random one.
     * @throws InterruptedException Dont wake me up!
     */
    @RequiresRaspberry
    public void closeShutter(Optional<Playlist> track) throws InterruptedException {
        logger.info("Close shutters triggered!");

        if (!track.isPresent()) {
            track = Optional.of(Playlist.getRandomPlaylist());
        }

        closeShutter(OPENING_CLOSING_TIME_MS);

        playSoundAndLight(track.get());
    }

    /**
     * Triggers the 12V transformator with the given rhytm, to make sound and
     * light effects.
     *
     * @param track contains playtime and waittimes.
     * @throws InterruptedException if the sleeping goes wrong.
     */
    @RequiresRaspberry
    public void playSoundAndLight(Playlist track) throws InterruptedException {

        logger.info("Start playing sound and light for track: {}", track.getTitle());

        // power on 12v transformator
        pin12VTransformator.low();

        for (int i = 0; i < track.getREPEATS(); i++) {
            pinLightAndSound.low();
            Thread.sleep(track.getPLAYTIME_1());
            pinLightAndSound.high();
            Thread.sleep(track.getWAITTIME_1());

            pinLightAndSound.low();
            Thread.sleep(track.getPLAYTIME_2());
            pinLightAndSound.high();
            Thread.sleep(track.getWAITTIME_2());
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
     * Triggers the relay to power on the light for the maintenance mode.
     *
     * @param turnOn true if you wanna turn the light on, false if otherwise.
     */
    @RequiresRaspberry
    public void triggerRelayLightMaintenance(boolean turnOn) {

        if (turnOn) {
            logger.info("Triggered relay maintenance-light to: ON");
            pinRelayLightMaintenance.high();
        } else {
            pinRelayLightMaintenance.low();
            logger.info("Triggered relay maintenance-light to: OFF");
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
     *
     * @return Optional with the result, empty optional if measurement failure
     * or null value, if no raspberry was initialized.
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
                logger.debug("Couldnt read values from temperature sensor, take a nap of 5 seconds and try again!");
                try {
                    Thread.sleep(5000);
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

        // Power on the temperature sensor
        pinTempSensorVoltage.high();
        Gpio.delay(5000);

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
            logger.warn("Data not good, skip");
        }

        // Power off the temperature sensor
        pinTempSensorVoltage.low();

        return returnValue;
    }

    private boolean checkParityTempSensor() {
        return dht22_dat[4] == (dht22_dat[0] + dht22_dat[1] + dht22_dat[2] + dht22_dat[3] & 0xFF);
    }

    @Override
    public long measureWeightLoadCell1() {
        long measurement = hx711LoadCell1.measure();
        logger.info("Measured weight of load-cell #1: " + measurement);
        return measurement;
    }

    @Override
    public long measureWeightLoadCell2() {
        long measurement = hx711LoadCell2.measure();
        logger.info("Measured weight of load-cell #2: " + measurement);
        return measurement;
    }

    @Override
    public long measureWeightLoadCell3() {
        long measurement = hx711LoadCell3.measure();
        logger.info("Measured weight of load-cell #3: " + measurement);
        return measurement;
    }

    @Override
    public long measureWeightLoadCell4() {
        long measurement = hx711LoadCell4.measure();
        logger.info("Measured weight of load-cell #4: " + measurement);
        return measurement;
    }

    @Override
    public long measureAndSetTareLoadCell1() {
        return hx711LoadCell1.measureAndSetTare();
    }

    @Override
    public long measureAndSetTareLoadCell2() {
        return hx711LoadCell2.measureAndSetTare();
    }

    @Override
    public long measureAndSetTareLoadCell3() {
        return hx711LoadCell3.measureAndSetTare();
    }

    @Override
    public long measureAndSetTareLoadCell4() {
        return hx711LoadCell4.measureAndSetTare();
    }

    @Override
    public long measureWeight() {
        return measureWeightLoadCell1() + measureWeightLoadCell2() + measureWeightLoadCell3() + measureWeightLoadCell4();
    }

    @Override
    public void setTareValueLoadCell1(long tareValue) {
        hx711LoadCell1.setTareValue(tareValue);
    }

    @Override
    public void setTareValueLoadCell2(long tareValue) {
        hx711LoadCell2.setTareValue(tareValue);
    }

    @Override
    public void setTareValueLoadCell3(long tareValue) {
        hx711LoadCell3.setTareValue(tareValue);
    }

    @Override
    public void setTareValueLoadCell4(long tareValue) {
        hx711LoadCell4.setTareValue(tareValue);
    }

    @Override
    public boolean isMaintenanceModeActive() {
        return this.getApplicationState() == ApplicationState.MAINTENANCE;
    }

}
