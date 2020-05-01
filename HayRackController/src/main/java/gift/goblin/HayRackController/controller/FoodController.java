/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.controller;

import gift.goblin.HayRackController.controller.model.CalendarEvent;
import gift.goblin.HayRackController.controller.model.FoodConsumption;
import gift.goblin.HayRackController.controller.model.FoodInformation;
import gift.goblin.HayRackController.controller.model.WeightDetailsDto;
import gift.goblin.HayRackController.database.backup.repo.event.TemperatureMeasurementBackupRepository;
import gift.goblin.HayRackController.database.embedded.repo.event.FeedingEventRepository;
import gift.goblin.HayRackController.database.model.event.FeedingEvent;
import gift.goblin.HayRackController.service.event.TemperatureDailyMaxMinService;
import gift.goblin.HayRackController.service.event.TemperatureMeasurementService;
import gift.goblin.HayRackController.database.model.event.TemperatureMeasurement;
import gift.goblin.HayRackController.service.converter.FeedingEventConverter;
import gift.goblin.HayRackController.service.io.WebcamDeviceService;
import gift.goblin.HayRackController.service.io.dto.TemperatureAndHumidity;
import gift.goblin.HayRackController.service.tools.NumberConverterUtil;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller for the food consumption view.
 *
 * @author andre
 */
@Controller
public class FoodController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WebcamDeviceService webcamService;

    @Autowired
    private WeightController weightController;

    @Autowired
    FeedingEventRepository feedingEventRepo;

    @Autowired
    FeedingEventConverter feedingEventConverter;
    
    @Autowired
    NumberConverterUtil numberConverterUtil;
    
    @GetMapping(value = "/food")
    public String renderFoodOverview(Model model) throws IOException {
        
        model.addAttribute("foodInformation", getFoodInformations());
        model.addAttribute("webcam_count", webcamService.getWebcamCount());
        return "food";
    }

    private FoodInformation getFoodInformations() throws IOException {

        FoodInformation foodInformation = new FoodInformation();

        WeightDetailsDto measuredLoadCells = weightController.measureLoadCells();
        double weightSumKg = measuredLoadCells.getWeightSumKg();
        
        String roundedWeightSumKg = numberConverterUtil.roundDoubleTwoDecimals(weightSumKg);
        
        foodInformation.setRemainingFoodKg(roundedWeightSumKg);
        
        foodInformation.setLast10Consumptions(getLast10FoodConsumptions());
        
        return foodInformation;
    }

    private List<FoodConsumption> getLast10FoodConsumptions() {
        List<FeedingEvent> lastFeedingEvents = feedingEventRepo.findTop10ByOrderByFeedingStartDesc();
        logger.info("Result of feedingEventRepo.findTop10ByOrderByFeedingStartDesc: {}", lastFeedingEvents);
        
        List<FoodConsumption> last10FoodConsumptions = lastFeedingEvents.stream()
                .map(fe -> feedingEventConverter.convertToFoodConsumption(fe))
                .collect(Collectors.toList());
        
        return last10FoodConsumptions;
    }

}
