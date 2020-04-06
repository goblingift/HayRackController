/*
 * Copyright (C) 2020 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.database.model.configuration;

/**
 * Defines several types of configuration values.
 * @author andre
 */
public enum ConfigurationType {
    
    SOUND_MELODY(50),
    LOADCELLS_ACTIVATED(100),
    LOADCELLS_AMOUNT(101),
    LOADCELL_1_PIN_SCK(110),
    LOADCELL_1_PIN_DAT(111),
    LOADCELL_1_MAXLOAD_KG(112),
    LOADCELL_1_MVV(113);
    
    
    private ConfigurationType(int id) {
        this.id = id;
    }
    
    private int id;

    public int getId() {
        return id;
    }
}
