/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gift.goblin.HayRackController.controller;

import gift.goblin.HayRackController.service.security.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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

    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public String renderDashboard(Model model) {

        String username = securityService.getUsernameOfCurrentUser();
        model.addAttribute("username", username);
        System.out.println("Added username to model:" + username);

        return "dashboard";
    }

    @RequestMapping(value = "/dashboard/shutters-up", method = RequestMethod.GET)
    public String shuttersUp(Model model) {

        // todo: Doing fancy stuff with raspberry pi, to roll up the shutters
        System.out.println("SHUTTER GOES UP!");

        return "dashboard";
    }

    @RequestMapping(value = "/dashboard/shutters-down", method = RequestMethod.GET)
    public String shuttersDown(Model model) {

        // todo: Doing fancy stuff with raspberry pi, to roll down the shutters
        System.out.println("SHUTTER GOES DOWN!");

        return "dashboard";
    }

}
