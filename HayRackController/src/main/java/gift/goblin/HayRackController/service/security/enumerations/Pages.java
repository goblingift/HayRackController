/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gift.goblin.HayRackController.service.security.enumerations;

/**
 * Enumeration which stores all used URLs of the application
 * @author andre
 */
public enum Pages {
    
    LOGINPAGE("/login"),
    DASHBOARD("/dashboard"),
    ADMIN_DASHBOARD("/control-center");
    
    private String url;

    private Pages(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
    
}
