/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gift.goblin.HayRackController.service.io;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.stereotype.Component;

/**
 *
 * @author andre
 */
@Component
public class ShutterController {

    private GpioController gpioController;

    private GpioPinDigitalOutput pinCloseMotor;
    private GpioPinDigitalOutput pinOpenMotor;
    private GpioPinDigitalOutput pinBlueLed;
    private GpioPinDigitalOutput pinYellowLed;

    @PostConstruct
    private void setupPins() {
        gpioController = GpioFactory.getInstance();

        setupOpenShutter();
        setupCloseShutter();
    }

    @PreDestroy
    private void releasePins() {
        System.out.println("Called PreDestroy bean ShutterController!");
        gpioController.shutdown();
        gpioController.unprovisionPin(pinCloseMotor);
    }

    /**
     * Initialize all required pins for the shutdown shutter functionality.
     */
    private void setupCloseShutter() {
        pinCloseMotor = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_24, "Relay Channel 1", PinState.HIGH);
        pinCloseMotor.setShutdownOptions(true, PinState.HIGH);

        pinYellowLed = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_26, "Yellow-LED", PinState.LOW);
        pinYellowLed.setShutdownOptions(true, PinState.LOW);
    }

    /**
     * Initialize all required pins for the shutdown shutter functionality.
     */
    private void setupOpenShutter() {
        pinOpenMotor = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_25, "Relay Channel 2", PinState.HIGH);
        pinOpenMotor.setShutdownOptions(true, PinState.HIGH);

        pinBlueLed = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_03, "Blue-LED", PinState.LOW);
        pinBlueLed.setShutdownOptions(true, PinState.LOW);
    }

    /**
     * Triggers the closing logic, which powers the motor to close the shutters.
     *
     * @param ms the duration, how long the motor will get powered.
     * @throws InterruptedException  Dont wake me up!
     */
    public void closeShutter() throws InterruptedException {
        System.out.println("Close shutters triggered! Relay will be triggered in 5 seconds! Warn lights on!");

        for (int i = 0; i < 5; i++) {
            pinYellowLed.high();
            Thread.sleep(500);
            pinYellowLed.low();
            Thread.sleep(500);
        }
    }

    /**
     * Closes the shutter for x milliseconds.
     * @param ms the duration how long the motor will get powered, to drive the shutter down.
     * @throws InterruptedException Dont wake me up!
     */
    public void closeShutter(int ms) throws InterruptedException {
        System.out.println("Triggering relay! Give em power for " + ms + " milliseconds!");
        pinCloseMotor.low();
        Thread.sleep(ms);
        pinCloseMotor.high();

        System.out.println("Close shutter process done!");
    }

    public void openShutter() throws InterruptedException {
        System.out.println("Open shutters triggered! Relay will be triggered in 5 seconds! Warn lights on!");

        for (int i = 0; i < 5; i++) {
            pinBlueLed.high();
            Thread.sleep(500);
            pinBlueLed.low();
            Thread.sleep(500);
        }

        pinOpenMotor.high();
        System.out.println("Triggered relay! Give em power for 10 seconds!");

        Thread.sleep(10_000);
        pinOpenMotor.low();

        System.out.println("Open shutter process done!");
    }

}
