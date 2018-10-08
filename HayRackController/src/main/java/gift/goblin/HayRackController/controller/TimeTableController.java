/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gift.goblin.HayRackController.controller;

import gift.goblin.HayRackController.database.security.model.ScheduledShutterMovement;
import gift.goblin.HayRackController.database.security.model.User;
import gift.goblin.HayRackController.service.timetable.ScheduledShutterMovementService;
import gift.goblin.HayRackController.view.model.ScheduledShutterMovementDto;
import gift.goblin.HayRackController.view.model.ShutterMovement;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
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

    @RequestMapping(value = "/timetable", method = RequestMethod.GET)
    public String renderTimetable(Model model) {
        
        logger.info("Rendering timetable...");

        List<ScheduledShutterMovement> scheduledMovements = scheduledShutterMovementService.readAllStoredShutterMovementSchedules();
        logger.info("Scheduled movements({})", scheduledMovements.size());
        
        for (ScheduledShutterMovement actSchedule : scheduledMovements) {
            logger.info(actSchedule.toString());
        }
        
        logger.info("Mapped shutter movements...");
        
        List<ScheduledShutterMovementDto> shutterMovementDtos = scheduledMovements.stream().map((ScheduledShutterMovement s) -> new ScheduledShutterMovementDto(s.isIsActive(),
                s.getOpenAt().toString(), s.getCloseAt().toString(), s.getComment(), s.getCreatedBy(), s.getCreatedAt().toString()))
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

        return renderTimetable(model);
    }

}
