package com.reactive.reactivewebapi.bean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateFactory {

    /**
     * @return create restTemplate for calling external bean api with custom configuration
     */
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
