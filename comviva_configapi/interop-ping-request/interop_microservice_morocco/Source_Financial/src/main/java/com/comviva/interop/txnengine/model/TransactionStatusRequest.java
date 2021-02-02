package com.comviva.interop.txnengine.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.ToString;


@Getter
@ToString
public class TransactionStatusRequest implements RequestWrapper {

    private String interopreferenceid;
    private String lang;

    public TransactionStatusRequest(String interopreferenceid, String lang) {
        this.interopreferenceid = interopreferenceid;
        this.lang = lang;
    }
    
    public String convertToJSON(TransactionStatusRequest transactionStatusRequest) throws JsonProcessingException {
        return new ObjectMapper().writer(new DefaultPrettyPrinter()).writeValueAsString(transactionStatusRequest);
    }
}
