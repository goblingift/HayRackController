/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.service.converter;

import gift.goblin.HayRackController.controller.model.SoundSettings;
import gift.goblin.HayRackController.database.model.configuration.ApplicationConfiguration;
import org.springframework.stereotype.Service;

/**
 * Converter service, to convert setting-object (Database-dto and form-dto).
 * @author andre
 */
@Service
public class SettingsConverter {
    
    public SoundSettings toFormDto(ApplicationConfiguration applicationConfiguration) {
        SoundSettings returnValue = new SoundSettings();
        returnValue.setSelectedSound(String.valueOf(applicationConfiguration.getSoundId()));
        
        return returnValue;
    }
    
    public ApplicationConfiguration toDatabaseDto(SoundSettings formDto) {
        ApplicationConfiguration returnValue = new ApplicationConfiguration();
        returnValue.setSoundId(Integer.parseInt(formDto.getSelectedSound()));
        
        return returnValue;
    }
    
}
