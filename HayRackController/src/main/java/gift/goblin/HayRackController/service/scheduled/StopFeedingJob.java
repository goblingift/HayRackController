/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gift.goblin.HayRackController.service.scheduled;

import gift.goblin.HayRackController.database.event.FeedingEventService;
import gift.goblin.HayRackController.database.event.repo.ScheduledShutterMovementRepository;
import gift.goblin.HayRackController.service.io.IOController;
import gift.goblin.HayRackController.service.tools.StringUtils;
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
    private IOController ioController;

    @Autowired
    ScheduledShutterMovementRepository repo;

    @Autowired
    private FeedingEventService feedingEventService;
    
    @Autowired
    private StringUtils stringUtils;

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {

        String jobKey = jec.getJobDetail().getKey().getName().toString();
        int jobId = stringUtils.getJobId(jobKey);
        
        logger.info("End of feeding scheduled! Job-Id: {}", jobId);

        try {
            ioController.triggerRelayLight(false);
            ioController.closeShutter();
            feedingEventService.finishFeedingEvent(jobId);
        } catch (InterruptedException ex) {
            logger.error("Exception thrown while closing shutters!", ex);
        }
    }

}
