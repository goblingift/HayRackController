/*
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.service.configuration;

import gift.goblin.HayRackController.controller.model.Settings;
import gift.goblin.HayRackController.service.io.model.Playlist;
import java.util.Optional;

/**
 * Defines several methods to read and edit the application settings.
 * @author andre
 */
public interface ConfigurationService {
    
    public void saveSelectedSound(Playlist selectedSound);
    
    public Optional<Playlist> getSelectedSound();
    
    /**
     * Try to read the saved settings from database.
     * @return the saved settings or an empty settings object
     * with default values.
     */
    public Settings getSettings();
    
    /**
     * Save the settings in database. Will overwrite existing entries,
     * cause only one entry is allowed in database.
     * @param settings the new settings.
     */
    public void saveSettings(Settings settings);
}
