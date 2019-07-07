/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.service.tools;

import gift.goblin.HayRackController.service.scheduled.SchedulerJobService;
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
     * @param jobDetailIdentifier, e.g. 'start_feeding_job_1' or 'stop_feeding_job_1'.
     * @return the id- for the example it would be 1.
     */
    public int getJobId(String jobDetailIdentifier) {
        
        String[] splittedStrings;
        if (jobDetailIdentifier.contains(SchedulerJobService.PREFIX_START_FEEDING_JOB)) {
            splittedStrings = jobDetailIdentifier.split(SchedulerJobService.PREFIX_START_FEEDING_JOB);
        } else if(jobDetailIdentifier.contains(SchedulerJobService.PREFIX_STOP_FEEDING_JOB)) {
            splittedStrings = jobDetailIdentifier.split(SchedulerJobService.PREFIX_STOP_FEEDING_JOB);
        } else {
            logger.warn("The job-detail identifier {} doesnt contain the prefix for start- or stop-feeding-jobs!", jobDetailIdentifier);
            return 0;
        }
        
        String jobIdString = splittedStrings[1];
        Integer jobId = Integer.valueOf(jobIdString);
        return jobId;
    }
    
}
