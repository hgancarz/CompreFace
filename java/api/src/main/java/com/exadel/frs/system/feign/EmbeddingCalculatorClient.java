package com.exadel.frs.system.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        url = "${environment.servers.PYTHON.url}",
        name = "embedding-calculator"
)
public interface EmbeddingCalculatorClient {

    @GetMapping(path = "/health")
    ResponseEntity<String> health();
}
