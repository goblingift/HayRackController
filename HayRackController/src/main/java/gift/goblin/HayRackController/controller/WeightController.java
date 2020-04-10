/*
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.controller;

import gift.goblin.HayRackController.service.io.WebcamDeviceService;
import gift.goblin.HayRackController.service.io.dto.TemperatureAndHumidity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for the weight overview page.
 *
 * @author andre
 */
@Controller
public class WeightController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WebcamDeviceService webcamService;

    @GetMapping(value = "/weight")
    public String renderWeightOverview(Model model) {
        
        model.addAttribute("webcam_count", webcamService.getWebcamCount());
        
        return "weight";
    }

}
