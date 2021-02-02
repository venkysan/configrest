package com.comviva.interop.txnengine.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class PendingTransactionRequest implements RequestWrapper {

    private String msisdn;
    
    private String numberOfTransactions;
    
    private String lang;

    public PendingTransactionRequest(String msisdn, String numberOfTransactions, String lang) {
        this.msisdn = msisdn;
        this.numberOfTransactions = numberOfTransactions;
        this.lang = lang;
    }
    
    public PendingTransactionRequest() {
    }
    
    public String convertToJSON(PendingTransactionRequest pendingtTransactionRequest) throws JsonProcessingException {
        return new ObjectMapper().writer(new DefaultPrettyPrinter()).writeValueAsString(pendingtTransactionRequest);
    }
}
