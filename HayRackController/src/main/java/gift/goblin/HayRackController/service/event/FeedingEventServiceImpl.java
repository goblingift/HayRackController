/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.service.event;

import gift.goblin.HayRackController.database.model.event.FeedingEvent;
import gift.goblin.HayRackController.database.model.event.ScheduledShutterMovement;
import gift.goblin.HayRackController.database.embedded.repo.event.FeedingEventRepository;
import gift.goblin.HayRackController.database.embedded.repo.event.ScheduledShutterMovementRepository;
import gift.goblin.HayRackController.service.io.IOController;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    IOController iOController;

    @Override
    public Long addNewFeedingEvent(long jobId) {

        Long feedingEventId = null;

        Optional<ScheduledShutterMovement> optEntity = scheduledShutterMovementRepo.findById(jobId);
        if (optEntity.isPresent()) {
            ScheduledShutterMovement scheduledShutterMovement = optEntity.get();
            FeedingEvent feedingEvent = new FeedingEvent(LocalDateTime.now(), scheduledShutterMovement);
            FeedingEvent savedEntity = feedingEventRepo.save(feedingEvent);
            logger.info("Created new feedingEvent in database: {}", savedEntity);
            feedingEventId = savedEntity.getId();
        } else {
            logger.warn("Couldnt find a ScheduledShutterMovement entity with id: {} - wont create log-entry.",
                    jobId);
        }

        return feedingEventId;
    }

    @Override
    @Transactional
    public Long finishFeedingEvent(long jobId) {

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

                feedingEventId = savedEntity.getId();
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

    @Override
    public void measureStartWeight(Long feedingEntryId) {

        Optional<FeedingEvent> optFeedingEvent = feedingEventRepo.findById(feedingEntryId);
        if (optFeedingEvent.isPresent()) {
            FeedingEvent feedingEvent = optFeedingEvent.get();

            long measureWeightLoadCell1 = iOController.measureWeightLoadCell1();
            long measureWeightLoadCell2 = iOController.measureWeightLoadCell2();
            long measureWeightLoadCell3 = iOController.measureWeightLoadCell3();
            long measureWeightLoadCell4 = iOController.measureWeightLoadCell4();
            long sum = measureWeightLoadCell1 + measureWeightLoadCell2 + measureWeightLoadCell3
                    + measureWeightLoadCell4;

            feedingEvent.setWeightGramStart(sum);
            feedingEventRepo.save(feedingEvent);
        }
    }

    @Override
    public void measureEndWeight(Long feedingEntryId) {
        Optional<FeedingEvent> optFeedingEvent = feedingEventRepo.findById(feedingEntryId);
        if (optFeedingEvent.isPresent()) {
            try {
                FeedingEvent feedingEvent = optFeedingEvent.get();

                long measureWeightLoadCell1 = iOController.measureWeightLoadCell1();
                Thread.sleep(2_000);
                long measureWeightLoadCell2 = iOController.measureWeightLoadCell2();
                Thread.sleep(2_000);
                long measureWeightLoadCell3 = iOController.measureWeightLoadCell3();
                Thread.sleep(2_000);
                long measureWeightLoadCell4 = iOController.measureWeightLoadCell4();
                long sum = measureWeightLoadCell1 + measureWeightLoadCell2 + measureWeightLoadCell3
                        + measureWeightLoadCell4;

                long weightGramStart = feedingEvent.getWeightGramStart();
                long consumption = weightGramStart - sum;

                feedingEvent.setWeightGramEnd(sum);
                feedingEvent.setFoodConsumptionGram(consumption);
                feedingEventRepo.save(feedingEvent);
            } catch (InterruptedException ex) {
                logger.warn("Exception while sleep while measure weights!", ex);
            }
        }
    }

}
