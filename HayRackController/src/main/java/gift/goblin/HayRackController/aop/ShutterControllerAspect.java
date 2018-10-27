/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gift.goblin.HayRackController.aop;

import gift.goblin.HayRackController.service.io.ShutterController;
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
public class ShutterControllerAspect {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ShutterController shutterController;

    @Around("@annotation(gift.goblin.HayRackController.aop.RequiresRaspberry)")
    public void around(ProceedingJoinPoint joinPoint) throws Throwable {

        if (shutterController.isRaspberryInitialized()) {
            joinPoint.proceed();
        } else {
            logger.warn("Raspberry isnt initialized! Skip method call: {}", joinPoint.getSignature().getName());
        }
    }

}
