/*
 * Copyright (C) 2020 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.controller.model;

/**
 * Dto with informations about a food consumption.
 * @author andre
 */
public class FoodConsumption {
    
    /**
     * Contains the formatted starting time of the feeding-event.
     * E.g. "DO 12.03. 14:00 Uhr".
     */
    private String startDateTime;
    
    /**
     * Contains the information how many food were consumed. In kilograms.
     */
    private double consumptionInKg;
    
    /**
     * Contains the feeding time in minutes.
     */
    private int feedingTimeMins;

    public FoodConsumption() {
    }

    public FoodConsumption(String startDateTime, double consumptionInKg, int feedingTimeMins) {
        this.startDateTime = startDateTime;
        this.consumptionInKg = consumptionInKg;
        this.feedingTimeMins = feedingTimeMins;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }

    public double getConsumptionInKg() {
        return consumptionInKg;
    }

    public void setConsumptionInKg(double consumptionInKg) {
        this.consumptionInKg = consumptionInKg;
    }

    public int getFeedingTimeMins() {
        return feedingTimeMins;
    }

    public void setFeedingTimeMins(int feedingTimeMins) {
        this.feedingTimeMins = feedingTimeMins;
    }

    @Override
    public String toString() {
        return "FoodConsumption{" + "startDateTime=" + startDateTime + ", consumptionInKg=" + consumptionInKg + ", feedingTimeMins=" + feedingTimeMins + '}';
    }
    
}
