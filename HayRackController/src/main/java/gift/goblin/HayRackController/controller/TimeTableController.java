/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gift.goblin.HayRackController.controller;

import gift.goblin.HayRackController.database.event.model.ScheduledShutterMovement;
import gift.goblin.HayRackController.service.io.WebcamDeviceService;
import gift.goblin.HayRackController.service.scheduled.SchedulerJobService;
import gift.goblin.HayRackController.database.event.ScheduledShutterMovementService;
import gift.goblin.HayRackController.service.tools.DateAndTimeUtil;
import gift.goblin.HayRackController.view.model.ScheduledShutterMovementDto;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
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
    private SchedulerJobService schedulerJobService;

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private DateAndTimeUtil dateAndTimeUtil;
    
    @Autowired
    private WebcamDeviceService webcamService;

    @RequestMapping(value = "/timetable", method = RequestMethod.GET)
    public String renderTimetable(Model model) {

        List<ScheduledShutterMovement> scheduledMovements = scheduledShutterMovementService.readAllStoredShutterMovementSchedules();

        for (ScheduledShutterMovement actSchedule : scheduledMovements) {
            logger.info(actSchedule.toString());
        }

        List<ScheduledShutterMovementDto> shutterMovementDtos = scheduledMovements.stream().map((ScheduledShutterMovement s) -> new ScheduledShutterMovementDto(s.getId().toString(),
                s.isIsActive(), s.getFeedingStartTime().toString(), s.getFeedingDuration().toString(), s.getComment(), s.getCreatedBy(), s.getCreatedAt().toString()))
                .collect(Collectors.toList());

        model.addAttribute("scheduledMovements", shutterMovementDtos);
        model.addAttribute("newMovement", new ScheduledShutterMovement(LocalTime.now(), 60, "-comment-"));
        model.addAttribute("webcam_count", webcamService.getWebcamCount());
        return "timetable";
    }

    @RequestMapping(value = "/timetable/add", method = RequestMethod.POST)
    public String addNewSchedule(@ModelAttribute("newMovement") ScheduledShutterMovement newMovement, BindingResult bindingResult, Model model) {

        logger.info("Called adding new schedule with object: {}", newMovement);

        Long newShutterMovementId = scheduledShutterMovementService
                .addNewShutterMovement(newMovement.getFeedingStartTime(), newMovement.getFeedingDuration(), newMovement.getComment());

        registerShutdownSchedule(newMovement.getFeedingStartTime(), newShutterMovementId, newMovement.getComment());

        return renderTimetable(model);
    }

    @RequestMapping(value = "/timetable/delete/{id}", method = RequestMethod.GET)
    public String deleteEntry(@PathVariable("id") String id, Model model) {

        scheduledShutterMovementService.deleteScheduledMovement(Long.valueOf(id));
        schedulerJobService.deleteStartFeedingJob(Integer.valueOf(id));
        
        return renderTimetable(model);
    }

    private void registerShutdownSchedule(LocalTime localTime, Long schedulerId, String triggerDescription) {

        LocalDateTime nextExecutionDateTime = dateAndTimeUtil.getNextExecutionDateTime(localTime);

        ZonedDateTime zdt = nextExecutionDateTime.atZone(ZoneId.systemDefault());
        Date nextExecutionDate = Date.from(zdt.toInstant());

        JobDetail jobDetail = schedulerJobService.createStartFeedingJob(schedulerId.intValue());
        SimpleTrigger newTrigger = schedulerJobService.createStartFeedingTrigger(schedulerId.intValue(), triggerDescription, nextExecutionDate, jobDetail);

        try {
            scheduler.scheduleJob(jobDetail, newTrigger);
            logger.info("Successful registered scheduler for shutdown shutters. Next execution:" + nextExecutionDate);
        } catch (SchedulerException ex) {
            logger.error("Couldnt register new scheduled job!", ex);
        }

    }

}
