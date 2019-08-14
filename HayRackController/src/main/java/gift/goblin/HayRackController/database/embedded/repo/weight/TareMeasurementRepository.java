/*
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.database.embedded.repo.weight;

import gift.goblin.HayRackController.database.model.weight.TareMeasurement;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author andre
 */
public interface TareMeasurementRepository extends JpaRepository<TareMeasurement, Long> {
    
    /**
     * Reads the latest tare measurement of the load cells.
     * @return current value of the tare-values of the load-cells.
     */
    Optional<TareMeasurement> findTop1ByOrderByMeasuredAtDesc();
    
}
