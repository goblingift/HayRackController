/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gift.goblin.HayRackController.controller;

import gift.goblin.HayRackController.service.io.IOController;
import gift.goblin.HayRackController.service.io.WebcamDeviceService;
import gift.goblin.HayRackController.controller.model.ShutterMovement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Controller for the manual shutter control page.
 *
 * @author andre
 */
@Controller
public class ManualShutterController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WebcamDeviceService webcamService;

    @Autowired
    private IOController shutterController;

    /**
     * Default render method.
     *
     * @param model model object.
     * @return the name of the view.
     */
    @GetMapping(value = "/shutter-control")
    public String renderDashboard(Model model) {

        model.addAttribute("webcam_count", webcamService.getWebcamCount());
        model.addAttribute("shutterMovement", new ShutterMovement(ShutterMovement.DIRECTION_DOWN, 1000));
        return "manual_control";
    }

    /**
     * Action-method, for opening the shutters.
     *
     * @param model
     * @return name of the view which should be rendered afterwards.
     */
    @GetMapping(value = "/shutter-control/shutters-up")
    public String shuttersUp(Model model) {

        logger.info("Manually triggered shutters up.");

        try {
            shutterController.openShutter();
            model.addAttribute("success_message", "shutters.shutterUp.success");
        } catch (Exception e) {
            logger.warn("Exception thrown while opening shutters!", e);
            model.addAttribute("error_message", "shutters.shutterUp.error");
            model.addAttribute("error_message_cause", e.getCause().getMessage());
        }

        return renderDashboard(model);
    }

    /**
     * Action-method, for closing the shutters.
     *
     * @param model
     * @return name of the view which should be rendered afterwards.
     */
    @GetMapping(value = "/shutter-control/shutters-down")
    public String shuttersDown(Model model) {

        logger.info("Manually triggered shutters down.");

        try {
            shutterController.closeShutter();
            model.addAttribute("success_message", "shutters.shutterDown.success");
        } catch (Exception e) {
            logger.warn("Exception thrown while closing shutters!", e);
            model.addAttribute("error_message", "shutters.shutterDown.error");
            model.addAttribute("error_message_cause", e.getCause().getMessage());
        }

        return renderDashboard(model);
    }

    /**
     * Custom shutter movement method, to manual trigger the shutters for a
     * defined amount of time and direction.
     *
     * @param shutterMovement dto with the information, how long and which
     * direction the shutters shall get moved.
     * @param model
     * @return name of the view.
     */
    @PostMapping(value = "/shutter-control/shutters-move-custom")
    public String shuttersMovementCustom(@ModelAttribute("shutterMovement") ShutterMovement shutterMovement,
            Model model) {

        logger.info("Manually triggered custom shutters movement, with param: {}", shutterMovement);

        try {
            if (shutterMovement.directionIsDown()) {
                shutterController.closeShutter(shutterMovement.getDuration());
            } else if (shutterMovement.directionIsUp()) {
                shutterController.openShutter(shutterMovement.getDuration());
            }
            model.addAttribute("success_message", "shutters.shutterCustom.success");
        } catch (Exception e) {
            logger.error("Exception thrown while manually trigger the shutters!", e);
            model.addAttribute("error_message", "shutters.shutterCustom.error");
            model.addAttribute("error_message_cause", e.getCause().getMessage());
        }

        return renderDashboard(model);
    }

}
