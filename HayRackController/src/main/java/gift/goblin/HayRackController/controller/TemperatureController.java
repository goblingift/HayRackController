/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gift.goblin.HayRackController.controller;

import gift.goblin.HayRackController.database.event.TemperatureMeasurementService;
import gift.goblin.HayRackController.service.io.WebcamDeviceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for the detailed view of the temperature measurements.
 * @author andre
 */
@Controller
public class TemperatureController {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private TemperatureMeasurementService temperatureMeasurementService;
    
    @Autowired
    private WebcamDeviceService webcamService;
    
    @GetMapping(value = "/temperature")
    public String renderDashboard(Model model) {
        
        model.addAttribute("temperature", temperatureMeasurementService.getLatestMeasurement());
        model.addAttribute("webcam_count", webcamService.getWebcamCount());
        return "temperature";
    }

    
}
