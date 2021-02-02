package com.comviva.interop.txnengine.configuration;

import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    private HttpClientProperties httpClientProperties;
    
    private CloseableHttpClient httpClient;

    @Autowired
    RestTemplateConfig(HttpClientProperties httpClientProperties, CloseableHttpClient httpClient) {
        this.httpClientProperties = httpClientProperties;
        this.httpClient = httpClient;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(clientHttpRequestFactory());
    }

    @Bean
    public HttpComponentsClientHttpRequestFactory clientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setHttpClient(httpClient);
        return clientHttpRequestFactory;
    }

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadNamePrefix("interop-http-pool-scheduler-");
        scheduler.setPoolSize(httpClientProperties.getHttpScheduledExecutorServicePoolSize());
        return scheduler;
    }
}
