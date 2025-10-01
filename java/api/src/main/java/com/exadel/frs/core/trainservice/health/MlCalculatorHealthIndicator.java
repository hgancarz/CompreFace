package com.exadel.frs.core.trainservice.health;

import com.exadel.frs.commonservice.sdk.faces.FacesApiClient;
import com.exadel.frs.commonservice.sdk.faces.feign.dto.FacesStatusResponse;
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
            FacesStatusResponse status = facesApiClient.getStatus();
            if (status != null) {
                return Health.up()
                        .withDetail("status", status.getStatus())
                        .withDetail("calculatorVersion", status.getCalculatorVersion())
                        .build();
            }
            return Health.down().withDetail("reason", "Status response is null").build();
        } catch (Exception ex) {
            return Health.down()
                    .withDetail("error", ex.getClass().getSimpleName())
                    .withDetail("message", ex.getMessage())
                    .build();
        }
    }
}
