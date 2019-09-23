/*
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.service.io;

import gift.goblin.HayRackController.aop.RequiresRaspberry;
import gift.goblin.HayRackController.database.embedded.repo.weight.TareMeasurementRepository;
import gift.goblin.HayRackController.database.model.weight.TareMeasurement;
import java.time.LocalDateTime;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Bean which offers different methods to measure the weight.
 *
 * @author andre
 */
@Component
public class WeightMeasurementService {

    @Autowired
    TareMeasurementRepository tareMeasurementRepository;

    @Autowired
    IOController ioController;

    @Autowired
    TareMeasurementRepository tareRepo;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Will read the latest tare-measurement (If available) and set their tare
     * values to all load-cells.
     *
     * @return true if tare-value was found and set, false if otherwise.
     */
    public boolean readAndSetTareValueLoadCells() {

        boolean successfulReadAndSet = false;

        Optional<TareMeasurement> optTareMeasurement
                = tareMeasurementRepository.findTop1ByOrderByMeasuredAtDesc();

        if (optTareMeasurement.isPresent()) {
            ioController.setTareValueLoadCell1(optTareMeasurement.get().getTareLoadCell1());
            ioController.setTareValueLoadCell2(optTareMeasurement.get().getTareLoadCell2());
            ioController.setTareValueLoadCell3(optTareMeasurement.get().getTareLoadCell3());
            ioController.setTareValueLoadCell4(optTareMeasurement.get().getTareLoadCell4());
            successfulReadAndSet = true;
        }

        return successfulReadAndSet;
    }

    /**
     * Will set the tare value of all load-cells to current measurement.Will
     * save the Tare-Measurement into db.
     * @return true if successful, false if otherwise.
     */
    @RequiresRaspberry
    public Boolean measureAndSaveTare() {
        
        boolean measuredSuccessful = false;

        Long tareLoadCell1 = ioController.measureAndSetTareLoadCell1();
        logger.info("TARE of load-cell #1 set: {}", tareLoadCell1);

        Long tareLoadCell2 = ioController.measureAndSetTareLoadCell2();
        logger.info("TARE of load-cell #2 set: {}", tareLoadCell2);

        Long tareLoadCell3 = ioController.measureAndSetTareLoadCell3();
        logger.info("TARE of load-cell #3 set: {}", tareLoadCell3);

        Long tareLoadCell4 = ioController.measureAndSetTareLoadCell4();
        logger.info("TARE of load-cell #4 set: {}", tareLoadCell4);
        
        if (tareLoadCell1 != null && tareLoadCell2 != null && tareLoadCell3 != null
                && tareLoadCell4 != null) {
            TareMeasurement tareMeasurement = new TareMeasurement(tareLoadCell1,
                    tareLoadCell2, tareLoadCell3, tareLoadCell4, LocalDateTime.now());
            tareRepo.save(tareMeasurement);
            logger.info("Successful saved tare-measurement entity: {}", tareMeasurement);
            
            measuredSuccessful = true;
        } else {
            logger.warn("Couldnt measure and set the tare value!");
        }
        
        return measuredSuccessful;
    }

}
