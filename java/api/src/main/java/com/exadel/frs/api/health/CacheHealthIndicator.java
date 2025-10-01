package com.exadel.frs.api.health;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentMap;

/**
 * Health indicator for cache state. Enumerates caches via CacheManager and includes
 * basic details like cache names and sizes (when available).
 */
@Component("cache")
public class CacheHealthIndicator implements HealthIndicator {

    private final CacheManager cacheManager;

    @Autowired
    public CacheHealthIndicator(final CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public Health health() {
        if (cacheManager == null) {
            return Health.down().withDetail("message", "CacheManager is not available").build();
        }

        Collection<String> names = cacheManager.getCacheNames();
        Map<String, Object> details = new LinkedHashMap<>();
        details.put("caches", new ArrayList<>(names));

        Map<String, Integer> sizes = new LinkedHashMap<>();
        for (String name : names) {
            try {
                Cache cache = cacheManager.getCache(name);
                if (cache instanceof ConcurrentMapCache) {
                    Object nativeCache = ((ConcurrentMapCache) cache).getNativeCache();
                    if (nativeCache instanceof ConcurrentMap) {
                        sizes.put(name, ((ConcurrentMap<?, ?>) nativeCache).size());
                    }
                }
            } catch (Exception ignored) {
                // Ignore issues querying cache size; health should still be UP if accessible
            }
        }
        if (!sizes.isEmpty()) {
            details.put("sizes", sizes);
        }

        return Health.up().withDetails(details).build();
    }
}
