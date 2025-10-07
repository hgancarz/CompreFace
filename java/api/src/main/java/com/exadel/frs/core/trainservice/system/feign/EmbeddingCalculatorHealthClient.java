package com.exadel.frs.core.trainservice.system.feign;

import com.exadel.frs.core.trainservice.config.EmbeddingCalculatorFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
    name = "embeddingCalculatorHealthClient",
    url = "${app.services.embedding-calculator.url}",
    configuration = EmbeddingCalculatorFeignConfig.class
)
public interface EmbeddingCalculatorHealthClient {

    @GetMapping("/healthcheck")
    HealthCheckResponse healthCheck();
    
    class HealthCheckResponse {
        private String status;
        
        public String getStatus() {
            return status;
        }
        
        public void setStatus(String status) {
            this.status = status;
        }
    }
}
