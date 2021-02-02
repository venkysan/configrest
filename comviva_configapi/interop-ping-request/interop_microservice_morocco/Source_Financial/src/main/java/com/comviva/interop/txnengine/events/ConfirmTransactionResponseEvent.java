package com.comviva.interop.txnengine.events;

import org.springframework.context.ApplicationEvent;

import com.comviva.interop.txnengine.model.ConfirmTransactionResponse;

public class ConfirmTransactionResponseEvent extends ApplicationEvent {

    private static final long serialVersionUID = 4894389827141318569L;

    private transient ConfirmTransactionResponse confirmTransactionResponse;

    private transient String reqId;

    public ConfirmTransactionResponseEvent(Object source, ConfirmTransactionResponse confirmTransactionResponse, String reqId) {
        super(source);
        this.confirmTransactionResponse = confirmTransactionResponse;
        this.reqId = reqId;
    }

    public ConfirmTransactionResponse getConfirmTransactionsResponse() {
        return this.confirmTransactionResponse;
    }

    public String getReqId() {
        return this.reqId;
    }

}