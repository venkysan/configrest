package com.comviva.interop.txnengine.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:thread_pool.properties")
public class ThreadPoolProperties {

    @Value("${async.threadpool.corePoolSize:50}")
    private int corePoolSize;

    @Value("${async.threadpool.maxPoolSize:1000}")
    private int maxPoolSize;

    @Value("${async.threadpool.queueCapacity:100}")
    private int queueCapacity;

    @Value("${async.threadpool.keepAliveTime:60}")
    private int keepAliveTime;

    @Value("${async.threadpool.waitForJobsToCompleteOnShutdown:false}")
    private String waitForJobsToCompleteOnShutdown;

    @Value("${deferred.result.async.threadpool.corePoolSize:30}")
    private int deferredResultCorePoolSize;

    @Value("${deferred.result.async.threadpool.maxPoolSize:1000}")
    private int deferredResultMaxPoolSize;

    @Value("${deferred.result.async.threadpool.queueCapacity:100}")
    private int deferredResultQueueCapacity;

    @Value("${deferred.result.async.threadpool.keepAliveTime:60}")
    private int deferredResultKeepAliveTime;

    @Value("${deferred.result.async.threadpool.waitForJobsToCompleteOnShutdown:false}")
    private String deferredResultWaitForJobsToCompleteOnShutdown;

    @Value("${third.party.async.threadpool.corePoolSize:50}")
    private int thirdPartyCorePoolSize;

    @Value("${third.party.async.threadpool.maxPoolSize:1000}")
    private int thirdPartyMaxPoolSize;

    @Value("${third.party.async.threadpool.queueCapacity:100}")
    private int thirdPartyQueueCapacity;

    @Value("${third.party.async.threadpool.keepAliveTime:60}")
    private int thirdPartyKeepAliveTime;

    @Value("${third.party.async.threadpool.waitForJobsToCompleteOnShutdown:false}")
    private String thirdPartyWaitForJobsToCompleteOnShutdown;

    @Value("${velocity.parser.pool.size:20}")
    private String velocityParserPoolSize;

    public String getVelocityParserPoolSize() {
        return velocityParserPoolSize;
    }

    public int getThirdPartyCorePoolSize() {
        return thirdPartyCorePoolSize;
    }

    public int getThirdPartyMaxPoolSize() {
        return thirdPartyMaxPoolSize;
    }

    public int getThirdPartyQueueCapacity() {
        return thirdPartyQueueCapacity;
    }

    public int getThirdPartyKeepAliveTime() {
        return thirdPartyKeepAliveTime;
    }

    public boolean isThirdPartyWaitForJobsToCompleteOnShutdown() {
        return ("true".equalsIgnoreCase(thirdPartyWaitForJobsToCompleteOnShutdown));
    }

    public int getDeferredResultCorePoolSize() {
        return deferredResultCorePoolSize;
    }

    public int getDeferredResultMaxPoolSize() {
        return deferredResultMaxPoolSize;
    }

    public int getDeferredResultQueueCapacity() {
        return deferredResultQueueCapacity;
    }

    public int getDeferredResultKeepAliveTime() {
        return deferredResultKeepAliveTime;
    }

    public boolean isDeferredResultWaitForJobsToCompleteOnShutdown() {
        return ("true".equalsIgnoreCase(deferredResultWaitForJobsToCompleteOnShutdown));
    }

    public boolean isWaitForJobsToCompleteOnShutdown() {
        return ("true".equalsIgnoreCase(waitForJobsToCompleteOnShutdown));
    }

    public int getKeepAliveTime() {
        return keepAliveTime;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public int getQueueCapacity() {
        return queueCapacity;
    }
}
