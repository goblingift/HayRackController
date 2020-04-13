/*
 * Copyright (C) 2020 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.controller.model;

import java.util.List;

/**
 * Dto which contains several informations about the load-cells current weight.
 * @author andre
 */
public class WeightDetailsDto {

    public WeightDetailsDto() {
    }

    public WeightDetailsDto(double weightSumKg, int maxWeightSum, String ticks, String description) {
        this.weightSumKg = weightSumKg;
        this.maxWeightSum = maxWeightSum;
        this.ticks = ticks;
        this.description = description;
    }
    
    /**
     * Summed current measured weight of all load-cells.
     */
    private double weightSumKg;
    
    /**
     * Number of load-cells.
     */
    private int loadCellAmount;

    /**
     * Summed max-weight of all load-cells.
     */
    private int maxWeightSum;
    
    /**
     * The ticks, to render the gauge.
     * Should contain comma-separated values.
     */
    private String ticks;
    
    /**
     * String which describes for which load-cell this element contains data.
     */
    private String description;
    
    /**
     * List with values for each single load-cell.
     */
    private List<WeightDetailsDto> loadCellList;

    public double getWeightSumKg() {
        return weightSumKg;
    }

    public void setWeightSumKg(double weightSumKg) {
        this.weightSumKg = weightSumKg;
    }

    public int getLoadCellAmount() {
        return loadCellAmount;
    }

    public void setLoadCellAmount(int loadCellAmount) {
        this.loadCellAmount = loadCellAmount;
    }

    public int getMaxWeightSum() {
        return maxWeightSum;
    }

    public void setMaxWeightSum(int maxWeightSum) {
        this.maxWeightSum = maxWeightSum;
    }

    public String getTicks() {
        return ticks;
    }

    public void setTicks(String ticks) {
        this.ticks = ticks;
    }

    public List<WeightDetailsDto> getLoadCellList() {
        return loadCellList;
    }

    public void setLoadCellList(List<WeightDetailsDto> loadCellList) {
        this.loadCellList = loadCellList;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "WeightDetailsDto{" + "weightSumKg=" + weightSumKg + ", loadCellAmount=" + loadCellAmount + ", maxWeightSum=" + maxWeightSum + ", ticks=" + ticks + ", description=" + description + ", loadCellList=" + loadCellList + '}';
    }

}
