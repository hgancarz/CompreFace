package com.exadel.frs.core.trainservice.system.health;

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
            String response = embeddingCalculatorHealthClient.health();
            if (response != null && response.contains("OK")) {
                return Health.up().withDetail("embedding-calculator", "Service is UP").build();
            } else {
                return Health.down().withDetail("embedding-calculator", "Service returned unexpected response").build();
            }
        } catch (Exception e) {
            log.warn("Embedding calculator health check failed: {}", e.getMessage());
            return Health.down().withDetail("embedding-calculator", "Service is DOWN: " + e.getMessage()).build();
        }
    }
}
