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
import com.comviva.interop.txnengine.events.TransactionQuotationResponseEvent;
import com.comviva.interop.txnengine.model.QuotationResponse;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

@Service
public class TransactionQuotationResponseCache {
    

    private final Cache<String, DeferredResult<ResponseEntity<QuotationResponse>>> responseMap;
    
    private HttpClientProperties httpClientProperties;

    @Autowired
    public TransactionQuotationResponseCache(HttpClientProperties httpClientProperties) {
        this.httpClientProperties = httpClientProperties;
        responseMap = CacheBuilder.newBuilder().expireAfterWrite(httpClientProperties.getRequestTimeout(), TimeUnit.MILLISECONDS)
                .build();
    }

    @Async("SetDeferredResultAsyncPool")
    @EventListener
    public void onApplicationEvent(TransactionQuotationResponseEvent transactionQuotationResponseEvent) {
        DeferredResult<ResponseEntity<QuotationResponse>> deferredResult = deferredResultFor(
                transactionQuotationResponseEvent.getReqId());
        if (null == deferredResult || deferredResult.hasResult()) {
            return;
        }
        if (!deferredResult.hasResult()) {
            deferredResult.setResult(
                    new ResponseEntity<>(transactionQuotationResponseEvent.getQuotationResponse(), HttpStatus.OK));
            removeFromCache(transactionQuotationResponseEvent.getReqId());

        }
    }

    // generate deferred result obj
    public DeferredResult<ResponseEntity<QuotationResponse>> getDeferredResponseObj() {
        return new DeferredResult<>(httpClientProperties.getRequestTimeout(), "timed out internalTransaction");
    }

    public void putInCache(String interOpReferenceId,
            DeferredResult<ResponseEntity<QuotationResponse>> deferredResult) {
        responseMap.put(interOpReferenceId, deferredResult);
    }

    public void removeFromCache(String interOpReferenceId) {
        responseMap.invalidate(interOpReferenceId);
    }

    private DeferredResult<ResponseEntity<QuotationResponse>> deferredResultFor(String interOpReferenceId) {
        return responseMap.getIfPresent(interOpReferenceId);
    }
}
