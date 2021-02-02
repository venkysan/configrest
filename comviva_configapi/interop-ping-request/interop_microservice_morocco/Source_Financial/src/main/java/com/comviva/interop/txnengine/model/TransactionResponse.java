package com.comviva.interop.txnengine.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Generated;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * TransactionResponse
 */
@Setter
@Getter
@ToString
@Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2019-03-05T10:52:25.503+05:30")
public class TransactionResponse {

    private String extOrgRefId = null;

    private String interopRefId = null;

    private Date transactionSubmitTime = null;

    private String transactionType = null;

    private List<AccountIdentifierBase> debitParty = new ArrayList<AccountIdentifierBase>();

    private List<AccountIdentifierBase> creditParty = new ArrayList<AccountIdentifierBase>();

    private String status = null;

    private String message = null;

    private String code = null;

    private String mappedCode = null;

    private String href = null;

    public TransactionResponse(String message, String code, String mappedCode) {
        super();
        this.message = message;
        this.code = code;
        this.mappedCode = mappedCode;
    }

    public TransactionResponse() {
    }
    
    public String convertToJSON(TransactionResponse transactionResponse) throws JsonProcessingException {
        return new ObjectMapper().writer(new DefaultPrettyPrinter()).writeValueAsString(transactionResponse);
    }
}