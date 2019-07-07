/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.controller;

import gift.goblin.HayRackController.service.event.TemperatureMeasurementService;
import gift.goblin.HayRackController.service.io.WebcamDeviceService;
import gift.goblin.HayRackController.service.io.dto.TemperatureAndHumidity;
import gift.goblin.HayRackController.service.security.SecurityService;
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
import org.springframework.web.bind.annotation.PathVariable;
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
    private WebcamDeviceService webcamService;

    @Autowired
    private BuildProperties buildProperties;
    
    @Autowired
    private TemperatureMeasurementService temperatureMeasurementService;

    /**
     * Default render method for the dashboard.
     *
     * @param model model with the used attributes in the view.
     * @return name of the view.
     */
    @GetMapping(value = {"/", "/dashboard"})
    public String renderDashboard(Model model) {
        
        TemperatureAndHumidity latestMeasurement = temperatureMeasurementService.getLatestMeasurement();
        if (latestMeasurement != null) {
            model.addAttribute("temperature", latestMeasurement);
        } else {
            model.addAttribute("temperature", new TemperatureAndHumidity(99, 99, 99));
        }
        
        String username = securityService.getUsernameOfCurrentUser();
        model.addAttribute("username", username);
        model.addAttribute("build_artifact", buildProperties.getArtifact());
        model.addAttribute("build_version", buildProperties.getVersion());
        model.addAttribute("build_time", buildProperties.getTime().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        model.addAttribute("webcam_count", webcamService.getWebcamCount());
        
        return "dashboard";
    }


    @GetMapping(
            value = "/dashboard/webcam/webcam-{camNumber}/sd",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    public @ResponseBody byte[] getWebcamPictureSD(@PathVariable int camNumber) throws IOException {
        return webcamService.takePicture(camNumber, false);
    }

    @GetMapping(
            value = "/dashboard/webcam/webcam-{camNumber}/hd",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    public @ResponseBody byte[] getWebcamPictureHD(@PathVariable int camNumber) throws IOException {
        return webcamService.takePicture(camNumber, true);
    }

}
