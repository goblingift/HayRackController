/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gift.goblin.HayRackController.database.event;

import gift.goblin.HayRackController.database.event.model.FeedingEvent;
import gift.goblin.HayRackController.database.event.model.ScheduledShutterMovement;
import gift.goblin.HayRackController.database.event.repo.FeedingEventRepository;
import java.time.LocalDateTime;
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
    FeedingEventRepository repo;
    
    @Override
    public Long addNewFeedingEvent(LocalDateTime feedingStart, ScheduledShutterMovement scheduledShutterMovement) {
        
        FeedingEvent feedingEvent = new FeedingEvent(feedingStart, scheduledShutterMovement);
        FeedingEvent savedEntity = repo.save(feedingEvent);
        logger.info("Created new feedingEvent in database: {}", savedEntity);
        return savedEntity.getFeedingEventId();
    }

    @Override
    public Long finishFeedingEvent(ScheduledShutterMovement scheduledShutterMovement) {
        
        List<FeedingEvent> feedingEvents = scheduledShutterMovement.getFeedingEvents();
        Optional<FeedingEvent> openFeedingEvent = feedingEvents.stream()
                .filter(fe -> fe.getFeedingEnd() == null)
                .sorted(Comparator.comparing(FeedingEvent::getFeedingStart).reversed())
                .findFirst();
        logger.info("Found open feeding event: {}", openFeedingEvent);
        
    }
    
}