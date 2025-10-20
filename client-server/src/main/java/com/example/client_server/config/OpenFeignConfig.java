package com.example.client_server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Logger;

@Configuration
public class OpenFeignConfig {

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }

}
