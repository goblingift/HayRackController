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
     * @return the tare value. Can be null if raspberry isnt initialized well or
     * the amount is load cells is to low and this loadcell will be ignored.
     */
    Long measureAndSetTareLoadCell1();

    /**
     * Sets the tare of that load-cell.So the current weight on the scale will
     * be set as the zero value without load.
     *
     * @return the tare value. Can be null if raspberry isnt initialized well or
     * the amount is load cells is to low and this loadcell will be ignored.
     */
    Long measureAndSetTareLoadCell2();

    /**
     * Sets the tare of that load-cell.So the current weight on the scale will
     * be set as the zero value without load.
     *
     * @return the tare value. Can be null if raspberry isnt initialized well or
     * the amount is load cells is to low and this loadcell will be ignored.
     */
    Long measureAndSetTareLoadCell3();

    /**
     * Sets the tare of that load-cell.So the current weight on the scale will
     * be set as the zero value without load.
     *
     * @return the tare value. Can be null if raspberry isnt initialized well or
     * the amount is load cells is to low and this loadcell will be ignored.
     */
    Long measureAndSetTareLoadCell4();

    /**
     * Measures the current weight on the scale (Sum of all load-cells). Will
     * read and set the last tare value to all load-cells (by using AOP - so dont
     * change method name!)
     *
     * @return the weight in gram, or null if load-cells are deactivated or misconfigured.
     */
    Long measureWeight();

    /**
     * Measures the weight of the load-cell- will not using the last tare value,
     * so you should better use the method measureWeight!
     *
     * @return the weight in grams.
     */
    Long measureWeightLoadCell1();

    /**
     * Measures the weight of the load-cell- will not using the last tare value,
     * so you should better use the method measureWeight!
     *
     * @return the weight in grams.
     */
    Long measureWeightLoadCell2();

    /**
     * Measures the weight of the load-cell- will not using the last tare value,
     * so you should better use the method measureWeight!
     *
     * @return the weight in grams.
     */
    Long measureWeightLoadCell3();

    /**
     * Measures the weight of the load-cell- will not using the last tare value,
     * so you should better use the method measureWeight!
     *
     * @return the weight in grams.
     */
    Long measureWeightLoadCell4();
    
    /**
     * Sets the tare value for load-cell #1.
     * @param tareValue 
     */
    void setTareValueLoadCell1(long tareValue);
    /**
     * Sets the tare value for load-cell #2.
     * @param tareValue 
     */
    void setTareValueLoadCell2(long tareValue);
    
    /**
     * Sets the tare value for load-cell #3.
     * @param tareValue 
     */
    void setTareValueLoadCell3(long tareValue);
    
    /**
     * Sets the tare value for load-cell #4.
     * @param tareValue 
     */
    void setTareValueLoadCell4(long tareValue);
    
    
    
}
