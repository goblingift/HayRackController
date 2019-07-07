/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.controller.model;

/**
 * Form DTO which contains all bound properties of the settings-page. 
 * @author andre
 */
public class Settings {
    
    private String selectedSound;

    public String getSelectedSound() {
        return selectedSound;
    }

    public void setSelectedSound(String selectedSound) {
        this.selectedSound = selectedSound;
    }

    @Override
    public String toString() {
        return "Settings{" + "selectedSound=" + selectedSound + '}';
    }
    
}
