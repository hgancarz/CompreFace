package com.exadel.frs.core.trainservice.health;

import com.exadel.frs.core.trainservice.feign.EmbeddingCalculatorClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmbeddingCalculatorHealthIndicator implements HealthIndicator {

    private final EmbeddingCalculatorClient embeddingCalculatorClient;

    @Override
    public Health health() {
        try {
            var response = embeddingCalculatorClient.health();
            if ("OK".equals(response.getStatus())) {
                return Health.up().withDetail("status", response.getStatus()).build();
            } else {
                return Health.down().withDetail("status", response.getStatus()).build();
            }
        } catch (Exception e) {
            log.warn("Failed to check embedding-calculator health", e);
            return Health.down().withDetail("error", e.getMessage()).build();
        }
    }
}
