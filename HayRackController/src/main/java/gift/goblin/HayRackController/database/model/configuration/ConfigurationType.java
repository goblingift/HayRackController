/*
 * Copyright (C) 2020 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.database.model.configuration;

/**
 * Defines several types of configuration values.
 *
 * @author andre
 */
public enum ConfigurationType {

    SOUND_MELODY(50),
    LOADCELLS_ACTIVATED(100),
    LOADCELLS_AMOUNT(101),
    LOADCELL_1_PIN_SCK(110),
    LOADCELL_1_PIN_DAT(111),
    LOADCELL_1_MAXLOAD_KG(112),
    LOADCELL_1_MVV(113),
    LOADCELL_2_PIN_SCK(120),
    LOADCELL_2_PIN_DAT(121),
    LOADCELL_2_MAXLOAD_KG(122),
    LOADCELL_2_MVV(123),
    LOADCELL_3_PIN_SCK(130),
    LOADCELL_3_PIN_DAT(131),
    LOADCELL_3_MAXLOAD_KG(132),
    LOADCELL_3_MVV(133),
    LOADCELL_4_PIN_SCK(140),
    LOADCELL_4_PIN_DAT(141),
    LOADCELL_4_MAXLOAD_KG(142),
    LOADCELL_4_MVV(143);

    private ConfigurationType(int id) {
        this.id = id;
    }
    


private int id;

    public int getId() {
        return id;
    }
}
