/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.aop;

import gift.goblin.HayRackController.service.io.IOController;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
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
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        if (ioController.isRaspberryInitialized()) {
            joinPoint.proceed();
        } else {

            // evaluate return type
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            Class clazz = methodSignature.getReturnType();
            logger.warn("Raspberry isnt initialized! Skip method call and return default value for: {}", joinPoint.getSignature().getName());

            return getReturnValue(clazz);
        }

        // should never reached
        return null;
    }

    /**
     * Evaluates the class of the given method and returns the default value for
     * em.
     *
     * @param clazz the class of the return value.
     * @return the default value.
     */
    private Object getReturnValue(Class clazz) {

        logger.info("Generates default return value for  {}", clazz.toString());
        
        if (!clazz.isPrimitive()) {
            return null;
        } else {
            if (clazz == int.class) {
                logger.info("Generating default value for return class: int");
                return 0;
            } else if (clazz == long.class) {
                logger.info("Generating default value for return class: long");
                return 0;
            } else if (clazz == byte.class) {
                logger.info("Generating default value for return class: byte");
                return 0;
            } else if (clazz == short.class) {
                logger.info("Generating default value for return class: short");
                return 0;
            } else if (clazz == double.class) {
                logger.info("Generating default value for return class: double");
                return 0.0d;
            } else if (clazz == float.class) {
                logger.info("Generating default value for return class: float");
                return 0.0;
            } else if (clazz == boolean.class) {
                logger.info("Generating default value for return class: boolean");
                return false;
            } else if (clazz == char.class) {
                logger.info("Generating default value for return class: char");
                return '?';
            }
        }
        
        // should never reached
        return null;
    }

}
