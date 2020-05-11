/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.controller;

import gift.goblin.HayRackController.controller.model.LoadCellSettings;
import gift.goblin.HayRackController.controller.model.SoundSettings;
import gift.goblin.HayRackController.controller.model.Soundtitle;
import gift.goblin.HayRackController.database.embedded.repo.weight.TareMeasurementRepository;
import gift.goblin.HayRackController.database.model.weight.TareMeasurement;
import gift.goblin.HayRackController.service.configuration.ConfigurationService;
import gift.goblin.HayRackController.service.event.TemperatureMeasurementService;
import gift.goblin.HayRackController.service.io.ApplicationState;
import gift.goblin.HayRackController.service.io.IOController;
import gift.goblin.HayRackController.service.io.LightAndSoundService;
import gift.goblin.HayRackController.service.io.WebcamDeviceService;
import gift.goblin.HayRackController.service.io.WeightMeasurementService;
import gift.goblin.HayRackController.service.io.dto.TemperatureAndHumidity;
import gift.goblin.HayRackController.service.io.interfaces.MaintenanceManager;
import gift.goblin.HayRackController.service.io.model.Playlist;
import gift.goblin.HayRackController.service.security.SecurityService;
import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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

    @Autowired
    private WeightMeasurementService weightMeasurementService;

    @Autowired
    private MaintenanceManager maintenanceManager;

    @Autowired
    private TareMeasurementRepository tareMeasurementRepository;

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

        SoundSettings soundSettings = configurationService.getSoundSettings();
        LoadCellSettings loadCellSettings = configurationService.getLoadCellSettings();
        logger.info("got loadcell settings successful:" + loadCellSettings);

        model.addAttribute("maintenance_mode", maintenanceManager.getApplicationState() == ApplicationState.MAINTENANCE);
        model.addAttribute("soundSettings", soundSettings);
        model.addAttribute("loadCellSettings", loadCellSettings);
        model.addAttribute("feedingLightOn", iOController.isFeedingLightOn());
        model.addAttribute("build_artifact", buildProperties.getArtifact());
        model.addAttribute("build_version", buildProperties.getVersion());
        model.addAttribute("build_time", buildProperties.getTime().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        model.addAttribute("webcam_count", webcamService.getWebcamCount());

        return "settings";
    }

    /**
     * Will measure the current weight on the load-cells and set them as tare
     * value for later weight measurements.
     *
     * @param model
     */
    @GetMapping(value = {"/settings/tare"})
    public String setTare(Model model) {

        if (maintenanceManager.getApplicationState() != ApplicationState.MAINTENANCE) {
            logger.warn("Called set-tare! Wont set tare of load-cells, because not in maintenance mode!");
            model.addAttribute("success_message", "settings.tare.fail_no_maintenance");
        } else {
            logger.info("Called set-tare! Will set tare of all load-cells now!");
            Boolean measurementResult = weightMeasurementService.measureAndSaveTare();
            boolean updatesLoadCellResult = weightMeasurementService.readAndSetTareValueLoadCells();

            if (measurementResult != null && measurementResult && updatesLoadCellResult) {
                model.addAttribute("success_message", "settings.tare.success");
            } else {
                model.addAttribute("success_message", "settings.tare.fail");
            }
        }

        return renderSettings(model);
    }

    /**
     * Will start the maintenance mode, this will prevent the hayrack from
     * upcoming shutter movements and activate maintenance lights. If
     * maintenance mode is already actived, ignore the call.
     *
     * @param model
     */
    @GetMapping(value = {"/settings/maintenance/start"})
    public String startMaintenance(Model model) {

        logger.info("Called start maintenance!");

        if (maintenanceManager.getApplicationState() != ApplicationState.MAINTENANCE) {
            maintenanceManager.startMaintenanceMode();
            iOController.triggerRelayLightMaintenance(true);
            model.addAttribute("success_message", "settings.maintenance.activated");
        } else {
            logger.warn("Already in maintenance mode- ignore call of start-maintenance.");
        }

        return renderSettings(model);
    }

    /**
     * Will stop the maintenance mode. If the system isnt in maintenance mode,
     * ignore call.
     *
     * @param model
     */
    @GetMapping(value = {"/settings/maintenance/stop"})
    public String stopMaintenance(Model model) {
        logger.info("Called stop maintenance!");

        if (maintenanceManager.getApplicationState() == ApplicationState.MAINTENANCE) {
            maintenanceManager.endMaintenanceMode();
            iOController.triggerRelayLightMaintenance(false);
            model.addAttribute("success_message", "settings.maintenance.deactivated");
        } else {
            logger.warn("Not in maintenance-mode, ignore call of stop maintenance.");
        }

        return renderSettings(model);
    }

    @PostMapping(value = {"/settings/sound/save"})
    public String saveSoundSettings(@ModelAttribute SoundSettings settings, BindingResult bindingResult, Model model) {
        configurationService.saveSettings(settings);

        return renderSettings(model);
    }

    @PostMapping(value = {"/settings/load-cell/save"})
    public String saveLoadCellSettings(@ModelAttribute LoadCellSettings settings, BindingResult bindingResult, Model model) {
        logger.info("Triggered saveLoadCellSettings with settings: {}", settings);

        LoadCellSettings oldSettings = configurationService.getLoadCellSettings();

        configurationService.saveSettings(settings);

        iOController.setLoadCellAmount(settings.getAmount());

        if (!oldSettings.isEnabled() && settings.isEnabled()) {
            logger.info("Load-cells were activated, initialize load-cells now...");
            initializeLoadCells(settings);
        }

        if (oldSettings.isEnabled() && !settings.isEnabled()) {
            logger.info("Load-cells were deactivated, release IO pins now...");
            iOController.releasePinsLoadCell1();
            iOController.releasePinsLoadCell2();
            iOController.releasePinsLoadCell3();
            iOController.releasePinsLoadCell4();
        }

        iOController.setLoadCellsActivated(settings.isEnabled());

        return renderSettings(model);
    }

    private void initializeLoadCells(LoadCellSettings loadCellSettings) {

        Optional<TareMeasurement> optTareMeasurement = Optional.empty();
        try {
            logger.info("Try to read the last tare-measurement, to initialize load-cells correctly...");
            optTareMeasurement = tareMeasurementRepository.findTop1ByOrderByMeasuredAtDesc();
        } catch (Exception e) {
            logger.error("Exception while try to read the last tare measurement entry!", e);
        }
        
        if (optTareMeasurement.isPresent()) {
            if (loadCellSettings.getAmount() >= 4) {
                iOController.initializeLoadCell4(loadCellSettings);
                if (optTareMeasurement.isPresent()) {
                    iOController.setTareValueLoadCell4(optTareMeasurement.get().getTareLoadCell4());
                }
            }
            if (loadCellSettings.getAmount() >= 3) {
                iOController.initializeLoadCell3(loadCellSettings);
                if (optTareMeasurement.isPresent()) {
                    iOController.setTareValueLoadCell3(optTareMeasurement.get().getTareLoadCell3());
                }
            }
            if (loadCellSettings.getAmount() >= 2) {
                iOController.initializeLoadCell2(loadCellSettings);
                if (optTareMeasurement.isPresent()) {
                    iOController.setTareValueLoadCell2(optTareMeasurement.get().getTareLoadCell2());
                }
            }
            if (loadCellSettings.getAmount() >= 1) {
                iOController.initializeLoadCell1(loadCellSettings);
                if (optTareMeasurement.isPresent()) {
                    iOController.setTareValueLoadCell1(optTareMeasurement.get().getTareLoadCell1());
                }
            }

            iOController.setLoadCellAmount(loadCellSettings.getAmount());
            iOController.setLoadCellsActivated(true);
        } else {
            logger.warn("No tare-measurement entry found, cant initialize load-cells.");
        }

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

    @PostMapping(value = "/settings/light/trigger")
    public @ResponseBody
    void triggerLightSwitch(@RequestParam("isEnabled") boolean isEnabled) {
        iOController.triggerRelayLight(isEnabled);
        logger.info("Successful triggered light-switch with value: {}", isEnabled);
    }

}
