/*
 * Copyright (C) 2020 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.controller.model;

/**
 * Form DTO which contains all bound properties of the load-cell-section on settings-page.
 * @author andre
 */
public class LoadCellSettings {
    
    /**
     * Decides if the load-cells are basically enabled (true) or disabled (false).
     */
    private boolean enabled;
    
    /**
     * Number of load-cells.
     */
    private int amount;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "LoadCellSettings{" + "enabled=" + enabled + ", amount=" + amount + '}';
    }
    
}
