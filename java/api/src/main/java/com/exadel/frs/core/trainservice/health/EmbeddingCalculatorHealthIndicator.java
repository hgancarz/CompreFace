package com.exadel.frs.core.trainservice.health;

import com.exadel.frs.commonservice.system.feign.EmbeddingCalculatorHealthClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmbeddingCalculatorHealthIndicator implements HealthIndicator {

    private final EmbeddingCalculatorHealthClient embeddingCalculatorHealthClient;

    @Override
    public Health health() {
        try {
            var response = embeddingCalculatorHealthClient.health();
            if (response.getStatusCode().is2xxSuccessful()) {
                return Health.up()
                        .withDetail("status", "UP")
                        .withDetail("message", "Embedding calculator is responding")
                        .build();
            } else {
                return Health.down()
                        .withDetail("status", "DOWN")
                        .withDetail("message", "Embedding calculator returned status: " + response.getStatusCode())
                        .build();
            }
        } catch (Exception e) {
            log.warn("Failed to check embedding calculator health", e);
            return Health.down()
                    .withDetail("status", "DOWN")
                    .withDetail("message", "Embedding calculator is not reachable: " + e.getMessage())
                    .build();
        }
    }
}
