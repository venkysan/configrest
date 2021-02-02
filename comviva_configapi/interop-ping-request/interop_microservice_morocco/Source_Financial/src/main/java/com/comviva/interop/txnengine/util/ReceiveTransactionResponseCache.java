package com.comviva.interop.txnengine.util;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import com.comviva.interop.txnengine.configuration.HttpClientProperties;
import com.comviva.interop.txnengine.events.ReceiveTransactionResponseEvent;
import com.comviva.interop.txnengine.model.ReceiveTransactionResponse;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

@Service
public class ReceiveTransactionResponseCache {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReceiveTransactionResponseCache.class);
	
    private final Cache<String, DeferredResult<ResponseEntity<ReceiveTransactionResponse>>> responseMap;
    
    private HttpClientProperties httpClientProperties;

    @Autowired
    public ReceiveTransactionResponseCache(HttpClientProperties httpClientProperties) {
        this.httpClientProperties = httpClientProperties;
        responseMap = CacheBuilder.newBuilder().expireAfterWrite(httpClientProperties.getRequestTimeout(), TimeUnit.MILLISECONDS)
                .build();
    }

    @Async("SetDeferredResultAsyncPool")
    @EventListener
    public void onApplicationEvent(ReceiveTransactionResponseEvent receiveTransactionResponseEvent) {
        DeferredResult<ResponseEntity<ReceiveTransactionResponse>> deferredResult = deferredResultFor(
        		receiveTransactionResponseEvent.getRequestId());
        if (null == deferredResult || deferredResult.hasResult()) {
            return;
        }
        if (!deferredResult.hasResult()) {
        	LOGGER.info("Sending response back to channel: {}",
                    		receiveTransactionResponseEvent.getReceiveTransactionResponse());

            deferredResult.setResult(
                    new ResponseEntity<>(receiveTransactionResponseEvent.getReceiveTransactionResponse(), HttpStatus.OK));
            removeFromCache(receiveTransactionResponseEvent.getRequestId());

        }
    }

    // generate deferred result obj
    public DeferredResult<ResponseEntity<ReceiveTransactionResponse>> getDeferredResponseObj() {
        return new DeferredResult<>(httpClientProperties.getRequestTimeout(), "timed out internalTransaction");
    }

    public void putInCache(String interOpReferenceId,
            DeferredResult<ResponseEntity<ReceiveTransactionResponse>> deferredResult) {
        responseMap.put(interOpReferenceId, deferredResult);
    }

    public void removeFromCache(String interOpReferenceId) {
        responseMap.invalidate(interOpReferenceId);
    }

    private DeferredResult<ResponseEntity<ReceiveTransactionResponse>> deferredResultFor(String interOpReferenceId) {
        return responseMap.getIfPresent(interOpReferenceId);
    }
}
