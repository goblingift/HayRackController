/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.database.backup.repo.user;

import gift.goblin.HayRackController.database.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Contains basic methods for accessing the user table.
 * @author andre
 */
public interface UserBackupRepository extends JpaRepository<User,Long>{
    User findByUsername(String username);
}
