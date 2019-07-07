/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.service.security;

/**
 *
 * @author andre
 */
public interface SecurityService {
    
    String getUsernameOfCurrentUser();
    
    void autoLogin(String username, String password);
    
}
