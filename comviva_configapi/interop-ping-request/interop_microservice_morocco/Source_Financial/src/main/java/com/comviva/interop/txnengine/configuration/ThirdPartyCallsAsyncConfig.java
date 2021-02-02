package com.comviva.interop.txnengine.configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ThirdPartyCallsAsyncConfig {

    private ThreadPoolProperties threadPoolProperties;

    @Autowired
    public ThirdPartyCallsAsyncConfig(ThreadPoolProperties threadPoolProperties) {
        this.threadPoolProperties = threadPoolProperties;

    }

    @Bean(name = "ThirdPartyCallsAsyncPool")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(threadPoolProperties.getThirdPartyCorePoolSize());
        executor.setMaxPoolSize(threadPoolProperties.getThirdPartyMaxPoolSize());
        executor.setQueueCapacity(threadPoolProperties.getThirdPartyQueueCapacity());
        executor.setThreadNamePrefix("interop-third-party-worker-exec-");
        executor.setKeepAliveSeconds(threadPoolProperties.getThirdPartyKeepAliveTime());
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(
                threadPoolProperties.isThirdPartyWaitForJobsToCompleteOnShutdown());
        executor.initialize();
        return executor;
    }

}
