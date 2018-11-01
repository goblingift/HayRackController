/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gift.goblin.HayRackController.service.tools;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service bean which offers several methods regarding date and time
 * calculations.
 *
 * @author andre
 */
@Service
public class DateAndTimeUtil {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public LocalDateTime getNextExecutionDateTime(LocalTime localTime) {
        if (localTime.isBefore(LocalTime.now())) {
            // Next execution will be tomorrow
            LocalDateTime nextExecutionTomorrow = LocalDateTime.of(LocalDate.now().plusDays(1), localTime);
            logger.debug("Next execution will be tomorrow: {}", nextExecutionTomorrow);
            return nextExecutionTomorrow;
        } else {
            // Next execution will be today
            LocalDateTime nextExecutionToday = LocalDateTime.of(LocalDate.now(), localTime);
            logger.debug("Next execution will be today: {}", nextExecutionToday);
            return nextExecutionToday;
        }
    }

    /**
     * Will calculate the datetime, if the minutes are added to actual time.
     *
     * @param minutesToAdd minutes to add
     * @return the calculated datetime, can be the next day.
     */
    public LocalDateTime getCalculatedDateTime(int minutesToAdd) {
        LocalDateTime calculatedDateTime = LocalDateTime.now().plusMinutes(minutesToAdd);

        logger.debug("Calculated datetime: {}", calculatedDateTime);

        return calculatedDateTime;
    }

}