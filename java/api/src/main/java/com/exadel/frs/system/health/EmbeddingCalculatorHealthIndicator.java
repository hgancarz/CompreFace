package com.exadel.frs.system.health;

import com.exadel.frs.system.feign.EmbeddingCalculatorClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmbeddingCalculatorHealthIndicator implements HealthIndicator {

    private final EmbeddingCalculatorClient embeddingCalculatorClient;
    
    private static final int TIMEOUT_SECONDS = 5;
    private static final String HEALTH_INDICATOR_NAME = "embeddingCalculator";

    @Override
    public Health health() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Health> future = executor.submit(new HealthCheckTask());
        
        try {
            return future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("Health check for embedding-calculator timed out or failed: {}", e.getMessage());
            future.cancel(true);
            return Health.down()
                    .withDetail("error", "Health check timeout or failure: " + e.getMessage())
                    .build();
        } finally {
            executor.shutdown();
        }
    }
    
    private class HealthCheckTask implements Callable<Health> {
        @Override
        public Health call() {
            try {
                ResponseEntity<String> response = embeddingCalculatorClient.health();
                if (response.getStatusCode().is2xxSuccessful()) {
                    return Health.up()
                            .withDetail("status", "Embedding calculator is reachable and responding")
                            .build();
                } else {
                    return Health.down()
                            .withDetail("status", "Embedding calculator returned non-2xx status: " + response.getStatusCode())
                            .build();
                }
            } catch (Exception e) {
                log.debug("Health check failed for embedding-calculator: {}", e.getMessage());
                return Health.down()
                        .withDetail("error", "Health check failed: " + e.getMessage())
                        .build();
            }
        }
    }
}
