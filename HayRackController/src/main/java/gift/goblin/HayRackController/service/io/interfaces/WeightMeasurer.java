/*
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.service.io.interfaces;

/**
 *
 * @author andre
 */
public interface WeightMeasurer {
    
    /**
     * Measures the current weight and return the value in gram.
     */
    long measureWeight();
    
    /**
     * Measures the current weight and save it in the end-weight field of 
     * the feedingEntry-entity. Also calculates the eaten food and save it.
     * @param feedingEntryId PK of the feedingEntry-entity.
     */
    void measureEndWeight(Long feedingEntryId);
    
}
