/*
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.database.model.configuration;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entity which contains settings for the application.
 * @author andre
 */
@Entity
@Table
public class ApplicationConfiguration {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long configurationId;
    
    /**
     * ID of the selected sound - should contain an id of
     * @see gift.goblin.HayRackController.service.io.model.Playlist
     */
    private int soundId;

    public ApplicationConfiguration(int soundId) {
        this.soundId = soundId;
    }

    public ApplicationConfiguration() {
    }

    public int getSoundId() {
        return soundId;
    }

    public void setSoundId(int soundId) {
        this.soundId = soundId;
    }

    @Override
    public String toString() {
        return "ApplicationConfiguration{" + "configurationId=" + configurationId + ", soundId=" + soundId + '}';
    }
    
}
