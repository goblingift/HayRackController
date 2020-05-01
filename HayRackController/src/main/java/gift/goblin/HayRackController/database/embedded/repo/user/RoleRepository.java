/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.database.embedded.repo.user;

import gift.goblin.HayRackController.database.model.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Contains basic methods for accessing the role table.
 * @author andre
 */
public interface RoleRepository extends JpaRepository<Role, Long>{
}
