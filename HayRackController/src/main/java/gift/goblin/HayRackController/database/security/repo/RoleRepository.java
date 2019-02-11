/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gift.goblin.HayRackController.database.security.repo;

import gift.goblin.HayRackController.database.model.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Contains basic methods for accessing the role table.
 * @author andre
 */
public interface RoleRepository extends JpaRepository<Role, Long>{
}
