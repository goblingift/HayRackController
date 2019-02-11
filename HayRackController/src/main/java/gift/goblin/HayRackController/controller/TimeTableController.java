/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gift.goblin.HayRackController.controller;

import gift.goblin.HayRackController.database.model.event.ScheduledShutterMovement;
import gift.goblin.HayRackController.service.io.WebcamDeviceService;
import gift.goblin.HayRackController.service.scheduled.SchedulerJobService;
import gift.goblin.HayRackController.service.event.ScheduledShutterMovementService;
import gift.goblin.HayRackController.database.embedded.repo.event.ScheduledShutterMovementRepository;
import gift.goblin.HayRackController.service.tools.DateAndTimeUtil;
import gift.goblin.HayRackController.view.model.ScheduledShutterMovementDto;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
    ScheduledShutterMovementRepository repo;

    @Autowired
    private SchedulerJobService schedulerJobService;

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private DateAndTimeUtil dateAndTimeUtil;
    
    @Autowired
    private WebcamDeviceService webcamService;

    @GetMapping(value = "/timetable")
    public String renderTimetable(Model model) {

        List<ScheduledShutterMovement> scheduledMovements = scheduledShutterMovementService.readAllStoredShutterMovementSchedules();

        for (ScheduledShutterMovement actSchedule : scheduledMovements) {
            logger.info(actSchedule.toString());
        }

        List<ScheduledShutterMovementDto> shutterMovementDtos = scheduledMovements.stream().map((ScheduledShutterMovement s) -> new ScheduledShutterMovementDto(s.getId().toString(),
                s.isIsActive(), s.getFeedingStartTime().toString(), s.getFeedingDuration().toString(), s.getCreatedBy(), s.getCreatedAt().toString()))
                .collect(Collectors.toList());

        model.addAttribute("scheduledMovements", shutterMovementDtos);
        model.addAttribute("newMovement", new ScheduledShutterMovement(LocalTime.now(), 60));
        model.addAttribute("webcam_count", webcamService.getWebcamCount());
        return "timetable";
    }

    @PostMapping(value = "/timetable/add")
    public String addNewSchedule(@ModelAttribute("newMovement") ScheduledShutterMovement newMovement, BindingResult bindingResult, Model model) {

        logger.info("Called adding new schedule with object: {}", newMovement);

        // Add database entry
        Long newShutterMovementId = scheduledShutterMovementService
                .addNewShutterMovement(newMovement.getFeedingStartTime(), newMovement.getFeedingDuration());

        // Register scheduled job
        registerStartFeedingJob(newMovement.getFeedingStartTime(), newShutterMovementId);
        
        model.addAttribute("added_time", newMovement.getFeedingStartTime());
        model.addAttribute("success_message", "timetable.addNew.success");

        return renderTimetable(model);
    }

    @GetMapping(value = "/timetable/delete/{id}")
    public String deleteEntry(@PathVariable("id") String id, Model model) {
        
        try {
            Optional<ScheduledShutterMovement> optScheduledShutterMovement = repo.findById(Long.valueOf(id));
            if (optScheduledShutterMovement.isPresent()) {
                LocalTime feedingStartTime = optScheduledShutterMovement.get().getFeedingStartTime();
                
                scheduledShutterMovementService.deleteScheduledMovement(Long.valueOf(id));
                schedulerJobService.deleteStartFeedingJob(Integer.valueOf(id));
                
                model.addAttribute("deleted_time", feedingStartTime);
                model.addAttribute("success_message", "timetable.delete.success");
            }
            
        } catch (Exception e) {
            logger.error("Exception thrown while try to delete scheduled entry with id: {}", id);
        }
        
        return renderTimetable(model);
    }

    private void registerStartFeedingJob(LocalTime localTime, Long schedulerId) {

        LocalDateTime nextExecutionDateTime = dateAndTimeUtil.getNextExecutionDateTime(localTime);

        ZonedDateTime zdt = nextExecutionDateTime.atZone(ZoneId.systemDefault());
        Date nextExecutionDate = Date.from(zdt.toInstant());

        JobDetail jobDetail = schedulerJobService.createStartFeedingJob(schedulerId.intValue());
        SimpleTrigger newTrigger = schedulerJobService.createStartFeedingTrigger(schedulerId.intValue(), nextExecutionDate, jobDetail);

        try {
            scheduler.scheduleJob(jobDetail, newTrigger);
            logger.info("Successful registered scheduler for shutdown shutters. Next execution:" + nextExecutionDate);
        } catch (SchedulerException ex) {
            logger.error("Couldnt register new scheduled job!", ex);
        }

    }

}
