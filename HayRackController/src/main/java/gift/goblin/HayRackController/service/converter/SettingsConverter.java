/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.service.converter;

import gift.goblin.HayRackController.controller.model.SoundSettings;
import gift.goblin.HayRackController.database.model.configuration.ApplicationConfiguration;
import gift.goblin.HayRackController.database.model.configuration.ConfigurationType;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

/**
 * Converter service, to convert settings-object into form-dto and vice versa.
 * @author andre
 */
@Service
public class SettingsConverter {
    
    public SoundSettings toFormDto(ApplicationConfiguration applicationConfiguration) {
        SoundSettings returnValue = new SoundSettings();
        returnValue.setSelectedSound(applicationConfiguration.getValue());
        
        return returnValue;
    }
    
    public ApplicationConfiguration toDatabaseDto(SoundSettings formDto) {
        ApplicationConfiguration returnValue = new ApplicationConfiguration();
        returnValue.setValue(formDto.getSelectedSound());
        returnValue.setDescription(ConfigurationType.SOUND_MELODY.name());
        returnValue.setConfigurationId(ConfigurationType.SOUND_MELODY.getId());
        returnValue.setLastModified(LocalDateTime.now());
        
        return returnValue;
    }
    
}
