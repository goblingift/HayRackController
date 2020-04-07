/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.service.configuration;

import gift.goblin.HayRackController.controller.model.LoadCellSettings;
import gift.goblin.HayRackController.controller.model.SoundSettings;
import gift.goblin.HayRackController.database.embedded.repo.configuration.ApplicationConfigurationRepository;
import gift.goblin.HayRackController.database.model.configuration.ApplicationConfiguration;
import gift.goblin.HayRackController.database.model.configuration.ConfigurationType;
import gift.goblin.HayRackController.service.converter.SettingsConverter;
import gift.goblin.HayRackController.service.io.model.Playlist;
import java.time.LocalDateTime;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service bean which implements several methods to read and write the
 * application configuration. (e.g. properties as selected sounds)
 *
 * @author andre
 */
@Service
public class ConfigurationServiceImpl implements ConfigurationService {

    @Autowired
    ApplicationConfigurationRepository repo;

    @Autowired
    SettingsConverter settingsConverter;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Optional<Playlist> getSelectedSound() {

        try {
            Optional<ApplicationConfiguration> configEntity = repo.findAll()
                    .stream()
                    .filter(ac -> ac.getConfigurationId() == ConfigurationType.SOUND_MELODY.getId())
                    .findFirst();
            if (configEntity.isPresent()) {

                int soundId = Integer.parseInt(configEntity.get().getValue());
                Optional<Playlist> optEntry = Playlist.findById(soundId);
                return optEntry;
            }
        } catch (Exception e) {
            logger.error("Exception thrown while read selected sound: {}", e.getMessage());
            return Optional.empty();
        }

        logger.error("No configuration entry in database found!");
        return Optional.empty();
    }

    @Override
    public SoundSettings getSoundSettings() {

        Optional<ApplicationConfiguration> configEntity = repo.findAll().stream().findFirst();
        if (configEntity.isPresent()) {
            ApplicationConfiguration entity = configEntity.get();
            SoundSettings settings = settingsConverter.toFormDto(entity);
            return settings;
        } else {
            SoundSettings defaultSettings = new SoundSettings();
            defaultSettings.setSelectedSound("99");
            return defaultSettings;
        }
    }

    @Override
    public LoadCellSettings getLoadCellSettings() {

        LoadCellSettings loadCellSettings = new LoadCellSettings();

        Optional<ApplicationConfiguration> optLoadCellsActivated = repo.findById(ConfigurationType.LOADCELLS_ACTIVATED.getId());
        if (optLoadCellsActivated.isPresent()) {
            loadCellSettings.setEnabled(Boolean.parseBoolean(optLoadCellsActivated.get().getValue()));
        }

        Optional<ApplicationConfiguration> optLoadCellsAmount = repo.findById(ConfigurationType.LOADCELLS_AMOUNT.getId());
        if (optLoadCellsAmount.isPresent()) {
            loadCellSettings.setAmount(Integer.parseInt(optLoadCellsAmount.get().getValue()));
        }

        readLoadCellSettings1(loadCellSettings);
        readLoadCellSettings2(loadCellSettings);
        readLoadCellSettings3(loadCellSettings);
        readLoadCellSettings4(loadCellSettings);

        return loadCellSettings;
    }

    @Override
    public void saveSettings(SoundSettings settings) {
        saveOrUpdateSetting(ConfigurationType.SOUND_MELODY.getId(),
                settings.getSelectedSound(), ConfigurationType.SOUND_MELODY.name());
    }

    /**
     * Try to find a configuration entity by the given id and updates it.
     * Otherwise it will create a new entity.
     *
     * @param settingsId The unique id for the entity.
     * @param value The value for this setting.
     * @param description Description for the setting. Will be stored in
     * database.
     * @return true if successful, false if otherwise.
     */
    private boolean saveOrUpdateSetting(int settingsId, String value, String description) {

        boolean success = true;

        try {
            Optional<ApplicationConfiguration> optConfigEntity = repo.findById(settingsId);
            if (optConfigEntity.isPresent()) {
                ApplicationConfiguration configEntity = optConfigEntity.get();

                configEntity.setValue(value);
                configEntity.setLastModified(LocalDateTime.now());

                repo.save(configEntity);
                logger.info("Successful updated application configuration ({}): {}",
                        description, configEntity);
            } else {
                ApplicationConfiguration configEntity = new ApplicationConfiguration(settingsId, description, value, LocalDateTime.now());

                repo.save(configEntity);
                logger.info("Successful saved new application configuration ({}): {}",
                        description, configEntity);
            }
        } catch (Exception e) {
            success = false;
            logger.error("Exception occurs while try to saveOrUpdateSetting for: " + description, e);
        }

        return success;
    }

