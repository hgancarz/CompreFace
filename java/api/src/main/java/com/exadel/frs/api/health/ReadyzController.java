package com.exadel.frs.api.health;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Exposes /readyz endpoint that forwards to Spring Boot Actuator readiness endpoint.
 * This allows operations to check readiness without authentication, if security permits it.
 */
@Controller
public class ReadyzController {

    @GetMapping(value = "/readyz", produces = MediaType.APPLICATION_JSON_VALUE)
    public String readyz() {
        // Forward internally so the response body is the actuator readiness JSON
        return "forward:/actuator/health/readiness";
    }
}
