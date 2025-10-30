package com.exadel.frs.core.trainservice.controller;

import java.time.Instant;

public class TestHealthStatus {
    private final boolean ok;
    private final Instant checkedAt;
    private final String message;

    public TestHealthStatus(boolean ok, Instant checkedAt, String message) {
        this.ok = ok;
        this.checkedAt = checkedAt;
        this.message = message;
    }

    public boolean isOk() {
        return ok;
    }

    public Instant getCheckedAt() {
        return checkedAt;
    }

    public String getMessage() {
        return message;
    }
}
