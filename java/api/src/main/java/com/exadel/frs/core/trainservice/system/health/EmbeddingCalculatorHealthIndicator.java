/*
 * Copyright (c) 2020 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.exadel.frs.core.trainservice.system.health;

import com.exadel.frs.commonservice.sdk.embedding.feign.EmbeddingCalculatorHealthClient;
import com.exadel.frs.commonservice.sdk.embedding.feign.dto.HealthResponse;
import feign.FeignException;
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
            HealthResponse response = embeddingCalculatorHealthClient.health();
            
            if ("OK".equalsIgnoreCase(response.getStatus())) {
                return Health.up()
                        .withDetail("status", response.getStatus())
                        .build();
            } else {
                return Health.down()
                        .withDetail("status", response.getStatus())
                        .withDetail("error", "Embedding calculator returned non-OK status")
                        .build();
            }
        } catch (FeignException e) {
            log.warn("Failed to check embedding calculator health: {}", e.getMessage());
            return Health.down()
                    .withDetail("error", "Failed to connect to embedding calculator")
                    .withDetail("exception", e.getMessage())
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error while checking embedding calculator health", e);
            return Health.down()
                    .withDetail("error", "Unexpected error occurred")
                    .withDetail("exception", e.getMessage())
                    .build();
        }
    }
}
