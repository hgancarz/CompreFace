package com.exadel.frs.system.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("calculatorHealthIndicator")
public class CalculatorHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        return Health.up().build();
    }
}
