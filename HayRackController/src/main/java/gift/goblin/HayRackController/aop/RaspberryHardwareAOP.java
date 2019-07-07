/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.aop;

import gift.goblin.HayRackController.service.io.IOController;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author andre
 */
@Aspect
@Configuration
public class RaspberryHardwareAOP {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IOController ioController;

    @Around("@annotation(gift.goblin.HayRackController.aop.RequiresRaspberry)")
    public void around(ProceedingJoinPoint joinPoint) throws Throwable {

        if (ioController.isRaspberryInitialized()) {
            joinPoint.proceed();
        } else {
            logger.warn("Raspberry isnt initialized! Skip method call: {}", joinPoint.getSignature().getName());
        }
    }

}
