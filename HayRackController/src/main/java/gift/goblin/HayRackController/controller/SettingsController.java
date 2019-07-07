/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.controller;

import gift.goblin.HayRackController.controller.model.Settings;
import gift.goblin.HayRackController.controller.model.Soundtitle;
import gift.goblin.HayRackController.service.configuration.ConfigurationService;
import gift.goblin.HayRackController.service.event.TemperatureMeasurementService;
import gift.goblin.HayRackController.service.io.IOController;
import gift.goblin.HayRackController.service.io.WebcamDeviceService;
import gift.goblin.HayRackController.service.io.dto.TemperatureAndHumidity;
import gift.goblin.HayRackController.service.io.model.Playlist;
import gift.goblin.HayRackController.service.security.SecurityService;
import java.awt.Dimension;
import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller which offers endpoints for all actions regarding the user
 * dashboard.
 *
 * @author andre
 */
@Controller
public class SettingsController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SecurityService securityService;

    @Autowired
    private WebcamDeviceService webcamService;

    @Autowired
    private BuildProperties buildProperties;
    
    @Autowired
    private TemperatureMeasurementService temperatureMeasurementService;

    @Autowired
    private ConfigurationService configurationService;
    
    @Autowired
    private IOController iOController;
    
    /**
     * Default render method for the dashboard.
     *
     * @param model model with the used attributes in the view.
     * @return name of the view.
     */
    @GetMapping(value = {"/settings"})
    public String renderSettings(Model model) {
        
        TemperatureAndHumidity latestMeasurement = temperatureMeasurementService.getLatestMeasurement();
        if (latestMeasurement != null) {
            model.addAttribute("temperature", latestMeasurement);
        } else {
            model.addAttribute("temperature", new TemperatureAndHumidity(99, 99, 99));
        }
        
        List<Soundtitle> availableSounds = generateAvailableSounds();
        model.addAttribute("sounds", availableSounds);
        
        Settings settings = configurationService.getSettings();
        
        model.addAttribute("settings", settings);
        model.addAttribute("build_artifact", buildProperties.getArtifact());
        model.addAttribute("build_version", buildProperties.getVersion());
        model.addAttribute("build_time", buildProperties.getTime().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        model.addAttribute("webcam_count", webcamService.getWebcamCount());
        
        return "settings";
    }
    
    @PostMapping(value = {"/settings/save"})
    public String saveSettings(@ModelAttribute Settings settings, BindingResult bindingResult, Model model) {
        configurationService.saveSettings(settings);
        
        return renderSettings(model);
    }

    private List<Soundtitle> generateAvailableSounds() {
        
        List<Playlist> tracks = Playlist.getVALUES();
        
        List<Soundtitle> soundtitles = tracks.stream()
                .map(t -> new Soundtitle(String.valueOf(t.getId()), t.getTitle()))
                .collect(Collectors.toList());
        soundtitles.add(new Soundtitle("99", "random"));
        
        return soundtitles;
    }
    
    @PostMapping(value = "/settings/play-sound")
    public @ResponseBody
    void playSound(@RequestParam("soundId") String soundId) throws IOException, InterruptedException {
        logger.info("play sound:" + soundId);
        
        if (soundId.equals("99")) {
            iOController.playSoundAndLight(Playlist.getRandomPlaylist());
        } else {
            Optional<Playlist> optPlaylist = Playlist.findById(Integer.parseInt(soundId));
            if (optPlaylist.isPresent()) {
                iOController.playSoundAndLight(optPlaylist.get());
            } else {
                throw new IllegalArgumentException("Unknown soundId:" + soundId);
            }
        }
    }

}
