/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
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
