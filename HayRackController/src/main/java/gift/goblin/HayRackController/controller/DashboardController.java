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
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private SecurityService securityService;

    @Autowired
    private ShutterController shutterController;

    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public String renderDashboard(Model model) {

        String username = securityService.getUsernameOfCurrentUser();
        model.addAttribute("username", username);
        System.out.println("Added username to model:" + username);

        model.addAttribute("shutterMovement", new ShutterMovement(ShutterMovement.DIRECTION_DOWN, 500));
        return "dashboard";
    }

    @RequestMapping(value = "/dashboard/shutters-up", method = RequestMethod.GET)
    public String shuttersUp(Model model) {

        // todo: Doing fancy stuff with raspberry pi, to roll up the shutters
        System.out.println("SHUTTER GOES UP!");

        try {
            shutterController.openShutter();
        } catch (InterruptedException ex) {
            System.out.println("InterruptedException thrown while called shuttersUp!");
        }

        return "dashboard";
    }

    @RequestMapping(value = "/dashboard/shutters-down", method = RequestMethod.GET)
    public String shuttersDown(Model model) {

        // todo: Doing fancy stuff with raspberry pi, to roll down the shutters
        System.out.println("SHUTTER GOES DOWN!");

        try {
            shutterController.closeShutter();
        } catch (InterruptedException ex) {
            System.out.println("InterruptedException thrown while called shuttersDown!");
        }

        return "dashboard";
    }

    @RequestMapping(value = "/dashboard/shutters-move-custom", method = RequestMethod.POST)
    public String shuttersMovementCustom(@ModelAttribute("shutterMovement") ShutterMovement shutterMovement,
            Model model) throws InterruptedException {

        System.out.println("Triggered custom shutdown method!");

        if (shutterMovement.directionIsDown()) {
            shutterController.closeShutter(shutterMovement.getDuration());
        } else if (shutterMovement.directionIsUp()) {
            // todo
        }

        return "dashboard";
    }

}
