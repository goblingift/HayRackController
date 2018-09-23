/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
