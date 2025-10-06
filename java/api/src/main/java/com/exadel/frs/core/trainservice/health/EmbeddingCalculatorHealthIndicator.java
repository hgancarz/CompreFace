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

import com.exadel.frs.commonservice.sdk.embedding.feign.EmbeddingFeignClient;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Health indicator for the embedding calculator service.
 * Bean name is 'embeddingCalculatorHealthIndicator' so the actuator id is 'embeddingCalculator'.
 */
@Component
public class EmbeddingCalculatorHealthIndicator implements HealthIndicator {

    private final EmbeddingFeignClient embeddingFeignClient;

    public EmbeddingCalculatorHealthIndicator(EmbeddingFeignClient embeddingFeignClient) {
        this.embeddingFeignClient = embeddingFeignClient;
    }

    @Override
    public Health health() {
        try {
            embeddingFeignClient.health();
            return Health.up().build();
        } catch (Exception ex) {
            return Health.down()
                    .withDetail("error", ex.getClass().getSimpleName())
                    .withDetail("message", ex.getMessage())
                    .build();
        }
    }
}
