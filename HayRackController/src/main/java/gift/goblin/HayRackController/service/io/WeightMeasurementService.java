/*
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.service.io;

import gift.goblin.HayRackController.database.embedded.repo.event.FeedingEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Bean which offers different methods to measure the weight.
 * @author andre
 */
@Component
public class WeightMeasurementService {
    
    
    @Autowired
    private FeedingEventRepository feedingEventRepo;
    
    
    
    
    
}
