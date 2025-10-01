package com.exadel.frs.core.trainservice.health;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Cache health indicator that verifies required caches exist and are initialized.
 * Bean name is 'cacheHealthIndicator' so the actuator id is 'cache'.
 *
 * Configuration properties:
 *  - app.health.cache.required-names: comma-separated list of required cache names (default: none)
 *  - app.health.cache.strict: if true, DOWN when required caches missing or not initialized (default: false)
 *  - app.health.cache.warmup-check: if true, attempt a lightweight warmup/access on required caches (default: false)
 */
@Component
public class CacheHealthIndicator implements HealthIndicator {

    private final CacheManager cacheManager;
    private final Set<String> requiredCaches;
    private final boolean strict;
    private final boolean warmupCheck;

    public CacheHealthIndicator(
            CacheManager cacheManager,
            @Value("${app.health.cache.required-names:}") String requiredNames,
            @Value("${app.health.cache.strict:false}") boolean strict,
            @Value("${app.health.cache.warmup-check:false}") boolean warmupCheck
    ) {
        this.cacheManager = cacheManager;
        this.strict = strict;
        this.warmupCheck = warmupCheck;
        this.requiredCaches = Arrays.stream(Optional.ofNullable(requiredNames).orElse("").split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public Health health() {
        List<String> available = new ArrayList<>(cacheManager.getCacheNames());
        Set<String> missing = new LinkedHashSet<>();
        Set<String> notInitialized = new LinkedHashSet<>();

        for (String name : requiredCaches) {
            Cache cache = cacheManager.getCache(name);
            if (cache == null) {
                missing.add(name);
                continue;
            }
            if (warmupCheck) {
                try {
                    Object nativeCache = cache.getNativeCache();
                    if (nativeCache == null || nativeCache.toString() == null) {
                        notInitialized.add(name);
                    }
                } catch (Throwable t) {
                    notInitialized.add(name);
                }
            }
        }

        Health.Builder builder;
        if (strict && (!missing.isEmpty() || !notInitialized.isEmpty())) {
            builder = Health.down();
        } else {
            builder = Health.up();
        }

        builder.withDetail("managerType", cacheManager.getClass().getName())
               .withDetail("available", available)
               .withDetail("required", new ArrayList<>(requiredCaches));

        if (!missing.isEmpty()) {
            builder.withDetail("missing", new ArrayList<>(missing));
        }
        if (!notInitialized.isEmpty()) {
            builder.withDetail("notInitialized", new ArrayList<>(notInitialized));
        }
        builder.withDetail("strict", strict)
               .withDetail("warmupCheck", warmupCheck);

        return builder.build();
    }
}
