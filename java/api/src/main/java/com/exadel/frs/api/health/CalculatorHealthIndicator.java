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

package com.exadel.frs.api.health;

import com.exadel.frs.commonservice.sdk.faces.feign.FacesFeignClient;
import com.exadel.frs.commonservice.sdk.faces.feign.dto.FacesStatusResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class CalculatorHealthIndicator implements HealthIndicator {

    private final FacesFeignClient facesFeignClient;

    @Override
    public Health health() {
        Instant start = Instant.now();
        try {
            FacesStatusResponse status = facesFeignClient.getStatus();
            Duration responseTime = Duration.between(start, Instant.now());
            
            Health.Builder healthBuilder = Health.up()
                    .withDetail("responseTime", responseTime.toMillis() + "ms")
                    .withDetail("endpoint", "GET /status");
            
            if (status != null) {
                healthBuilder.withDetail("calculatorStatus", status.getStatus());
            }
            
            return healthBuilder.build();
        } catch (Exception e) {
            Duration responseTime = Duration.between(start, Instant.now());
            log.warn("Calculator health check failed: {}", e.getMessage());
            return Health.down(e)
                    .withDetail("responseTime", responseTime.toMillis() + "ms")
                    .withDetail("endpoint", "GET /status")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
