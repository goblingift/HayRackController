/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.service.security;

import gift.goblin.HayRackController.database.model.user.Role;
import gift.goblin.HayRackController.database.model.user.User;
import gift.goblin.HayRackController.database.embedded.repo.user.UserRepository;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author andre
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        User user = userRepository.findByUsername(username);
        
        if (user == null) {
            logger.warn("Couldnt find user by username: {}", username);
            throw new UsernameNotFoundException(username);
        }
        
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        for (Role role : user.getRoles()){
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        org.springframework.security.core.userdetails.User springUser =
                new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), grantedAuthorities);
                
        return springUser;
    }
    
}
