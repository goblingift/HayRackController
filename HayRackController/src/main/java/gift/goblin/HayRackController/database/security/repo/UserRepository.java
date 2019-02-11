/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gift.goblin.HayRackController.database.security.repo;

import gift.goblin.HayRackController.database.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Contains basic methods for accessing the user table.
 * @author andre
 */
public interface UserRepository extends JpaRepository<User,Long>{
    User findByUsername(String username);
}
