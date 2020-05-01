/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.service.configuration;

import gift.goblin.HayRackController.controller.model.LoadCellSettings;
import gift.goblin.HayRackController.controller.model.SoundSettings;
import gift.goblin.HayRackController.service.io.model.Playlist;
import java.util.Optional;

/**
 * Defines several methods to read and edit the application settings.
 * @author andre
 */
public interface ConfigurationService {
    
    public Optional<Playlist> getSelectedSound();
    
    /**
     * Try to read the saved settings from database.
     * @return the saved settings or an empty settings object
     * with default values.
     */
    public SoundSettings getSoundSettings();

    /**
     * Reads the load-cell related settings from database. 
     * @return the saved settings or an empty settings object
     * with default values.
     */
    public LoadCellSettings getLoadCellSettings();
    
    /**
     * Saves the sound settings into database.
     * Will overwrite existing entries.
     * @param settings the sound settings dto.
     */
    public void saveSettings(SoundSettings settings);
    
    /**
     * Saves the load-cell settings into the database.
     * Will overwrite existing entries.
     * @param settings the load-cell settings dto.
     */
    public void saveSettings(LoadCellSettings settings);
    
}
