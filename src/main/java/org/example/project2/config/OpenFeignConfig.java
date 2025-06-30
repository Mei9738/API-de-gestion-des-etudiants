package org.example.project2.config;

import feign.Retryer;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "org.example.project2.client")
public class OpenFeignConfig {
    @Bean
    public Retryer retryer() {
        return new Retryer.Default();
    }
}
