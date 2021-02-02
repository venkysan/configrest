package com.comviva.interop.txnengine.events;

import org.springframework.context.ApplicationEvent;

import com.comviva.interop.txnengine.model.PendingTransactionsResponse;

public class PendingTransactionsResponseEvent extends ApplicationEvent {

    private static final long serialVersionUID = 4894389827141318569L;

    private transient PendingTransactionsResponse pendingTransactionsResponse;

    private transient String reqId;

    public PendingTransactionsResponseEvent(Object source, PendingTransactionsResponse pendingTransactionsResponse, String reqId) {
        super(source);
        this.pendingTransactionsResponse = pendingTransactionsResponse;
        this.reqId = reqId;
    }

    public PendingTransactionsResponse getPendingTransactionsResponse() {
        return this.pendingTransactionsResponse;
    }

    public String getReqId() {
        return this.reqId;
    }

}