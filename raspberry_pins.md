| Usage                            | BCM | WiringPi | Name    | Physical | Physical | Name    | WiringPi | BCM | Usage                     |
|----------------------------------|-----|----------|---------|----------|----------|---------|----------|-----|---------------------------|
|                                  |     |          | 3.3v    | 1        | 2        | 5v      |          |     |                           |
|                                  | 2   | 8        | SDA.1   | 3        | 4        | 5v      |          |     |                           |
|                                  | 3   | 9        | SCL.1   | 5        | 6        | GND     |          |     |                           |
| Temp-Sensor Outside              | 4   | 7        | 1-wire  | 7        | 8        | TxD     | 15       | 14  |                           |
|                                  |     |          | GND     | 9        | 10       | RxD     | 16       | 15  |                           |
| Temp-Voltage Outside             | 17  | 0        | GPIO.0  | 11       | 12       | GPIO.1  | 1        | 18  | Button maintenance        |
| Button tare                      | 27  | 2        | GPIO.2  | 13       | 14       | GND     |          |     |                           |
| Button remaining food            | 22  | 3        | GPIO.3  | 15       | 16       | GPIO.4  | 4        | 23  | Load-cell 2 DAT           |
|                                  |     |          | 3.3v    | 17       | 18       | GPIO.5  | 5        | 24  | Load-cell 2 SCK           |
|                                  | 10  | 12       | MOSI    | 19       | 20       | GND     |          |     |                           |
|                                  | 9   | 13       | MISO    | 21       | 22       | GPIO.6  | 6        | 25  | Load-cell 3 DAT           |
|                                  | 11  | 14       | SCLK    | 23       | 24       | CE0     | 10       | 8   | Load-cell 3 SCK           |
|                                  |     |          | GND     | 25       | 26       | CE1     | 11       | 7   | Load-cell 4 DAT           |
|                                  | 0   | 30       | SDA.0   | 27       | 28       | SCL.0   | 31       | 1   | Load-cell 4 SCK           |
| External Relay Maintenance-Light | 5   | 21       | GPIO.21 | 29       | 30       | GND     |          |     |                           |
| Brightness Sensor                | 6   | 22       | GPIO.22 | 31       | 32       | GPIO.26 | 26       | 12  | Load-cell 1 DAT           |
| External Relay Feeding-Light     | 13  | 23       | GPIO.23 | 33       | 34       | GND     |          |     |                           |
| Onboard Relay 12V source         | 19  | 24       | GPIO.24 | 35       | 36       | GPIO.27 | 27       | 16  | Load-cell 1 SCK           |
| Onboard Relay Light&Sound        | 26  | 25       | GPIO.25 | 37       | 38       | GPIO.28 | 28       | 20  | Onboard Relay Motor open  |
|                                  |     |          | GND     | 39       | 40       | GPIO.29 | 29       | 21  | Onboard Relay Motor close |