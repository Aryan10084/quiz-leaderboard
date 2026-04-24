package com.quiz.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Spring configuration: exposes a RestTemplate bean with sensible timeouts.
 */
@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        // 10 second connect timeout
        factory.setConnectTimeout(10_000);
        // 30 second read timeout (generous for slow APIs)
        factory.setReadTimeout(30_000);
        return new RestTemplate(factory);
    }
}
