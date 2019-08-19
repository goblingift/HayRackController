/*
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.database.backup.repo.weight;

import gift.goblin.HayRackController.database.model.weight.TareMeasurement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author andre
 */
public interface TareMeasurementBackupRepository extends JpaRepository<TareMeasurement, Long> {

    /**
     * Reads the latest tare measurement of the load cells.
     *
     * @return current value of the tare-values of the load-cells.
     */
    Optional<TareMeasurement> findTop1ByOrderByMeasuredAtDesc();
    
}
