package com.exadel.frs.core.trainservice.system.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        url = "${environment.servers.PYTHON.url}",
        name = "embedding-calculator-health"
)
public interface EmbeddingCalculatorHealthClient {

    @GetMapping(path = "/health")
    void health();
}
