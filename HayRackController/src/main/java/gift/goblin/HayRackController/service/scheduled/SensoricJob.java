/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gift.goblin.HayRackController.service.scheduled;

import gift.goblin.HayRackController.service.io.IOController;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Scheduled job, which measures several sensors, logs the results
 * and triggers following actions.
 * @author andre
 */
@Component
public class SensoricJob implements Job {

    @Autowired
    IOController iOController;
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // todo

    }
    
    /**
     * Measures the brightness and does following tasks.
     * So if its feeding time and its dark, power on the indoor-lights.
     */
    private void measureBrightness() {
        
        boolean daylightDetected = iOController.daylightDetected();
        
        // todo
        
    }
    
    
    
    
}
