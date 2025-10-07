package com.exadel.frs.core.trainservice.config;

import feign.Request;
import feign.Retryer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

import static feign.Logger.Level.BASIC;

@Configuration
public class EmbeddingCalculatorFeignConfig {

    @Value("${app.feign.embedding-calculator.connect-timeout}")
    private int connectTimeout;

    @Value("${app.feign.embedding-calculator.read-timeout}")
    private int readTimeout;

    @Bean
    public FeignClientProperties.FeignClientConfiguration embeddingCalculatorFeignConfiguration() {
        FeignClientProperties.FeignClientConfiguration config = new FeignClientProperties.FeignClientConfiguration();
        config.setConnectTimeout(connectTimeout);
        config.setReadTimeout(readTimeout);
        config.setLoggerLevel(BASIC);
        return config;
    }
}
