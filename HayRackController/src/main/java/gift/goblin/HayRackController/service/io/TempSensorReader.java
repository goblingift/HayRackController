/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gift.goblin.HayRackController.service.io;

import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.GpioUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


/**
 * Bean for reading temperature and humidity values from the
 * connected DHT-22 (Or DHT-11) sensor.
 * @author andre
 */
@Component
public class TempSensorReader {
    private static final int    MAXTIMINGS  = 85;
    private final int[]         dht22_dat   = { 0, 0, 0, 0, 0 };
    private static final int    MAX_READ_ATTEMPTS = 20;
    
    public static final String KEY_TEMPERATURE = "temp";
    public static final String KEY_TEMPERATURE_FAHRENHEIT = "tempFahrenheit";
    public static final String KEY_HUMIDITY = "humidity";
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    public TempSensorReader() {

        // setup wiringPi
        if (Gpio.wiringPiSetup() == -1) {
            System.out.println(" ==>> GPIO SETUP FAILED");
            return;
        }

        GpioUtil.export(3, GpioUtil.DIRECTION_OUT);
    }
    
    /**
     * Try to read the temperature and humidity of the DHT-22 sensor.
     * If the measured data is faulty, it will trigger the measurement several
     * times until its successful (default = 10 tries).
     * @param pin number of the used input pin for the DHT-22.
     * @return Optional Map with the values, or an empty optional, if the measurement
     * wasnt succesful after the maximum amount of tries.
     * @throws InterruptedException If the sleep commands were throwing Exceptions.
     */
    public Optional<Map<String, Float>> getTempAndHumidity(final int pin) throws InterruptedException {
        
        Optional<Map<String, Float>> returnValue = Optional.empty();
        
        for (int i = 1; i <= MAX_READ_ATTEMPTS && !returnValue.isPresent(); i++) {
            returnValue = measureValues(pin);
            if (!returnValue.isPresent()) {
                logger.info("Couldnt read values from temperature sensor, try again!");
                Thread.sleep(2000);
            } else {
                logger.info("Successful read values from temperature sensor.");
                // Add number of read attempts to map
                Map<String, Float> resultMap = returnValue.get();
                resultMap.put("attempts", Integer.valueOf(i).floatValue());
                returnValue = Optional.of(resultMap);
            }
        }
        
        return returnValue;
    }
    
    private Optional<Map<String, Float>> measureValues(final int pin) {
        int laststate = Gpio.HIGH;
        int j = 0;
        dht22_dat[0] = dht22_dat[1] = dht22_dat[2] = dht22_dat[3] = dht22_dat[4] = 0;

        Gpio.pinMode(pin, Gpio.OUTPUT);
        Gpio.digitalWrite(pin, Gpio.LOW);
        Gpio.delay(18);
        
        Gpio.digitalWrite(pin, Gpio.HIGH);
        Gpio.delayMicroseconds(7);
        Gpio.pinMode(pin, Gpio.INPUT);

        for (int i = 0; i < MAXTIMINGS; i++) {
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
        if (j >= 40 && checkParity()) {
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
            
            Map<String, Float> valueMap = new HashMap<>();
            valueMap.put(KEY_TEMPERATURE, c);
            valueMap.put(KEY_TEMPERATURE_FAHRENHEIT, f);
            valueMap.put(KEY_HUMIDITY, h);
            return Optional.of(valueMap);
        } else {
            System.out.println("Data not good, skip");
            return Optional.empty();
        }

    }

    private boolean checkParity() {
        return dht22_dat[4] == (dht22_dat[0] + dht22_dat[1] + dht22_dat[2] + dht22_dat[3] & 0xFF);
    }

}