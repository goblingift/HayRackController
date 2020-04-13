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
import gift.goblin.HayRackController.service.tools.StringUtils;
import java.util.ArrayList;
import java.util.List;
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

    @Autowired
    WeightMeasurementService weightMeasurementService;

    @Autowired
    IOController iOController;

    @Autowired
    StringUtils stringUtils;
    
    private static final String PREFIX_LOADCELL_DESCR = "Load-Cell ";

    @GetMapping(value = "/weight")
    public String renderWeightOverview(Model model) {

        if (!iOController.isLoadCellsActivated()) {
            model.addAttribute("error_message", "weight.loadcells.deactivated");
        }

        model.addAttribute("weightDetailsDto", readLoadCells());
        model.addAttribute("webcam_count", webcamService.getWebcamCount());

        return "weight";
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
        returnValue.setDescription(PREFIX_LOADCELL_DESCR + "SUM");

        List<WeightDetailsDto> singleLoadCells = new ArrayList<>();

        int weightSum = 0;
        int maxWeightSum = 0;

        if (loadCellAmount >= 4) {
            int maxWeightLoadCell4 = weightMeasurementService.getMaxWeightLoadCell4();
            maxWeightSum += maxWeightLoadCell4;

            String ticksLoadCell4 = stringUtils.createTicksString(maxWeightLoadCell4);
            Long weightLoadCell4 = weightMeasurementService.measureWeightLoadCell4();

            // fallback, required if raspberry isnt initialized!
            if (weightLoadCell4 != null) {
                weightSum += weightLoadCell4;
            } else {
                logger.warn("NULL value returned when calling weightMeasurementService.measureWeightLoadCell4() - Ignore, if no raspberry is initialized!");
                weightLoadCell4 = new Long(0);
            }

            WeightDetailsDto loadCell4Data = new WeightDetailsDto(weightLoadCell4, maxWeightLoadCell4, ticksLoadCell4, PREFIX_LOADCELL_DESCR + "#4");
            singleLoadCells.add(loadCell4Data);
        }
        if (loadCellAmount >= 3) {
            int maxWeightLoadCell3 = weightMeasurementService.getMaxWeightLoadCell3();
            maxWeightSum += maxWeightLoadCell3;

            String ticksLoadCell3 = stringUtils.createTicksString(maxWeightLoadCell3);
            Long weightLoadCell3 = weightMeasurementService.measureWeightLoadCell3();

            // fallback, required if raspberry isnt initialized!
            if (weightLoadCell3 != null) {
                weightSum += weightLoadCell3;
            } else {
                logger.warn("NULL value returned when calling weightMeasurementService.measureWeightLoadCell3() - Ignore, if no raspberry is initialized!");
                weightLoadCell3 = new Long(0);
            }

            WeightDetailsDto loadCell3Data = new WeightDetailsDto(weightLoadCell3, maxWeightLoadCell3, ticksLoadCell3, PREFIX_LOADCELL_DESCR + "#3");
            singleLoadCells.add(loadCell3Data);
        }
        if (loadCellAmount >= 2) {
            int maxWeightLoadCell2 = weightMeasurementService.getMaxWeightLoadCell2();
            maxWeightSum += maxWeightLoadCell2;

            String ticksLoadCell2 = stringUtils.createTicksString(maxWeightLoadCell2);
            Long weightLoadCell2 = weightMeasurementService.measureWeightLoadCell2();

            // fallback, required if raspberry isnt initialized!
            if (weightLoadCell2 != null) {
                weightSum += weightLoadCell2;
            } else {
                logger.warn("NULL value returned when calling weightMeasurementService.measureWeightLoadCell2() - Ignore, if no raspberry is initialized!");
                weightLoadCell2 = new Long(0);
            }

            WeightDetailsDto loadCell2Data = new WeightDetailsDto(weightLoadCell2, maxWeightLoadCell2, ticksLoadCell2, PREFIX_LOADCELL_DESCR + "#2");
            singleLoadCells.add(loadCell2Data);
        }
        if (loadCellAmount >= 1) {
            int maxWeightLoadCell1 = weightMeasurementService.getMaxWeightLoadCell1();
            maxWeightSum += maxWeightLoadCell1;

            String ticksLoadCell1 = stringUtils.createTicksString(maxWeightLoadCell1);
            Long weightLoadCell1 = weightMeasurementService.measureWeightLoadCell1();

            // fallback, required if raspberry isnt initialized!
            if (weightLoadCell1 != null) {
                weightSum += weightLoadCell1;
            } else {
                logger.warn("NULL value returned when calling weightMeasurementService.measureWeightLoadCell1() - Ignore, if no raspberry is initialized!");
                weightLoadCell1 = new Long(0);
            }

            WeightDetailsDto loadCell1Data = new WeightDetailsDto(weightLoadCell1, maxWeightLoadCell1, ticksLoadCell1, PREFIX_LOADCELL_DESCR + "#1");
            singleLoadCells.add(loadCell1Data);
        }
        returnValue.setLoadCellList(singleLoadCells);
        returnValue.setMaxWeightSum(maxWeightSum);
        returnValue.setWeightSumKg(weightSum);
        String ticksSum = stringUtils.createTicksString(maxWeightSum);
        returnValue.setTicks(ticksSum);

        return returnValue;
    }

}
