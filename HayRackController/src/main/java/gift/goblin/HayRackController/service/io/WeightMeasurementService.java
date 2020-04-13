/*
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.service.io;

import gift.goblin.HayRackController.aop.RequiresRaspberry;
import gift.goblin.HayRackController.controller.model.LoadCellSettings;
import gift.goblin.HayRackController.database.embedded.repo.weight.TareMeasurementRepository;
import gift.goblin.HayRackController.database.model.weight.TareMeasurement;
import gift.goblin.HayRackController.service.configuration.ConfigurationService;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.logging.Level;
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

    @Autowired
    private ConfigurationService configurationService;

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
     *
     * @return true if successful, false if otherwise.
     */
    public Boolean measureAndSaveTare() {

        boolean measuredSuccessful = false;
        try {

            Long tareLoadCell1 = ioController.measureAndSetTareLoadCell1();
            logger.info("TARE of load-cell #1 set: {}", tareLoadCell1);
            Thread.sleep(2_000);

            Long tareLoadCell2 = ioController.measureAndSetTareLoadCell2();
            logger.info("TARE of load-cell #2 set: {}", tareLoadCell2);
            Thread.sleep(2_000);

            Long tareLoadCell3 = ioController.measureAndSetTareLoadCell3();
            logger.info("TARE of load-cell #3 set: {}", tareLoadCell3);
            Thread.sleep(2_000);

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
        } catch (InterruptedException ex) {
            logger.warn("Exception while sleep while measure weights!", ex);
        }
        return measuredSuccessful;
    }

    public long measureWeightSum() {
        long sum = 0;

        if (ioController.getLoadCellAmount() >= 4) {
            sum += ioController.measureWeightLoadCell4();
        }
        if (ioController.getLoadCellAmount() >= 3) {
            sum += ioController.measureWeightLoadCell3();
        }
        if (ioController.getLoadCellAmount() >= 2) {
            sum += ioController.measureWeightLoadCell2();
        }
        if (ioController.getLoadCellAmount() >= 1) {
            sum += ioController.measureWeightLoadCell1();
        }
        return sum;
    }

    public Long measureWeightLoadCell1() {
        return ioController.measureWeightLoadCell1();
    }

    public Long measureWeightLoadCell2() {
        return ioController.measureWeightLoadCell2();
    }

    public Long measureWeightLoadCell3() {
        return ioController.measureWeightLoadCell3();
    }

    public Long measureWeightLoadCell4() {
        return ioController.measureWeightLoadCell4();
    }

    /**
     * Reads the configured max-weight of the loadcell from the embedded
     * config-database.
     *
     * @return the max weight in kg, or 0 if not saved.
     */
    public int getMaxWeightLoadCell1() {
        LoadCellSettings loadCellSettings = configurationService.getLoadCellSettings();
        return loadCellSettings.getLoadCellMax1();
    }

    /**
     * Reads the configured max-weight of the loadcell from the embedded
     * config-database.
     *
     * @return the max weight in kg, or 0 if not saved.
     */
    public int getMaxWeightLoadCell2() {
        LoadCellSettings loadCellSettings = configurationService.getLoadCellSettings();
        return loadCellSettings.getLoadCellMax2();
    }

    /**
     * Reads the configured max-weight of the loadcell from the embedded
     * config-database.
     *
     * @return the max weight in kg, or 0 if not saved.
     */
    public int getMaxWeightLoadCell3() {
        LoadCellSettings loadCellSettings = configurationService.getLoadCellSettings();
        return loadCellSettings.getLoadCellMax3();
    }

    /**
     * Reads the configured max-weight of the loadcell from the embedded
     * config-database.
     *
     * @return the max weight in kg, or 0 if not saved.
     */
    public int getMaxWeightLoadCell4() {
        LoadCellSettings loadCellSettings = configurationService.getLoadCellSettings();
        return loadCellSettings.getLoadCellMax4();
    }

}
