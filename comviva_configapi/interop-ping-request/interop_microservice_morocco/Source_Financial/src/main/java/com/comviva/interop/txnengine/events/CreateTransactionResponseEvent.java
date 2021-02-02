package com.comviva.interop.txnengine.events;

import org.springframework.context.ApplicationEvent;

import com.comviva.interop.txnengine.model.TransactionResponse;

public class CreateTransactionResponseEvent extends ApplicationEvent {

    private static final long serialVersionUID = 6586742443307509008L;

    private transient TransactionResponse transactionResponse;

    private String requestId;

    public CreateTransactionResponseEvent(Object source, TransactionResponse transactionResponse, String requestId) {
        super(source);
        this.transactionResponse = transactionResponse;
        this.requestId = requestId;
    }

    public TransactionResponse getTransactionResponse() {
        return this.transactionResponse;
    }

    public String getRequestId() {
        return this.requestId;
    }

}