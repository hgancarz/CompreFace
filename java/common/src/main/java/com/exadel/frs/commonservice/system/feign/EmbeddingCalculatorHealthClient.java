package com.exadel.frs.commonservice.system.feign;

import com.exadel.frs.core.trainservice.config.EmbeddingCalculatorFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        url = "\${environment.servers.PYTHON.url}",
        name = "embedding-calculator-health",
        configuration = EmbeddingCalculatorFeignConfig.class
)
public interface EmbeddingCalculatorHealthClient {

    @GetMapping(path = "/health")
    String health();
}
