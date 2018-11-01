/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gift.goblin.HayRackController.controller;

import gift.goblin.HayRackController.service.io.ShutterController;
import gift.goblin.HayRackController.service.security.SecurityService;
import gift.goblin.HayRackController.view.model.ShutterMovement;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller which offers endpoints for all actions regarding the user
 * dashboard.
 *
 * @author andre
 */
@Controller
public class DashboardController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SecurityService securityService;

    @Autowired
    private ShutterController shutterController;
    
    @Autowired
    private BuildProperties buildProperties;

    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public String renderDashboard(Model model) {

        String username = securityService.getUsernameOfCurrentUser();
        model.addAttribute("your-username", username);
        
        logger.info("v: {}", buildProperties.getVersion());
        model.addAttribute("maven-version", buildProperties.getVersion());

        model.addAttribute("shutterMovement", new ShutterMovement(ShutterMovement.DIRECTION_DOWN, 500));
        return "dashboard";
    }

    @RequestMapping(value = "/dashboard/shutters-up", method = RequestMethod.GET)
    public String shuttersUp(@ModelAttribute("shutterMovement") ShutterMovement shutterMovement,
            Model model) {

        // todo: Doing fancy stuff with raspberry pi, to roll up the shutters
        try {
            shutterController.openShutter();
        } catch (InterruptedException ex) {
            logger.warn("Exception thrown while opening shutters!", ex);
        }

        return "dashboard";
    }

    @RequestMapping(value = "/dashboard/shutters-down", method = RequestMethod.GET)
    public String shuttersDown(@ModelAttribute("shutterMovement") ShutterMovement shutterMovement,
            Model model) throws InterruptedException {

        logger.info("Manually triggered shutters down.");
        // todo: Doing fancy stuff with raspberry pi, to roll down the shutters

        shutterController.closeShutter();

        return "dashboard";
    }

    @RequestMapping(value = "/dashboard/shutters-move-custom", method = RequestMethod.POST)
    public String shuttersMovementCustom(@ModelAttribute("shutterMovement") ShutterMovement shutterMovement,
            Model model) throws InterruptedException {
        
        logger.info("Manually triggered custom shutters movement, with param: {}", shutterMovement);

        if (shutterMovement.directionIsDown()) {
            shutterController.closeShutter(shutterMovement.getDuration());
        } else if (shutterMovement.directionIsUp()) {
            shutterController.openShutter(shutterMovement.getDuration());
        }

        return "dashboard";
    }

}
