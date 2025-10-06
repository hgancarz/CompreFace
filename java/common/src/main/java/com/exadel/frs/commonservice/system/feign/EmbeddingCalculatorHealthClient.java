package com.exadel.frs.commonservice.system.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        url = "${environment.servers.PYTHON.url}",
        name = "embedding-calculator-health"
)
public interface EmbeddingCalculatorHealthClient {

    @GetMapping(path = "/health")
    ResponseEntity<String> health();
}
