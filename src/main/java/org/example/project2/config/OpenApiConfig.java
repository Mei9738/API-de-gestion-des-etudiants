package org.example.project2.config;

import feign.codec.ErrorDecoder;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI();
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new FeignClientErrorDecoder();
    }
}