    @Override
    public void saveSettings(LoadCellSettings settings) {
        saveOrUpdateSetting(ConfigurationType.LOADCELLS_ACTIVATED.getId(),
                String.valueOf(settings.isEnabled()), ConfigurationType.LOADCELLS_ACTIVATED.name());

        saveOrUpdateSetting(ConfigurationType.LOADCELLS_AMOUNT.getId(),
                String.valueOf(settings.getAmount()), ConfigurationType.LOADCELLS_AMOUNT.name());

        if (settings.getAmount() >= 4) {
            saveLoadCell4Settings(settings);
        }
        if (settings.getAmount() >= 3) {
            saveLoadCell3Settings(settings);
        }
        if (settings.getAmount() >= 2) {
            saveLoadCell2Settings(settings);
        }
        if (settings.getAmount() >= 1) {
            saveLoadCell1Settings(settings);
        }
    }

//<editor-fold defaultstate="collapsed" desc="saveLoadCells">
    private void saveLoadCell1Settings(LoadCellSettings settings) {
        saveOrUpdateSetting(ConfigurationType.LOADCELL_1_PIN_DAT.getId(),
                String.valueOf(settings.getLoadCellDAT1()),
                ConfigurationType.LOADCELL_1_PIN_DAT.name());

        saveOrUpdateSetting(ConfigurationType.LOADCELL_1_PIN_SCK.getId(),
                String.valueOf(settings.getLoadCellSCK1()),
                ConfigurationType.LOADCELL_1_PIN_SCK.name());

        saveOrUpdateSetting(ConfigurationType.LOADCELL_1_MAXLOAD_KG.getId(),
                String.valueOf(settings.getLoadCellMax1()),
                ConfigurationType.LOADCELL_1_MAXLOAD_KG.name());

        saveOrUpdateSetting(ConfigurationType.LOADCELL_1_MVV.getId(),
                String.valueOf(settings.getLoadCellMVV1()),
                ConfigurationType.LOADCELL_1_MVV.name());
    }

    private void saveLoadCell2Settings(LoadCellSettings settings) {
        saveOrUpdateSetting(ConfigurationType.LOADCELL_2_PIN_DAT.getId(),
                String.valueOf(settings.getLoadCellDAT2()),
                ConfigurationType.LOADCELL_2_PIN_DAT.name());

        saveOrUpdateSetting(ConfigurationType.LOADCELL_2_PIN_SCK.getId(),
                String.valueOf(settings.getLoadCellSCK2()),
                ConfigurationType.LOADCELL_2_PIN_SCK.name());

        saveOrUpdateSetting(ConfigurationType.LOADCELL_2_MAXLOAD_KG.getId(),
                String.valueOf(settings.getLoadCellMax2()),
                ConfigurationType.LOADCELL_2_MAXLOAD_KG.name());

        saveOrUpdateSetting(ConfigurationType.LOADCELL_2_MVV.getId(),
                String.valueOf(settings.getLoadCellMVV2()),
                ConfigurationType.LOADCELL_2_MVV.name());
    }

    private void saveLoadCell3Settings(LoadCellSettings settings) {
        saveOrUpdateSetting(ConfigurationType.LOADCELL_3_PIN_DAT.getId(),
                String.valueOf(settings.getLoadCellDAT3()),
                ConfigurationType.LOADCELL_3_PIN_DAT.name());

        saveOrUpdateSetting(ConfigurationType.LOADCELL_3_PIN_SCK.getId(),
                String.valueOf(settings.getLoadCellSCK3()),
                ConfigurationType.LOADCELL_3_PIN_SCK.name());

        saveOrUpdateSetting(ConfigurationType.LOADCELL_3_MAXLOAD_KG.getId(),
                String.valueOf(settings.getLoadCellMax3()),
                ConfigurationType.LOADCELL_3_MAXLOAD_KG.name());

        saveOrUpdateSetting(ConfigurationType.LOADCELL_3_MVV.getId(),
                String.valueOf(settings.getLoadCellMVV3()),
                ConfigurationType.LOADCELL_3_MVV.name());
    }

