package com.comviva.interop.txnengine.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Getter;

@Configuration
@PropertySource("classpath:http_client.properties")
@Getter
public class HttpClientProperties {

    @Value("${requestTimeout.in.milli:100000}")
    private long requestTimeout;
    
    @Value("${http.connect.timeout:30000}")
    private int httpConnectTimeout;

    @Value("${http.request.timeout:30000}")
    private int httpRequestTimeout;

    @Value("${http.socket.timeout:60000}")
    private int httpSocketTimeout;

    @Value("${http.max.total.connections:50}")
    private int httpMaxTotalConnections;

    @Value("${http.default.keep.alive.time.millis:20000}")
    private int httpDefaultKeepAliveTimeMillis;

    @Value("${http.close.idle.connection.wait.time.secs:30}")
    private int httpCloseIdleConnectionWaitTimeSecs;

    @Value("${http.scheduled.executor.service.pool.size:50}")
    private int httpScheduledExecutorServicePoolSize;
    
    @Value("${idleConnectionMonitor.fixedDelay.in.milliseconds:#{5000}}")
    private int idleConnectionMonitorFixedDelay;
    
    @Value("${initial.delay:0}")
    private int initialDelay;
}
