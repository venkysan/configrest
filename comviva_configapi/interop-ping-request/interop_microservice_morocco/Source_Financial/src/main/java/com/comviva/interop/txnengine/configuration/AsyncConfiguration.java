package com.comviva.interop.txnengine.configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfiguration implements AsyncConfigurer {
  
	private ThreadPoolProperties threadPoolProperties;
	
	@Autowired
	public AsyncConfiguration(ThreadPoolProperties threadPoolProperties){
	this.threadPoolProperties=threadPoolProperties;
	
	}
	
  @Override
  public Executor getAsyncExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(threadPoolProperties.getCorePoolSize());
    executor.setMaxPoolSize(threadPoolProperties.getMaxPoolSize());
    executor.setQueueCapacity(threadPoolProperties.getQueueCapacity());
    executor.setThreadNamePrefix("interop-worker-exec-");
    executor.setKeepAliveSeconds(threadPoolProperties.getKeepAliveTime());
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    executor.setWaitForTasksToCompleteOnShutdown(threadPoolProperties.isWaitForJobsToCompleteOnShutdown());
    executor.initialize();
    return executor;
  }
  @Override
  public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
    return (ex, method, params) -> {
      Class<?> targetClass = method.getDeclaringClass();
      Logger log = LoggerFactory.getLogger(targetClass);
      log.error(ex.getMessage(), ex);
    };
  }
}
