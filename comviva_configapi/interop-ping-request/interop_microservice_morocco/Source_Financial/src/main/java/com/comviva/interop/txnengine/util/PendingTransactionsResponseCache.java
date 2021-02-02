package com.comviva.interop.txnengine.util;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import com.comviva.interop.txnengine.configuration.HttpClientProperties;
import com.comviva.interop.txnengine.events.PendingTransactionsResponseEvent;
import com.comviva.interop.txnengine.model.PendingTransactionsResponse;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

@Service
public class PendingTransactionsResponseCache {
    

    private final Cache<String, DeferredResult<ResponseEntity<PendingTransactionsResponse>>> responseMap;
    
    private HttpClientProperties httpClientProperties;

    @Autowired
    public PendingTransactionsResponseCache(HttpClientProperties httpClientProperties) {
        this.httpClientProperties = httpClientProperties;
        responseMap = CacheBuilder.newBuilder().expireAfterWrite(httpClientProperties.getRequestTimeout(), TimeUnit.MILLISECONDS)
                .build();
    }

    @Async("SetDeferredResultAsyncPool")
    @EventListener
    public void onApplicationEvent(PendingTransactionsResponseEvent pendingTransactionsResponseEvent) {
        DeferredResult<ResponseEntity<PendingTransactionsResponse>> deferredResult = deferredResultFor(
        		pendingTransactionsResponseEvent.getReqId());
        if (null == deferredResult || deferredResult.hasResult()) {
            return;
        }
        if (!deferredResult.hasResult()) {
            deferredResult.setResult(
                    new ResponseEntity<>(pendingTransactionsResponseEvent.getPendingTransactionsResponse(), HttpStatus.OK));
            removeFromCache(pendingTransactionsResponseEvent.getReqId());

        }
    }

    // generate deferred result obj
    public DeferredResult<ResponseEntity<PendingTransactionsResponse>> getDeferredResponseObj() {
        return new DeferredResult<>(httpClientProperties.getRequestTimeout(), "timed out internalTransaction");
    }

    public void putInCache(String interOpReferenceId,
            DeferredResult<ResponseEntity<PendingTransactionsResponse>> deferredResult) {
        responseMap.put(interOpReferenceId, deferredResult);
    }

    public void removeFromCache(String interOpReferenceId) {
        responseMap.invalidate(interOpReferenceId);
    }

    private DeferredResult<ResponseEntity<PendingTransactionsResponse>> deferredResultFor(String interOpReferenceId) {
        return responseMap.getIfPresent(interOpReferenceId);
    }
}
