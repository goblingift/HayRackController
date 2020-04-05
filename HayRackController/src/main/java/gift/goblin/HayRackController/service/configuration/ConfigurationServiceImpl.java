/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.service.configuration;

import gift.goblin.HayRackController.controller.model.SoundSettings;
import gift.goblin.HayRackController.database.embedded.repo.configuration.ApplicationConfigurationRepository;
import gift.goblin.HayRackController.database.model.configuration.ApplicationConfiguration;
import gift.goblin.HayRackController.service.converter.SettingsConverter;
import gift.goblin.HayRackController.service.io.model.Playlist;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service bean which implements several methods to read and write the application
 * configuration. (e.g. properties as selected sounds)
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
    public void saveSelectedSound(Playlist selectedSound) {
        
        Optional<ApplicationConfiguration> configEntity = repo.findAll().stream().findFirst();
        if (configEntity.isPresent()) {
            ApplicationConfiguration applicationConfiguration = configEntity.get();
            applicationConfiguration.setSoundId(selectedSound.getId());
            repo.save(applicationConfiguration);
            logger.info("Successful updated application configuration: Changed sound to {}",
                    selectedSound.getTitle());
        } else {
            ApplicationConfiguration applicationConfiguration = new ApplicationConfiguration(selectedSound.getId());
            repo.save(applicationConfiguration);
            logger.info("Successful saved new application configuration: Sound set to {}",
                    selectedSound.getTitle());
        }
    }

    @Override
    public Optional<Playlist> getSelectedSound() {
        
        try {
            Optional<ApplicationConfiguration> configEntity = repo.findAll().stream().findFirst();
            if (configEntity.isPresent()) {

                int soundId = configEntity.get().getSoundId();
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
    public SoundSettings getSettings() {
        
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
    public void saveSettings(SoundSettings settings) {
        
        
        Optional<ApplicationConfiguration> configEntity = repo.findAll().stream().findFirst();
        if (configEntity.isPresent()) {
            ApplicationConfiguration applicationConfiguration = configEntity.get();
            
            applicationConfiguration.setSoundId(Integer.parseInt(settings.getSelectedSound()));
            repo.save(applicationConfiguration);
            logger.info("Successful updated application configuration: {}",
                    applicationConfiguration);
        } else {
            ApplicationConfiguration convertedEntity = settingsConverter.toDatabaseDto(settings);
            repo.save(convertedEntity);
            logger.info("Successful saved new application configuration: {}",
                    convertedEntity);
        }
    }
    
}
