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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class CacheHealthIndicator implements HealthIndicator {

    private final CacheManager cacheManager;

    @Override
    public Health health() {
        try {
            if (cacheManager == null) {
                return Health.down()
                        .withDetail("error", "CacheManager is not available")
                        .build();
            }

            String[] cacheNames = cacheManager.getCacheNames();
            Map<String, Object> cacheDetails = new HashMap<>();

            for (String cacheName : cacheNames) {
                Cache cache = cacheManager.getCache(cacheName);
                if (cache != null) {
                    Map<String, Object> cacheInfo = new HashMap<>();
                    cacheInfo.put("type", cache.getClass().getSimpleName());
                    
                    // Try to access the cache to verify it's working
                    try {
                        cache.get("health-check-test");
                        cacheInfo.put("accessible", true);
                    } catch (Exception e) {
                        cacheInfo.put("accessible", false);
                        cacheInfo.put("error", e.getMessage());
                    }
                    
                    cacheDetails.put(cacheName, cacheInfo);
                } else {
                    cacheDetails.put(cacheName, "cache-not-found");
                }
            }

            return Health.up()
                    .withDetail("cacheCount", cacheNames.length)
                    .withDetail("caches", cacheDetails)
                    .build();

        } catch (Exception e) {
            log.warn("Cache health check failed: {}", e.getMessage());
            return Health.down(e)
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
