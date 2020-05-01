/*
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.service.io.listener;

import gift.goblin.HayRackController.service.io.IOController;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author andre
 */
@Service
public class IOListenerRegistration {
    
    @Autowired
    IOController iOController;
    
    @PostConstruct
    private void registerIOListener() {
        
        
        
        
    }
    
    
    
    
}
