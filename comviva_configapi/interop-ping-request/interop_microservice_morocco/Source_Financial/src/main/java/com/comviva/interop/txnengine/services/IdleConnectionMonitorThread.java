package com.comviva.interop.txnengine.services;

import java.util.concurrent.TimeUnit;

import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comviva.interop.txnengine.configuration.HttpClientProperties;
import com.comviva.interop.txnengine.enums.LogConstants;
import com.comviva.interop.txnengine.util.LoggerUtil;

@Service
public class IdleConnectionMonitorThread implements Runnable{

	 private static final Logger LOGGER = LoggerFactory.getLogger(IdleConnectionMonitorThread.class);
	 
	 private HttpClientProperties httpClientProperties;
	 
	 private PoolingHttpClientConnectionManager connectionManager;
	 
	 @Autowired
	 public IdleConnectionMonitorThread(HttpClientProperties httpClientProperties, PoolingHttpClientConnectionManager connectionManager) {
	        this.httpClientProperties = httpClientProperties;
	        this.connectionManager = connectionManager;
	 }
	 
	@Override
	public void run() {
		if (connectionManager != null) {
            connectionManager.closeExpiredConnections();
            connectionManager.closeIdleConnections(httpClientProperties.getHttpCloseIdleConnectionWaitTimeSecs(), TimeUnit.SECONDS);
        } else {
            String message = LoggerUtil.printLog(LogConstants.IDLE_CONNECTION_NOT_INITIALIZATION_EVENT.getValue(), null);
            LOGGER.info("run IdleConnectionMonitor - Http Client Connection manager is not initialised.. {}", message);
        }
	}

}
