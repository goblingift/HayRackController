/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gift.goblin.HayRackController.controller;

import gift.goblin.HayRackController.controller.dto.CalendarEvent;
import gift.goblin.HayRackController.database.event.TemperatureMeasurementService;
import gift.goblin.HayRackController.database.event.model.TemperatureMeasurement;
import gift.goblin.HayRackController.service.io.WebcamDeviceService;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
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
 * Controller for the detailed view of the temperature measurements.
 * @author andre
 */
@Controller
public class TemperatureController {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private TemperatureMeasurementService temperatureMeasurementService;
    
    @Autowired
    private WebcamDeviceService webcamService;
    
    @GetMapping(value = "/temperature")
    public String renderDashboard(Model model) {
        
        model.addAttribute("temperature", temperatureMeasurementService.getLatestMeasurement());
        model.addAttribute("webcam_count", webcamService.getWebcamCount());
        return "temperature";
    }
    
    /**
     * Returns a list of all temperature events for the given date period.
     * @param startDate From this date we want to fetch all temperature events.
     * @param endDate Until this date we want to fetch all temperature events.
     * @return List with all found temperate events. Or an empty List.
     */
    @GetMapping(value = "/temperature/events")
    @ResponseBody
    public List<CalendarEvent> getEvents(@RequestParam(value = "start", required = false) String startDate,
            @RequestParam(value = "end", required = false) String endDate) {
        
        logger.info("The calendar plugin wants to get results for: start={}, end={}", startDate, endDate);
        
        List<TemperatureMeasurement> temperatureMeasurements = temperatureMeasurementService.getTemperatureMeasurements(LocalDate.of(2019, Month.JANUARY, 1),
                LocalDate.of(2019, Month.MARCH, 1));

        List<CalendarEvent> calendarEvents = temperatureMeasurements.stream()
                .map(tm -> new CalendarEvent("high", DateTimeFormatter.ISO_DATE.format(tm.getMeasuredAt())))
                .collect(Collectors.toList());
        
        return calendarEvents;
    }

    
}
