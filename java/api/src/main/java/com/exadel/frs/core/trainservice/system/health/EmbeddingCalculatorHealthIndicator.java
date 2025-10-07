package com.exadel.frs.core.trainservice.system.health;

import com.exadel.frs.core.trainservice.system.feign.EmbeddingCalculatorHealthClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmbeddingCalculatorHealthIndicator implements HealthIndicator {

    private final EmbeddingCalculatorHealthClient healthClient;

    @Override
    public Health health() {
        try {
            var response = healthClient.healthCheck();
            if ("OK".equals(response.getStatus())) {
                return Health.up()
                        .withDetail("status", response.getStatus())
                        .build();
            } else {
                return Health.down()
                        .withDetail("status", response.getStatus())
                        .withDetail("error", "Unexpected status from embedding-calculator")
                        .build();
            }
        } catch (Exception e) {
            log.warn("Failed to check embedding-calculator health: {}", e.getMessage());
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
