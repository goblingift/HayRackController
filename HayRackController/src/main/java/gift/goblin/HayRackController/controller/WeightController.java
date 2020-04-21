/*
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.controller;

import gift.goblin.HayRackController.controller.model.WeightDetailsDto;
import gift.goblin.HayRackController.service.io.IOController;
import gift.goblin.HayRackController.service.io.WebcamDeviceService;
import gift.goblin.HayRackController.service.io.WeightMeasurementService;
import gift.goblin.HayRackController.service.io.dto.TemperatureAndHumidity;
import gift.goblin.HayRackController.service.tools.NumberConverterUtil;
import gift.goblin.HayRackController.service.tools.StringUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @Autowired
    private WeightMeasurementService weightMeasurementService;

    @Autowired
    private IOController iOController;

    @Autowired
    private StringUtils stringUtils;
    
    @Autowired
    private NumberConverterUtil numberConverterUtil;

    @GetMapping(value = "/weight")
    public String renderWeightOverview(Model model) {

        if (!iOController.isLoadCellsActivated()) {
            model.addAttribute("error_message", "weight.loadcells.deactivated");
        }

        model.addAttribute("weightDetailsDto", readLoadCells());
        model.addAttribute("webcam_count", webcamService.getWebcamCount());

        return "weight";
    }

    @PostMapping(
            value = "/weight/measure",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public @ResponseBody
    WeightDetailsDto measureLoadCells() throws IOException {
        logger.info("Called measureLoadCells!");

        return readLoadCells();
    }

    /**
     * Reads the values of the load-cells and generate dto with their details.
     *
     * @return dto which contains all required details to render the
     * frontend-view with the load-cells informations.
     */
    private WeightDetailsDto readLoadCells() {

        WeightDetailsDto returnValue = new WeightDetailsDto();

        int loadCellAmount = iOController.getLoadCellAmount();
        returnValue.setLoadCellAmount(loadCellAmount);

        List<WeightDetailsDto> singleLoadCells = new ArrayList<>();

        double weightSum = 0;
        int maxWeightSum = 0;

        if (loadCellAmount >= 1) {
            int maxWeightLoadCell1 = weightMeasurementService.getMaxWeightLoadCell1();
            maxWeightSum += maxWeightLoadCell1;

            String ticksLoadCell1 = stringUtils.createTicksString(maxWeightLoadCell1);
            Long weightLoadCell1 = weightMeasurementService.measureWeightLoadCell1();
            double convertedGramsToKg = numberConverterUtil.convertGramsToKg(weightLoadCell1);
            
            // fallback, required if raspberry isnt initialized!
            if (weightLoadCell1 != null) {
                weightSum += convertedGramsToKg;
            } else {
                logger.warn("NULL value returned when calling weightMeasurementService.measureWeightLoadCell1() - Ignore, if no raspberry is initialized!");
                weightLoadCell1 = new Long(0);
            }

            WeightDetailsDto loadCell1Data = new WeightDetailsDto(convertedGramsToKg, maxWeightLoadCell1, ticksLoadCell1, 1);
            singleLoadCells.add(loadCell1Data);
        }
        if (loadCellAmount >= 2) {
            int maxWeightLoadCell2 = weightMeasurementService.getMaxWeightLoadCell2();
            maxWeightSum += maxWeightLoadCell2;

            String ticksLoadCell2 = stringUtils.createTicksString(maxWeightLoadCell2);
            Long weightLoadCell2 = weightMeasurementService.measureWeightLoadCell2();
            double convertedGramsToKg = numberConverterUtil.convertGramsToKg(weightLoadCell2);
            
            // fallback, required if raspberry isnt initialized!
            if (weightLoadCell2 != null) {
                weightSum += convertedGramsToKg;
            } else {
                logger.warn("NULL value returned when calling weightMeasurementService.measureWeightLoadCell2() - Ignore, if no raspberry is initialized!");
                weightLoadCell2 = new Long(0);
            }

            WeightDetailsDto loadCell2Data = new WeightDetailsDto(convertedGramsToKg, maxWeightLoadCell2, ticksLoadCell2, 2);
            singleLoadCells.add(loadCell2Data);
        }
        if (loadCellAmount >= 3) {
            int maxWeightLoadCell3 = weightMeasurementService.getMaxWeightLoadCell3();
            maxWeightSum += maxWeightLoadCell3;

            String ticksLoadCell3 = stringUtils.createTicksString(maxWeightLoadCell3);
            Long weightLoadCell3 = weightMeasurementService.measureWeightLoadCell3();
            double convertedGramsToKg = numberConverterUtil.convertGramsToKg(weightLoadCell3);
            
            // fallback, required if raspberry isnt initialized!
            if (weightLoadCell3 != null) {
                weightSum += convertedGramsToKg;
            } else {
                logger.warn("NULL value returned when calling weightMeasurementService.measureWeightLoadCell3() - Ignore, if no raspberry is initialized!");
                weightLoadCell3 = new Long(0);
            }

            WeightDetailsDto loadCell3Data = new WeightDetailsDto(convertedGramsToKg, maxWeightLoadCell3, ticksLoadCell3, 3);
            singleLoadCells.add(loadCell3Data);
        }
        if (loadCellAmount >= 4) {
            int maxWeightLoadCell4 = weightMeasurementService.getMaxWeightLoadCell4();
            maxWeightSum += maxWeightLoadCell4;

            String ticksLoadCell4 = stringUtils.createTicksString(maxWeightLoadCell4);
            Long weightLoadCell4 = weightMeasurementService.measureWeightLoadCell4();
            double convertedGramsToKg = numberConverterUtil.convertGramsToKg(weightLoadCell4);
            
            // fallback, required if raspberry isnt initialized!
            if (weightLoadCell4 != null) {
                weightSum += convertedGramsToKg;
            } else {
                logger.warn("NULL value returned when calling weightMeasurementService.measureWeightLoadCell4() - Ignore, if no raspberry is initialized!");
                weightLoadCell4 = new Long(0);
            }

            WeightDetailsDto loadCell4Data = new WeightDetailsDto(convertedGramsToKg, maxWeightLoadCell4, ticksLoadCell4, 4);
            singleLoadCells.add(loadCell4Data);
        }

        returnValue.setLoadCellList(singleLoadCells);
        returnValue.setMaxWeightSum(maxWeightSum);
        returnValue.setWeightSumKg(weightSum);
        String ticksSum = stringUtils.createTicksString(maxWeightSum);
        returnValue.setTicks(ticksSum);

        return returnValue;
    }

}
