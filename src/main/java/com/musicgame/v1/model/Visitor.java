package com.musicgame.v1.model;

import java.time.ZonedDateTime;

public class Visitor {

    private final String sessionId;
    private final ZonedDateTime localDateTime;

    public Visitor(String sessionId) {
        this.sessionId = sessionId;
        this.localDateTime = ZonedDateTime.now();
    }

    public String getSessionId() {
        return sessionId;
    }

    public ZonedDateTime getLocalDateTime() {
        return localDateTime;
    }
}
