package com.exadel.frs.core.trainservice.system.health;

import com.exadel.frs.core.trainservice.system.feign.EmbeddingCalculatorHealthClient;
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
            embeddingCalculatorHealthClient.health();
            return Health.up()
                    .withDetail("service", "embedding-calculator")
                    .withDetail("message", "Service is responding correctly")
                    .build();
        } catch (Exception e) {
            log.warn("Embedding calculator health check failed: {}", e.getMessage());
            return Health.down()
                    .withDetail("service", "embedding-calculator")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
