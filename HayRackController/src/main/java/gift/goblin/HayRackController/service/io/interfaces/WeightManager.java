/*
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.service.io.interfaces;

/**
 * Interface which defines several methods regarding weight-related operations.
 *
 * @author andre
 */
public interface WeightManager {

    /**
     * Sets the tare of that load-cell.So the current weight on the scale will
     * be set as the zero value without load.
     *
     * @return the tare value.
     */
    long setTareLoadCell1();

    /**
     * Sets the tare of that load-cell.So the current weight on the scale will
     * be set as the zero value without load.
     *
     * @return the tare value.
     */
    long setTareLoadCell2();

    /**
     * Sets the tare of that load-cell.So the current weight on the scale will
     * be set as the zero value without load.
     *
     * @return the tare value.
     */
    long setTareLoadCell3();

    /**
     * Sets the tare of that load-cell.So the current weight on the scale will
     * be set as the zero value without load.
     *
     * @return the tare value.
     */
    long setTareLoadCell4();

    /**
     * Measures the current weight on the scale (Sum of all load-cells).
     * @return the weight in gram.
     */
    long measureWeight();
    
    long measureWeightLoadCell1();
    long measureWeightLoadCell2();
    long measureWeightLoadCell3();
    long measureWeightLoadCell4();

}
