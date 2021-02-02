package com.comviva.interop.txnengine.events;

import org.springframework.context.ApplicationEvent;

import com.comviva.interop.txnengine.model.QuotationResponse;

public class TransactionQuotationResponseEvent extends ApplicationEvent {

    private static final long serialVersionUID = 4894389827141318569L;

    private transient QuotationResponse quotationResponse;

    private transient String reqId;

    public TransactionQuotationResponseEvent(Object source, QuotationResponse quotationResponse, String reqId) {
        super(source);
        this.quotationResponse = quotationResponse;
        this.reqId = reqId;
    }

    public QuotationResponse getQuotationResponse() {
        return this.quotationResponse;
    }

    public String getReqId() {
        return this.reqId;
    }

}