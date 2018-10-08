/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gift.goblin.HayRackController.service.timetable;

import gift.goblin.HayRackController.database.security.model.ScheduledShutterMovement;
import gift.goblin.HayRackController.database.security.repo.ScheduledShutterMovementRepository;
import gift.goblin.HayRackController.service.security.SecurityService;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
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
    SecurityService securityService;
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void addNewShutterMovement(LocalTime openAt, LocalTime closeAt, String comment) {

        ScheduledShutterMovement newShutterMovement = new ScheduledShutterMovement(openAt, closeAt, comment);
        
        String usernameOfCurrentUser = securityService.getUsernameOfCurrentUser();
        
        newShutterMovement.setCreatedBy(usernameOfCurrentUser);
        newShutterMovement.setCreatedAt(LocalDateTime.now());
        repo.save(newShutterMovement);
        logger.info("Successful added new scheduled Movement: {}", newShutterMovement);
    }

    @Override
    public List<ScheduledShutterMovement> readAllStoredShutterMovementSchedules() {
        
        List<ScheduledShutterMovement> allShutterMovements = repo.findAll();
        List<ScheduledShutterMovement> sortedShutterMovements = allShutterMovements.stream().sorted().collect(Collectors.toList());
        return sortedShutterMovements;
    }

}
