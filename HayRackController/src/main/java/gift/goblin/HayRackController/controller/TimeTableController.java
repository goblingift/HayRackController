/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gift.goblin.HayRackController.controller;

import gift.goblin.HayRackController.database.security.model.ScheduledShutterMovement;
import gift.goblin.HayRackController.service.scheduled.ShutterDownJob;
import gift.goblin.HayRackController.service.timetable.ScheduledShutterMovementService;
import gift.goblin.HayRackController.view.model.ScheduledShutterMovementDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller for the timetable view, where you can see the scheduled timings
 * for the shutter opening and closing times.
 *
 * @author andre
 */
@Controller
public class TimeTableController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ScheduledShutterMovementService scheduledShutterMovementService;

    @Autowired
    Scheduler scheduler;

    @RequestMapping(value = "/timetable", method = RequestMethod.GET)
    public String renderTimetable(Model model) {

        logger.info("Rendering timetable...");

        List<ScheduledShutterMovement> scheduledMovements = scheduledShutterMovementService.readAllStoredShutterMovementSchedules();
        logger.info("Scheduled movements({})", scheduledMovements.size());

        for (ScheduledShutterMovement actSchedule : scheduledMovements) {
            logger.info(actSchedule.toString());
        }

        logger.info("Mapped shutter movements...");

        List<ScheduledShutterMovementDto> shutterMovementDtos = scheduledMovements.stream().map((ScheduledShutterMovement s) -> new ScheduledShutterMovementDto(s.getId().toString(),
                s.isIsActive(), s.getOpenAt().toString(), s.getCloseAt().toString(), s.getComment(), s.getCreatedBy(), s.getCreatedAt().toString()))
                .collect(Collectors.toList());
        logger.info("Number of mapped ones:" + shutterMovementDtos.size());

        for (ScheduledShutterMovementDto actDto : shutterMovementDtos) {
            logger.info(actDto.toString());
        }

        model.addAttribute("scheduledMovements", shutterMovementDtos);
        model.addAttribute("newMovement", new ScheduledShutterMovement(LocalTime.now(), LocalTime.now(), "-comment-"));
        return "timetable";
    }

    @RequestMapping(value = "/timetable/add", method = RequestMethod.POST)
    public String addNewSchedule(@ModelAttribute("newMovement") ScheduledShutterMovement newMovement, BindingResult bindingResult, Model model) {

        logger.info("Called adding new schedule with object: {}", newMovement);

        scheduledShutterMovementService.addNewShutterMovement(newMovement.getOpenAt(), newMovement.getCloseAt(), newMovement.getComment());

        scheduler.
        
        return renderTimetable(model);
    }

    @RequestMapping(value = "/timetable/delete/{id}", method = RequestMethod.GET)
    public String deleteEntry(@PathVariable("id") String id, Model model) {

        scheduledShutterMovementService.deleteScheduledMovement(Long.valueOf(id));
        return renderTimetable(model);
    }

    private void registerShutdownSchedule(String time) {

        LocalTime localTime = LocalTime.parse(time);
        LocalDateTime nextExecutionDateTime = getNextExecutionDateTime(localTime);

        ZonedDateTime zdt = nextExecutionDateTime.atZone(ZoneId.systemDefault());
        Date nextExecutionDate = Date.from(zdt.toInstant());

        JobDetail newjobDetail = JobBuilder.newJob().ofType(ShutterDownJob.class)
                .storeDurably()
                .withIdentity("Quartz_shutter_down_job")
                .withDescription("Scheduler for closing shutters.")
                .build();

        SimpleTrigger trigger = TriggerBuilder.newTrigger().forJob(newjobDetail)
                .withIdentity("Quartz_Trigger")
                .withDescription("Daily trigger")
                .startAt(nextExecutionDate)
                .withSchedule(simpleSchedule().repeatForever().withIntervalInHours(24))
                .build();
        
        try {
            scheduler.scheduleJob(newjobDetail, trigger);
        } catch (SchedulerException ex) {
            logger.error("Couldnt register new scheduled job!", ex);
        }
        
        logger.info("Successful registered scheduler for shutdown shutters. Next execution:" + nextExecutionDate);
    }

    private LocalDateTime getNextExecutionDateTime(LocalTime localTime) {

        if (localTime.isBefore(LocalTime.now())) {
            // Next execution will be tomorrow
            LocalDateTime nextExecutionTomorrow = LocalDateTime.of(LocalDate.now().plusDays(1), localTime);
            logger.debug("Next execution will be tomorrow: {}", nextExecutionTomorrow);
            return nextExecutionTomorrow;
        } else {
            // Next execution will be today
            LocalDateTime nextExecutionToday = LocalDateTime.of(LocalDate.now(), localTime);
            logger.debug("Next execution will be today: {}", nextExecutionToday);
            return nextExecutionToday;
        }

    }

}
