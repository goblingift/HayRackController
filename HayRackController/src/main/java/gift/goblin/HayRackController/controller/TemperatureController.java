/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.controller;

import gift.goblin.HayRackController.controller.model.CalendarEvent;
import gift.goblin.HayRackController.database.backup.repo.event.TemperatureMeasurementBackupRepository;
import gift.goblin.HayRackController.service.event.TemperatureDailyMaxMinService;
import gift.goblin.HayRackController.service.event.TemperatureMeasurementService;
import gift.goblin.HayRackController.database.model.event.TemperatureMeasurement;
import gift.goblin.HayRackController.service.io.WebcamDeviceService;
import gift.goblin.HayRackController.service.io.dto.TemperatureAndHumidity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.List;
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
 * Controller for the detailed view of the temperature measurements.
 *
 * @author andre
 */
@Controller
public class TemperatureController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final String DATEFORMAT_DATE = "yyyy-MM-dd";

    @Autowired
    private TemperatureMeasurementService temperatureMeasurementService;

    @Autowired
    private TemperatureDailyMaxMinService temperatureDailyMaxMinService;

    @Autowired
    private WebcamDeviceService webcamService;
    
    @GetMapping(value = "/temperature")
    public String renderTemperatureOverview(Model model) {

        TemperatureAndHumidity latestMeasurement = temperatureMeasurementService.getLatestMeasurement();
        if (latestMeasurement != null) {
            model.addAttribute("temperature", latestMeasurement);
        } else {
            model.addAttribute("temperature", new TemperatureAndHumidity(99, 99, 99));
        }

        model.addAttribute("webcam_count", webcamService.getWebcamCount());
        return "temperature";
    }
    
    /**
     * Returns a list of all temperature events for the given date period.
     *
     * @param startDate From this date we want to fetch all temperature events.
     * @param endDate Until this date we want to fetch all temperature events.
     * @return List with all found temperate events. Or an empty List.
     */
    @GetMapping(value = "/temperature/events")
    @ResponseBody
    public List<CalendarEvent> getEvents(@RequestParam(value = "start", required = false) String startDate,
            @RequestParam(value = "end", required = false) String endDate) {

        LocalDate startDateParsed = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDate endDateParsed = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);

        List<TemperatureMeasurement> temperatureMeasurements = temperatureMeasurementService.getTemperatureMeasurements(startDateParsed, endDateParsed);

        List<CalendarEvent> calendarEvents = temperatureMeasurements.stream()
                .map(tm -> new CalendarEvent(DateTimeFormatter.ISO_DATE_TIME.format(tm.getMeasuredAt()),
                        DateTimeFormatter.ISO_DATE.format(tm.getMeasuredAt())))
                .collect(Collectors.toList());

        return calendarEvents;
    }

    /**
     * Returns a list of the daily highest and lowest temperature events for the
     * given timespan.
     *
     * @param startDate first day to fetch.
     * @param endDate last day to fetch.
     * @return list with two event objects for each day- one for highest
     * measurement and one for lowest measurement.
     */
    @GetMapping(value = "/temperature/events/max-and-min")
    @ResponseBody
    public List<CalendarEvent> getEventsMaxMin(@RequestParam(value = "start", required = false) String startDate,
            @RequestParam(value = "end", required = false) String endDate) {

        logger.debug("The calendar plugin wants to get max and min results for: start={}, end={}", startDate, endDate);

        LocalDate startDateParsed = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDate endDateParsed = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);

        List<TemperatureMeasurement> minTemperatures = temperatureDailyMaxMinService.getMinTemperatures(startDateParsed, endDateParsed);
        logger.debug("Found {} minimum-temperature entries.", minTemperatures.size());

        List<TemperatureMeasurement> maxTemperatures = temperatureDailyMaxMinService.getMaxTemperatures(startDateParsed, endDateParsed);
        logger.debug("Found {} maximum-temperature entries.", maxTemperatures.size());

        List<CalendarEvent> minCalendarEvents = minTemperatures.stream()
                .map(tm -> new CalendarEvent("MIN: " + tm.getTemperature(), DateTimeFormatter.ISO_DATE.format(tm.getMeasuredAt()),
                        CalendarEvent.COLOR_LOWEST_TEMP, CalendarEvent.DEFAULT_TEXTCOLOR))
                .collect(Collectors.toList());

        List<CalendarEvent> maxCalendarEvents = maxTemperatures.stream()
                .map(tm -> new CalendarEvent("MAX: " + tm.getTemperature(), DateTimeFormatter.ISO_DATE.format(tm.getMeasuredAt()),
                        CalendarEvent.COLOR_HIGHEST_TEMP, CalendarEvent.DEFAULT_TEXTCOLOR))
                .collect(Collectors.toList());

        List<CalendarEvent> allEvents = Stream.concat(minCalendarEvents.stream(), maxCalendarEvents.stream())
                .collect(Collectors.toList());

        return allEvents;
    }

}
