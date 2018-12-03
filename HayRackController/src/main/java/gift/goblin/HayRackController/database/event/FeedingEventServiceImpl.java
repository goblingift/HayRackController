/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gift.goblin.HayRackController.database.event;

import gift.goblin.HayRackController.database.event.model.FeedingEvent;
import gift.goblin.HayRackController.database.event.model.ScheduledShutterMovement;
import gift.goblin.HayRackController.database.event.repo.FeedingEventRepository;
import gift.goblin.HayRackController.database.event.repo.ScheduledShutterMovementRepository;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author andre
 */
@Service
public class FeedingEventServiceImpl implements FeedingEventService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ScheduledShutterMovementRepository scheduledShutterMovementRepo;
    
    @Autowired
    FeedingEventRepository feedingEventRepo;
    
    @Override
    public Long addNewFeedingEvent(int jobId) {
        
        Long feedingEventId = null;
        
        Optional<ScheduledShutterMovement> optEntity = scheduledShutterMovementRepo.findById(new Long(jobId));
        if (optEntity.isPresent()) {
            ScheduledShutterMovement scheduledShutterMovement = optEntity.get();
            FeedingEvent feedingEvent = new FeedingEvent(LocalDateTime.now(), scheduledShutterMovement);
            FeedingEvent savedEntity = feedingEventRepo.save(feedingEvent);
            logger.info("Created new feedingEvent in database: {}", savedEntity);
            feedingEventId = savedEntity.getFeedingEventId();
        } else {
            logger.warn("Couldnt find a ScheduledShutterMovement entity with id: {} - wont create log-entry.",
                    jobId);
        }
        
        return feedingEventId;
    }

    @Override
    public Long finishFeedingEvent(int jobId) {
        
        Long feedingEventId = null;
        
        Optional<ScheduledShutterMovement> optJob = scheduledShutterMovementRepo.findById(new Long(jobId));
        if (optJob.isPresent()) {
            ScheduledShutterMovement scheduledShutterMovement = optJob.get();
            Optional<FeedingEvent> optFeedingEvent = getLatestUnfinishedFeedingEvent(scheduledShutterMovement);
            if (optFeedingEvent.isPresent()) {
                FeedingEvent feedingEvent = optFeedingEvent.get();
                logger.info("Found open feeding event in db- will now finish it: {}", feedingEvent);
                
                LocalDateTime now = LocalDateTime.now();
                feedingEvent.setFeedingEnd(now);
                long feedingTime = feedingEvent.getFeedingStart().until(now, ChronoUnit.MILLIS);
                feedingEvent.setFeedingDurationMs(feedingTime);
                FeedingEvent savedEntity = feedingEventRepo.save(feedingEvent);
                
                feedingEventId = savedEntity.getFeedingEventId();
            } else {
                logger.warn("Couldnt find any feeding event for this ScheduledShutterMovement: {}", scheduledShutterMovement);
            }
        } else {
            logger.warn("Couldnt find a ScheduledShutterMovement entity with id: {} - wont create log-entry.",
                    jobId);
        }
        
        return feedingEventId;
    }
    
    private Optional<FeedingEvent> getLatestUnfinishedFeedingEvent(ScheduledShutterMovement scheduledShutterMovement) {
        
        List<FeedingEvent> feedingEvents = scheduledShutterMovement.getFeedingEvents();
        Optional<FeedingEvent> openFeedingEvent = feedingEvents.stream()
                .filter(fe -> fe.getFeedingEnd() == null)
                .sorted(Comparator.comparing(FeedingEvent::getFeedingStart).reversed())
                .findFirst();
        
        return openFeedingEvent;
    }
    
}
