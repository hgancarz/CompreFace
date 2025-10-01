package com.exadel.frs.core.trainservice.health;

import com.exadel.frs.commonservice.sdk.faces.FacesApiClient;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Health indicator for the ML calculator (Faces) service.
 * Bean name is 'mlCalculatorHealthIndicator' so the actuator id is 'mlCalculator'.
 */
@Component
public class MlCalculatorHealthIndicator implements HealthIndicator {

    private final FacesApiClient facesApiClient;

    public MlCalculatorHealthIndicator(FacesApiClient facesApiClient) {
        this.facesApiClient = facesApiClient;
    }

    @Override
    public Health health() {
        try {
            facesApiClient.health();
            return Health.up().build();
        } catch (Exception ex) {
            return Health.down()
                    .withDetail("error", ex.getClass().getSimpleName())
                    .withDetail("message", ex.getMessage())
                    .build();
        }
    }
}
