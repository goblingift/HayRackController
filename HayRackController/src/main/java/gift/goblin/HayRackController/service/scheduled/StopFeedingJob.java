/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gift.goblin.HayRackController.service.scheduled;

import gift.goblin.HayRackController.service.io.ShutterController;
import java.util.logging.Level;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author andre
 */
@Component
public class StopFeedingJob implements Job {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ShutterController shutterController;

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        logger.info("End of feeding scheduled! Triggername: {} - Group: {}",
                jec.getTrigger().getKey().getName(), jec.getTrigger().getKey().getGroup());
        
        try {
            shutterController.closeShutter();
        } catch (InterruptedException ex) {
            logger.error("Exception thrown while closing shutters!", ex);
        }
    }

}
