/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.service.event;

import gift.goblin.HayRackController.database.backup.repo.event.ScheduledShutterMovementBackupRepository;
import gift.goblin.HayRackController.database.model.event.FeedingEvent;
import gift.goblin.HayRackController.database.model.event.ScheduledShutterMovement;
import gift.goblin.HayRackController.database.embedded.repo.event.ScheduledShutterMovementRepository;
import gift.goblin.HayRackController.service.security.SecurityService;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implements several methods regarding scheduled shutter movements.
 *
 * @author andre
 */
@Service
public class ScheduledShutterMovementServiceImpl implements ScheduledShutterMovementService {

    @Autowired
    ScheduledShutterMovementRepository repo;
    
    @Autowired
    ScheduledShutterMovementBackupRepository backupRepo;
    
    @Autowired
    SecurityService securityService;
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Long addNewShutterMovement(LocalTime openAt, Integer feedingDuration) {

        ScheduledShutterMovement newShutterMovement = new ScheduledShutterMovement(openAt, feedingDuration);
        
        String usernameOfCurrentUser = securityService.getUsernameOfCurrentUser();
         
        newShutterMovement.setCreatedBy(usernameOfCurrentUser);
        newShutterMovement.setCreatedAt(LocalDateTime.now());
        ScheduledShutterMovement entity = repo.save(newShutterMovement);
        logger.info("Successful added new scheduled Movement: {}", newShutterMovement);
        
        return entity.getId();
    }

    @Override
    public List<ScheduledShutterMovement> readAllStoredShutterMovementSchedules() {
        
        List<ScheduledShutterMovement> allShutterMovements = repo.findAll();
        List<ScheduledShutterMovement> sortedShutterMovements = allShutterMovements.stream().sorted().collect(Collectors.toList());
        return sortedShutterMovements;
    }

    @Override
    public void deleteScheduledMovement(Long id) {
        
        // Remove all foreign-key relationships of triggered feedingEvent entries
        Optional<ScheduledShutterMovement> optScheduledShutterMovement = repo.findById(id);
        if (optScheduledShutterMovement.isPresent()) {
            List<FeedingEvent> feedingEvents = optScheduledShutterMovement.get().getFeedingEvents();
            feedingEvents.stream().forEach(fe -> fe.setScheduledShutterMovement(null));
        }

        repo.delete(optScheduledShutterMovement.get());
        logger.info("Successful deleted shutter movement entry with id: {}", id);
    }

}
