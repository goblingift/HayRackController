/*
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.aop;

import gift.goblin.HayRackController.service.io.WeightMeasurementService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * Defines several advices, to always set the last tare value to the load-cells,
 * before measure any values with the scale.
 * @author andre
 */
@Aspect
@Configuration
public class LoadCellTaringAOP {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    WeightMeasurementService weightMeasurementService;
    
    /**
     * Always read and set the tare value for the load cells, before executing a
     * measurement.
     * @param joinPoint 
     */
    @Before("execution(* *.measureWeightLoadCell*())")
    public void before(JoinPoint joinPoint) {
        boolean success = weightMeasurementService.readAndSetTareValueLoadCells();
        if (success) {
            logger.info("Successfully read and set tare value for all load-cells.");
        } else {
            logger.warn("Couldnt set tare value for all load-cells, measurement could be invalid!");
        }
    }
    
    
}
