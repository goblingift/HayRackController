/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.service.security;

import gift.goblin.HayRackController.database.model.user.User;

/**
 *
 * @author andre
 */
public interface UserService {
    
    void save(User user);
    
    User findByUsername(String username);
    
}