    private void saveLoadCell4Settings(LoadCellSettings settings) {
        saveOrUpdateSetting(ConfigurationType.LOADCELL_4_PIN_DAT.getId(),
                String.valueOf(settings.getLoadCellDAT4()),
                ConfigurationType.LOADCELL_4_PIN_DAT.name());

        saveOrUpdateSetting(ConfigurationType.LOADCELL_4_PIN_SCK.getId(),
                String.valueOf(settings.getLoadCellSCK4()),
                ConfigurationType.LOADCELL_4_PIN_SCK.name());

        saveOrUpdateSetting(ConfigurationType.LOADCELL_4_MAXLOAD_KG.getId(),
                String.valueOf(settings.getLoadCellMax4()),
                ConfigurationType.LOADCELL_4_MAXLOAD_KG.name());

        saveOrUpdateSetting(ConfigurationType.LOADCELL_4_MVV.getId(),
                String.valueOf(settings.getLoadCellMVV4()),
                ConfigurationType.LOADCELL_4_MVV.name());
    }

//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="readLoadCells">
    private void readLoadCellSettings1(LoadCellSettings loadCellSettings) {
        Optional<ApplicationConfiguration> optValue = repo.findById(ConfigurationType.LOADCELL_1_PIN_DAT.getId());
        if (optValue.isPresent()) {
            loadCellSettings.setLoadCellDAT1(Integer.parseInt(optValue.get().getValue()));
        }

        optValue = repo.findById(ConfigurationType.LOADCELL_1_PIN_SCK.getId());
        if (optValue.isPresent()) {
            loadCellSettings.setLoadCellSCK1(Integer.parseInt(optValue.get().getValue()));
        }

        optValue = repo.findById(ConfigurationType.LOADCELL_1_MAXLOAD_KG.getId());
        if (optValue.isPresent()) {
            loadCellSettings.setLoadCellMax1(Integer.parseInt(optValue.get().getValue()));
        }

        optValue = repo.findById(ConfigurationType.LOADCELL_1_MVV.getId());
        if (optValue.isPresent()) {
            loadCellSettings.setLoadCellMVV1(Double.parseDouble(optValue.get().getValue()));
        }
    }

    private void readLoadCellSettings2(LoadCellSettings loadCellSettings) {
        Optional<ApplicationConfiguration> optValue = repo.findById(ConfigurationType.LOADCELL_2_PIN_DAT.getId());
        if (optValue.isPresent()) {
            loadCellSettings.setLoadCellDAT2(Integer.parseInt(optValue.get().getValue()));
        }

        optValue = repo.findById(ConfigurationType.LOADCELL_2_PIN_SCK.getId());
        if (optValue.isPresent()) {
            loadCellSettings.setLoadCellSCK2(Integer.parseInt(optValue.get().getValue()));
        }

        optValue = repo.findById(ConfigurationType.LOADCELL_2_MAXLOAD_KG.getId());
        if (optValue.isPresent()) {
            loadCellSettings.setLoadCellMax2(Integer.parseInt(optValue.get().getValue()));
        }

        optValue = repo.findById(ConfigurationType.LOADCELL_2_MVV.getId());
        if (optValue.isPresent()) {
            loadCellSettings.setLoadCellMVV2(Double.parseDouble(optValue.get().getValue()));
        }
    }

    private void readLoadCellSettings3(LoadCellSettings loadCellSettings) {
        Optional<ApplicationConfiguration> optValue = repo.findById(ConfigurationType.LOADCELL_3_PIN_DAT.getId());
        if (optValue.isPresent()) {
            loadCellSettings.setLoadCellDAT3(Integer.parseInt(optValue.get().getValue()));
        }

        optValue = repo.findById(ConfigurationType.LOADCELL_3_PIN_SCK.getId());
        if (optValue.isPresent()) {
            loadCellSettings.setLoadCellSCK3(Integer.parseInt(optValue.get().getValue()));
        }

        optValue = repo.findById(ConfigurationType.LOADCELL_3_MAXLOAD_KG.getId());
        if (optValue.isPresent()) {
            loadCellSettings.setLoadCellMax3(Integer.parseInt(optValue.get().getValue()));
        }

        optValue = repo.findById(ConfigurationType.LOADCELL_3_MVV.getId());
        if (optValue.isPresent()) {
            loadCellSettings.setLoadCellMVV3(Double.parseDouble(optValue.get().getValue()));
        }
    }

    private void readLoadCellSettings4(LoadCellSettings loadCellSettings) {
        Optional<ApplicationConfiguration> optValue = repo.findById(ConfigurationType.LOADCELL_4_PIN_DAT.getId());
        if (optValue.isPresent()) {
            loadCellSettings.setLoadCellDAT4(Integer.parseInt(optValue.get().getValue()));
        }

        optValue = repo.findById(ConfigurationType.LOADCELL_4_PIN_SCK.getId());
        if (optValue.isPresent()) {
            loadCellSettings.setLoadCellSCK4(Integer.parseInt(optValue.get().getValue()));
        }

        optValue = repo.findById(ConfigurationType.LOADCELL_4_MAXLOAD_KG.getId());
        if (optValue.isPresent()) {
            loadCellSettings.setLoadCellMax4(Integer.parseInt(optValue.get().getValue()));
        }

        optValue = repo.findById(ConfigurationType.LOADCELL_4_MVV.getId());
        if (optValue.isPresent()) {
            loadCellSettings.setLoadCellMVV4(Double.parseDouble(optValue.get().getValue()));
        }
    }
//</editor-fold>
}
