package com.exadel.frs.core.trainservice.config;

import com.exadel.frs.commonservice.system.feign.EmbeddingCalculatorHealthClient;
import feign.Request;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmbeddingCalculatorFeignConfig {

    @Value("${app.feign.embedding-calculator.connect-timeout:5000}")
    private int connectTimeout;

    @Value("${app.feign.embedding-calculator.read-timeout:10000}")
    private int readTimeout;

    @Bean
    public Request.Options embeddingCalculatorRequestOptions() {
        return new Request.Options(connectTimeout, readTimeout);
    }
}
