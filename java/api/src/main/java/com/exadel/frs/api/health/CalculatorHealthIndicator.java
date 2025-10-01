package com.exadel.frs.api.health;

import static com.exadel.frs.commonservice.system.global.EnvironmentProperties.ServerType.PYTHON;

import com.exadel.frs.commonservice.sdk.faces.service.FacesRestApiClient;
import com.exadel.frs.commonservice.system.global.EnvironmentProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Health indicator for the ML backend (embedding-calculator).
 * Uses the existing FacesRestApiClient to ping the /status endpoint with configured Feign timeouts.
 * Returns UP/DOWN with details (response time and endpoint).
 */
@Component("calculator")
public class CalculatorHealthIndicator implements HealthIndicator {

    private final FacesRestApiClient facesRestApiClient;
    private final EnvironmentProperties environmentProperties;

    @Autowired
    public CalculatorHealthIndicator(
            final FacesRestApiClient facesRestApiClient,
            final EnvironmentProperties environmentProperties
    ) {
        this.facesRestApiClient = facesRestApiClient;
        this.environmentProperties = environmentProperties;
    }

    @Override
    public Health health() {
        String endpoint = null;
        long start = System.nanoTime();
        try {
            endpoint = environmentProperties.getServers().get(PYTHON).getUrl() + "/status";
            facesRestApiClient.getStatus();
            long durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
            return Health.up()
                    .withDetail("endpoint", endpoint)
                    .withDetail("responseTimeMs", durationMs)
                    .build();
        } catch (Exception ex) {
            long durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
            Health.Builder b = Health.down(ex).withDetail("responseTimeMs", durationMs);
            if (endpoint != null) {
                b.withDetail("endpoint", endpoint);
            }
            return b.build();
        }
    }
}
