/*
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.service.io;

import gift.goblin.HayRackController.database.embedded.repo.weight.TareMeasurementRepository;
import gift.goblin.HayRackController.database.model.weight.TareMeasurement;
import java.util.Optional;
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

}
