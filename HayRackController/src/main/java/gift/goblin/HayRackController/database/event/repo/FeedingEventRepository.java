/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gift.goblin.HayRackController.database.event.repo;

import gift.goblin.HayRackController.database.event.model.FeedingEvent;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Contains basic methods for accessing the feeding events from database.
 * @author andre
 */
public interface FeedingEventRepository extends JpaRepository<FeedingEvent, Long> {
    
}
