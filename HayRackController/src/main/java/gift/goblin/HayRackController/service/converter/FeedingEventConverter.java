/*
 * Copyright (C) 2020 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.service.converter;

import gift.goblin.HayRackController.controller.model.FoodConsumption;
import gift.goblin.HayRackController.database.model.event.FeedingEvent;
import gift.goblin.HayRackController.service.tools.DateAndTimeUtil;
import gift.goblin.HayRackController.service.tools.NumberConverterUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Converts feeding-event entities to dtos and vice versa.
 * @author andre
 */
@Service
public class FeedingEventConverter {
    
    @Autowired
    private NumberConverterUtil numberConverterUtil;
    
    @Autowired
    private DateAndTimeUtil dateAndTimeUtil;
    
    public FoodConsumption convertToFoodConsumption(FeedingEvent entity) {
        
        int feedingDurationMinutes = 0;
        
        if (entity.getFeedingDurationMs() != null) {
            feedingDurationMinutes = (int) (entity.getFeedingDurationMs() / 1000 / 60);
        }
        
        double consumedFoodKg = numberConverterUtil.convertGramsToKg(entity.getFoodConsumptionGram());
        String readableStartDateTime = dateAndTimeUtil.convertToReadableDateTime(entity.getFeedingStart());
        return new FoodConsumption(readableStartDateTime, consumedFoodKg, feedingDurationMinutes);
    }
    
    
}
