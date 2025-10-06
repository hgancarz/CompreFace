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

package com.exadel.frs.core.trainservice.health;

import com.exadel.frs.commonservice.sdk.faces.feign.FacesHealthFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmbeddingCalculatorHealthIndicator implements HealthIndicator {

    private final FacesHealthFeignClient facesHealthFeignClient;

    @Override
    public Health health() {
        try {
            String response = facesHealthFeignClient.checkHealth();
            log.debug("Embedding calculator health check successful: {}", response);
            return Health.up()
                    .withDetail("service", "embedding-calculator")
                    .withDetail("response", response)
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
