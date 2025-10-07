package com.exadel.frs.core.trainservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
    name = "embedding-calculator", 
    url = "${environment.servers.PYTHON.url}",
    configuration = EmbeddingCalculatorClientConfig.class
)
public interface EmbeddingCalculatorClient {

    @GetMapping("/health")
    HealthResponse health();
    
    class HealthResponse {
        private String status;
        
        public String getStatus() {
            return status;
        }
        
        public void setStatus(String status) {
            this.status = status;
        }
    }
}
