package com.comviva.interop.txnengine.configuration;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.comviva.interop.txnengine.enums.LogConstants;
import com.comviva.interop.txnengine.services.IdleConnectionMonitorThread;
import com.comviva.interop.txnengine.util.LoggerUtil;

/**
 * - Supports both HTTP and HTTPS - Uses a connection pool to re-use connections
 * and save overhead of creating connections. - Has a custom connection
 * keep-alive strategy (to apply a default keep-alive if one isn't specified) -
 * Starts an idle connection monitor to continuously clean up stale connections.
 */
@Configuration
@EnableScheduling
public class HttpClientConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientConfig.class);
    private HttpClientProperties httpClientProperties;
    private static final String TIMEOUT = "timeout";
    private static final int TIMEOUT_MULTIPLIER = 1000;

    @Autowired
    public HttpClientConfig(HttpClientProperties httpClientProperties) {
        this.httpClientProperties = httpClientProperties;
    }

    @Bean
    public PoolingHttpClientConnectionManager poolingConnectionManager() {
        SSLContextBuilder builder = new SSLContextBuilder();
        try {
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
        } catch (NoSuchAlgorithmException | KeyStoreException e) {
            String message = LoggerUtil.printLog(LogConstants.HTTP_CLIENT_CONFIGURATION_EVENT.getValue(), e);
            LOGGER.info("Pooling Connection Manager Initialisation failure because of due to NoSuchAlgorithmException | KeyStoreException {}" ,message);
        }

        SSLConnectionSocketFactory sslsf = null;
        try {
            sslsf = new SSLConnectionSocketFactory(builder.build());
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            String message = LoggerUtil.printLog(LogConstants.HTTP_CLIENT_CONFIGURATION_EVENT.getValue(), e);
            LOGGER.info("Pooling Connection Manager Initialisation failure because of due to NoSuchAlgorithmException | KeyStoreException {}" ,message);
        }

        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", sslsf).register("http", new PlainConnectionSocketFactory()).build();

        PoolingHttpClientConnectionManager poolingConnectionManager = new PoolingHttpClientConnectionManager(
                socketFactoryRegistry);
        poolingConnectionManager.setMaxTotal(httpClientProperties.getHttpMaxTotalConnections());
        return poolingConnectionManager;
    }

    @Bean
    public ConnectionKeepAliveStrategy connectionKeepAliveStrategy() {
        return (response, context) -> {
            HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
            while (it.hasNext()) {
                HeaderElement he = it.nextElement();
                String param = he.getName();
                String value = he.getValue();

                if (value != null && TIMEOUT.equalsIgnoreCase(param)) {
                    return Long.parseLong(value) * TIMEOUT_MULTIPLIER;
                }
            }
            return httpClientProperties.getHttpDefaultKeepAliveTimeMillis();
        };
    }

    @Bean
    public CloseableHttpClient httpClient() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(httpClientProperties.getHttpRequestTimeout())
                .setConnectTimeout(httpClientProperties.getHttpConnectTimeout()).setSocketTimeout(httpClientProperties.getHttpSocketTimeout())
                .build();

        return HttpClients.custom().setDefaultRequestConfig(requestConfig)
                .setConnectionManager(poolingConnectionManager()).setKeepAliveStrategy(connectionKeepAliveStrategy())
                .build();
    }

    @Bean
    public IdleConnectionMonitorThread idleConnectionMonitor(final PoolingHttpClientConnectionManager connectionManager) {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        IdleConnectionMonitorThread idelConnectionMonitorServiceThread = new IdleConnectionMonitorThread(httpClientProperties, connectionManager);
        scheduledExecutorService.scheduleAtFixedRate(idelConnectionMonitorServiceThread, httpClientProperties.getInitialDelay(),
                httpClientProperties.getIdleConnectionMonitorFixedDelay(), TimeUnit.MILLISECONDS);
        return idelConnectionMonitorServiceThread;
    }
}
