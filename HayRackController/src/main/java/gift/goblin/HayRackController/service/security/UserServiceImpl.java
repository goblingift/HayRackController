/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.service.security;

import gift.goblin.HayRackController.database.model.user.User;
import gift.goblin.HayRackController.database.embedded.repo.user.RoleRepository;
import gift.goblin.HayRackController.database.embedded.repo.user.UserRepository;
import java.util.HashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service for several methods regarding User-entities.
 * @author andre
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Override
    public void save(User user) {
        
        String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRoles(new HashSet<>(roleRepository.findAll()));
        userRepository.save(user);
        logger.info("Successful saved new user: {}", user.getUsername());
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
