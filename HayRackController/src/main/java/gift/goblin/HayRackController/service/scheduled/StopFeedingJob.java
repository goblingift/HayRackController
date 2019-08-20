/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.service.scheduled;

import gift.goblin.HayRackController.service.event.FeedingEventService;
import gift.goblin.HayRackController.database.embedded.repo.event.ScheduledShutterMovementRepository;
import gift.goblin.HayRackController.service.configuration.ConfigurationService;
import gift.goblin.HayRackController.service.io.IOController;
import gift.goblin.HayRackController.service.io.model.Playlist;
import gift.goblin.HayRackController.service.tools.StringUtils;
import java.util.Optional;
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

    @Autowired
    private ConfigurationService configurationService;
    
    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {

        String jobKey = jec.getJobDetail().getKey().getName().toString();
        int jobId = stringUtils.getJobId(jobKey);
        
        logger.info("End of feeding scheduled! Job-Id: {}", jobId);
        
        Playlist track = configurationService.getSelectedSound().orElseGet(Playlist.getRandomPlaylist());
        
        try {
            ioController.triggerRelayLight(false);
            ioController.closeShutter(Optional.of(track));
            Long feedingEventId = feedingEventService.finishFeedingEvent(jobId);
            feedingEventService.measureEndWeight(feedingEventId);
        } catch (InterruptedException ex) {
            logger.error("Exception thrown while closing shutters!", ex);
        }
    }

}
