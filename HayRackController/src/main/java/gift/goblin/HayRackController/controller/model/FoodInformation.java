/*
 * Copyright (C) 2020 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.controller.model;

import java.util.List;

/**
 * Dto with several important informations about the current loaded food and the latest
 * consumptions.
 * @author andre
 */
public class FoodInformation {
    
    /**
     * How many food is remaining, in kilograms, rounded to 2 decimal places.
     */
    private String remainingFoodKg;
    
    /**
     * List with the last 10 food consumptions.
     */
    private List<FoodConsumption> last10Consumptions;

    public String getRemainingFoodKg() {
        return remainingFoodKg;
    }

    public void setRemainingFoodKg(String remainingFoodKg) {
        this.remainingFoodKg = remainingFoodKg;
    }

    public List<FoodConsumption> getLast10Consumptions() {
        return last10Consumptions;
    }

    public void setLast10Consumptions(List<FoodConsumption> last10Consumptions) {
        this.last10Consumptions = last10Consumptions;
    }

    @Override
    public String toString() {
        return "FoodInformation{" + "remainingFoodKg=" + remainingFoodKg + ", last10Consumptions=" + last10Consumptions + '}';
    }
    
}
