/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.service.configuration;

import gift.goblin.HayRackController.controller.model.LoadCellSettings;
import gift.goblin.HayRackController.controller.model.SoundSettings;
import gift.goblin.HayRackController.database.embedded.repo.configuration.ApplicationConfigurationRepository;
import gift.goblin.HayRackController.database.model.configuration.ApplicationConfiguration;
import gift.goblin.HayRackController.database.model.configuration.ConfigurationType;
import gift.goblin.HayRackController.service.converter.SettingsConverter;
import gift.goblin.HayRackController.service.io.model.Playlist;
import java.time.LocalDateTime;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service bean which implements several methods to read and write the
 * application configuration. (e.g. properties as selected sounds)
 *
 * @author andre
 */
@Service
public class ConfigurationServiceImpl implements ConfigurationService {

    @Autowired
    ApplicationConfigurationRepository repo;

    @Autowired
    SettingsConverter settingsConverter;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Optional<Playlist> getSelectedSound() {

        try {
            Optional<ApplicationConfiguration> configEntity = repo.findAll()
                    .stream()
                    .filter(ac -> ac.getConfigurationId() == ConfigurationType.SOUND_MELODY.getId())
                    .findFirst();
            if (configEntity.isPresent()) {

                int soundId = Integer.parseInt(configEntity.get().getValue());
                Optional<Playlist> optEntry = Playlist.findById(soundId);
                return optEntry;
            }
        } catch (Exception e) {
            logger.error("Exception thrown while read selected sound: {}", e.getMessage());
            return Optional.empty();
        }

        logger.error("No configuration entry in database found!");
        return Optional.empty();
    }

    @Override
    public SoundSettings getSoundSettings() {

        Optional<ApplicationConfiguration> configEntity = repo.findAll().stream().findFirst();
        if (configEntity.isPresent()) {
            ApplicationConfiguration entity = configEntity.get();
            SoundSettings settings = settingsConverter.toFormDto(entity);
            return settings;
        } else {
            SoundSettings defaultSettings = new SoundSettings();
            defaultSettings.setSelectedSound("99");
            return defaultSettings;
        }
    }

    @Override
    public LoadCellSettings getLoadCellSettings() {

        LoadCellSettings loadCellSettings = new LoadCellSettings();

        Optional<ApplicationConfiguration> optLoadCellsActivated = repo.findById(ConfigurationType.LOADCELLS_ACTIVATED.getId());
        if (optLoadCellsActivated.isPresent()) {
            loadCellSettings.setEnabled(Boolean.parseBoolean(optLoadCellsActivated.get().getValue()));
        }

        Optional<ApplicationConfiguration> optLoadCellsAmount = repo.findById(ConfigurationType.LOADCELLS_AMOUNT.getId());
        if (optLoadCellsAmount.isPresent()) {
            loadCellSettings.setAmount(Integer.parseInt(optLoadCellsAmount.get().getValue()));
        }
        
        return loadCellSettings;
    }

    @Override
    public void saveSettings(SoundSettings settings) {
        saveOrUpdateSetting(ConfigurationType.SOUND_MELODY.getId(),
                settings.getSelectedSound(), ConfigurationType.SOUND_MELODY.name());
    }

    /**
     * Try to find a configuration entity by the given id and updates it.
     * Otherwise it will create a new entity.
     *
     * @param settingsId The unique id for the entity.
     * @param value The value for this setting.
     * @param description Description for the setting. Will be stored in
     * database.
     * @return true if successful, false if otherwise.
     */
    private boolean saveOrUpdateSetting(int settingsId, String value, String description) {

        boolean success = true;

        try {
            Optional<ApplicationConfiguration> optConfigEntity = repo.findById(settingsId);
            if (optConfigEntity.isPresent()) {
                ApplicationConfiguration configEntity = optConfigEntity.get();

                configEntity.setValue(value);
                configEntity.setLastModified(LocalDateTime.now());

                repo.save(configEntity);
                logger.info("Successful updated application configuration ({}): {}",
                        description, configEntity);
            } else {
                ApplicationConfiguration configEntity = new ApplicationConfiguration(settingsId, description, value, LocalDateTime.now());
                
                repo.save(configEntity);
                logger.info("Successful saved new application configuration ({}): {}",
                        description, configEntity);
            }
        } catch (Exception e) {
            success = false;
            logger.error("Exception occurs while try to saveOrUpdateSetting for: " + description, e);
        }

        return success;
    }

    @Override
    public void saveSettings(LoadCellSettings settings) {
        saveOrUpdateSetting(ConfigurationType.LOADCELLS_ACTIVATED.getId(),
                String.valueOf(settings.isEnabled()), ConfigurationType.LOADCELLS_ACTIVATED.name());

        saveOrUpdateSetting(ConfigurationType.LOADCELLS_AMOUNT.getId(),
                String.valueOf(settings.getAmount()), ConfigurationType.LOADCELLS_AMOUNT.name());
    }

}
