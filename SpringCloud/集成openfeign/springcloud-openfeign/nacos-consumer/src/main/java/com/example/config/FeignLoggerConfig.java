package com.example.config;


import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 定义feign组件的日志级别
 */
@Configuration
public class FeignLoggerConfig {

    @Bean
    Logger.Level feignLogger() {
        return Logger.Level.FULL;
    }
}