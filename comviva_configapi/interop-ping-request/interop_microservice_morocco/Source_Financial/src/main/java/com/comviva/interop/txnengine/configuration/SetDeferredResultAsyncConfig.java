package com.comviva.interop.txnengine.configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class SetDeferredResultAsyncConfig {

    private ThreadPoolProperties threadPoolProperties;

    @Autowired
    public SetDeferredResultAsyncConfig(ThreadPoolProperties threadPoolProperties) {
        this.threadPoolProperties = threadPoolProperties;

    }

    @Bean(name = "SetDeferredResultAsyncPool")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(threadPoolProperties.getDeferredResultCorePoolSize());
        executor.setMaxPoolSize(threadPoolProperties.getDeferredResultMaxPoolSize());
        executor.setQueueCapacity(threadPoolProperties.getDeferredResultQueueCapacity());
        executor.setThreadNamePrefix("interop-deferred-result-worker-exec-");
        executor.setKeepAliveSeconds(threadPoolProperties.getDeferredResultKeepAliveTime());
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(
                threadPoolProperties.isDeferredResultWaitForJobsToCompleteOnShutdown());
        executor.initialize();
        return executor;
    }

}
