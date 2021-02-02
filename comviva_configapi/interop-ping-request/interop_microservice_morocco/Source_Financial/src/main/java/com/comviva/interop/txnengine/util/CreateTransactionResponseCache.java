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
import com.comviva.interop.txnengine.events.CreateTransactionResponseEvent;
import com.comviva.interop.txnengine.model.TransactionResponse;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

@Service
public class CreateTransactionResponseCache {

    
    private final Cache<String, DeferredResult<ResponseEntity<TransactionResponse>>> responseMap;
    
    private HttpClientProperties httpClientProperties;

    @Autowired
    public CreateTransactionResponseCache(HttpClientProperties httpClientProperties) {
        this.httpClientProperties = httpClientProperties;
        responseMap = CacheBuilder.newBuilder().expireAfterWrite(httpClientProperties.getRequestTimeout(), TimeUnit.MILLISECONDS)
                .build();
    }

    @Async("SetDeferredResultAsyncPool")
    @EventListener
    public void onApplicationEvent(CreateTransactionResponseEvent createTransactionResponseEvent) {
        DeferredResult<ResponseEntity<TransactionResponse>> deferredResult = deferredResultFor(
                createTransactionResponseEvent.getRequestId());
        if (null == deferredResult || deferredResult.hasResult()) {
            return;
        }
        if (!deferredResult.hasResult()) {
            deferredResult.setResult(
                    new ResponseEntity<>(createTransactionResponseEvent.getTransactionResponse(), HttpStatus.OK));
            removeFromCache(createTransactionResponseEvent.getRequestId());

        }
    }

    // generate deferred result obj
    public DeferredResult<ResponseEntity<TransactionResponse>> getDeferredResponseObj() {
        return new DeferredResult<>(httpClientProperties.getRequestTimeout(), "timed out internalTransaction");
    }

    public void putInCache(String interOpReferenceId,
            DeferredResult<ResponseEntity<TransactionResponse>> deferredResult) {
        responseMap.put(interOpReferenceId, deferredResult);
    }

    public void removeFromCache(String interOpReferenceId) {
        responseMap.invalidate(interOpReferenceId);
    }

    private DeferredResult<ResponseEntity<TransactionResponse>> deferredResultFor(String interOpReferenceId) {
        return responseMap.getIfPresent(interOpReferenceId);
    }
}
