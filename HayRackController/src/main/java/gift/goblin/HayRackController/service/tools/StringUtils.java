/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gift.goblin.HayRackController.service.tools;

import gift.goblin.HayRackController.service.scheduled.SchedulerJobFactory;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 *
 * @author andre
 */
@Service
public class StringUtils {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Extracts the id of the scheduled jobdetail.
     * @param jobDetailIdentifier, e.g. 'start_feeding_job_1'.
     * @return the id- for the example it would be 1.
     */
    public int getJobId(String jobDetailIdentifier) {
        
        logger.debug("start splitting with:" + jobDetailIdentifier);
        logger.debug("splitting with:" + SchedulerJobFactory.PREFIX_START_FEEDING_JOB);
        
        String[] splittedStrings = jobDetailIdentifier.split(SchedulerJobFactory.PREFIX_START_FEEDING_JOB);
        
        logger.debug("splitted:" + Arrays.toString(splittedStrings));
        
        String jobIdString = splittedStrings[1];
        Integer jobId = Integer.valueOf(jobIdString);
        return jobId;
    }
    
}
