package com.exadel.frs.core.trainservice.system.health;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        url = "${app.feign.embedding-calculator.url:${environment.servers.PYTHON.url:http://compreface-core:3000}}",
        name = "embedding-calculator-health"
)
public interface EmbeddingCalculatorHealthClient {

    @GetMapping("/health")
    String health();
}
