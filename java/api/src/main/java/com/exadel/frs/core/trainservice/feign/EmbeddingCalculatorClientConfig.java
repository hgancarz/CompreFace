package com.exadel.frs.core.trainservice.feign;

import feign.Request;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class EmbeddingCalculatorClientConfig {

    @Value("${app.feign.embedding-calculator.connect-timeout:5000}")
    private int connectTimeout;

    @Value("${app.feign.embedding-calculator.read-timeout:10000}")
    private int readTimeout;

    @Bean
    public Request.Options options() {
        return new Request.Options(connectTimeout, readTimeout);
    }
}
