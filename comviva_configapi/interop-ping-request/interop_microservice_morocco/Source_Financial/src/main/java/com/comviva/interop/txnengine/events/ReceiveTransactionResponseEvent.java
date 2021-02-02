package com.comviva.interop.txnengine.events;

import org.springframework.context.ApplicationEvent;

import com.comviva.interop.txnengine.model.ReceiveTransactionResponse;

public class ReceiveTransactionResponseEvent extends ApplicationEvent {

    private static final long serialVersionUID = 6586742443307509008L;

    private transient ReceiveTransactionResponse receiveTransactionResponse;

    private String requestId;

    public ReceiveTransactionResponseEvent(Object source, ReceiveTransactionResponse receiveTransactionResponse, String requestId) {
        super(source);
        this.receiveTransactionResponse = receiveTransactionResponse;
        this.requestId = requestId;
    }

    public ReceiveTransactionResponse getReceiveTransactionResponse() {
        return this.receiveTransactionResponse;
    }

    public String getRequestId() {
        return this.requestId;
    }

}