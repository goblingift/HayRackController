/*
 * Copyright (C) 2020 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.controller.model;

import java.time.LocalDateTime;

/**
 * Contains informations about the user and their session.
 * @author andre
 */
public class UserSessionInfo {
    
    private String username;
    private String sessionId;
    private LocalDateTime lastRequest;

    public UserSessionInfo() {
    }

    public UserSessionInfo(String username, String sessionId, LocalDateTime lastRequest) {
        this.username = username;
        this.sessionId = sessionId;
        this.lastRequest = lastRequest;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public LocalDateTime getLastRequest() {
        return lastRequest;
    }

    public void setLastRequest(LocalDateTime lastRequest) {
        this.lastRequest = lastRequest;
    }

    @Override
    public String toString() {
        return "UserSessionInfo{" + "username=" + username + ", sessionId=" + sessionId + ", lastRequest=" + lastRequest + '}';
    }
    
}
