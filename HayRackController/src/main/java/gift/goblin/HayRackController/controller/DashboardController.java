/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gift.goblin.HayRackController.controller;

import gift.goblin.HayRackController.service.io.ShutterController;
import gift.goblin.HayRackController.service.io.WebcamController;
import gift.goblin.HayRackController.service.security.SecurityService;
import gift.goblin.HayRackController.view.model.ShutterMovement;
import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
    private WebcamController webcamController;

    @Autowired
    private BuildProperties buildProperties;

    /**
     * Default render method for the dashboard.
     *
     * @param model model with the used attributes in the view.
     * @return name of the view.
     */
    @GetMapping(value = "/dashboard")
    public String renderDashboard(Model model) {

        String username = securityService.getUsernameOfCurrentUser();
        model.addAttribute("username", username);

        model.addAttribute("build_artifact", buildProperties.getArtifact());
        model.addAttribute("build_version", buildProperties.getVersion());
        model.addAttribute("build_time", buildProperties.getTime().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        model.addAttribute("shutterMovement", new ShutterMovement(ShutterMovement.DIRECTION_DOWN, 1_000));

        model.addAttribute("webcam_count", webcamController.getWebcamCount());
        return "dashboard";
    }

    /**
     * Action-method, for opening the shutters.
     *
     * @param model
     * @return name of the view which should be rendered afterwards.
     */
    @GetMapping(value = "/dashboard/shutters-up")
    public String shuttersUp(Model model) {

        logger.info("Manually triggered shutters down.");

        try {
            shutterController.openShutter();
            model.addAttribute("success_message", "dashboard.shutterUp.success");
        } catch (Exception e) {
            logger.warn("Exception thrown while opening shutters!", e);
            model.addAttribute("error_message", "dashboard.shutterUp.error");
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
    @GetMapping(value = "/dashboard/shutters-down")
    public String shuttersDown(Model model) {

        logger.info("Manually triggered shutters down.");

        try {
            shutterController.closeShutter();
            model.addAttribute("success_message", "dashboard.shutterDown.success");
        } catch (Exception e) {
            logger.warn("Exception thrown while closing shutters!", e);
            model.addAttribute("error_message", "dashboard.shutterDown.error");
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
    @PostMapping(value = "/dashboard/shutters-move-custom")
    public String shuttersMovementCustom(@ModelAttribute("shutterMovement") ShutterMovement shutterMovement,
            Model model) {

        logger.info("Manually triggered custom shutters movement, with param: {}", shutterMovement);

        try {
            if (shutterMovement.directionIsDown()) {
                shutterController.closeShutter(shutterMovement.getDuration());
            } else if (shutterMovement.directionIsUp()) {
                shutterController.openShutter(shutterMovement.getDuration());
            }
            model.addAttribute("success_message", "dashboard.shutterCustom.success");
        } catch (Exception e) {
            logger.error("Exception thrown while manually trigger the shutters!", e);
            model.addAttribute("error_message", "dashboard.shutterCustom.error");
            model.addAttribute("error_message_cause", e.getCause().getMessage());
        }

        return renderDashboard(model);
    }

    @GetMapping(
            value = "/dashboard/webcam/webcam-{camNumber}/sd",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    public @ResponseBody byte[] getWebcamPictureSD(@PathVariable int camNumber) throws IOException {
        return webcamController.takePicture(camNumber, false);
    }

    @GetMapping(
            value = "/dashboard/webcam/webcam-{camNumber}/hd",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    public @ResponseBody byte[] getWebcamPictureHD(@PathVariable int camNumber) throws IOException {
        return webcamController.takePicture(camNumber, true);
    }

}
