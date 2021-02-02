package com.comviva.interop.txnengine.model;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * TransactionRequest
 */
@Setter
@Getter
@ToString
@Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2019-03-05T10:52:25.503+05:30")
public class TransactionRequest implements RequestWrapper {

    private String amount = null;
    private String currency = null;
    private String transactionType = null;
    private List<AccountIdentifierBase> debitParty = new ArrayList<AccountIdentifierBase>();
    private List<AccountIdentifierBase> creditParty = new ArrayList<AccountIdentifierBase>();
    private String extOrgRefId = null;
    private String requestSource = null;
    private String lang = null;
    private DebitPartyCredentialsDetails debitPartyCredentials;
    private String sourceAccountType;
    
    public String convertToJSON(TransactionRequest transactionRequest) throws JsonProcessingException {
        return new ObjectMapper().writer(new DefaultPrettyPrinter()).writeValueAsString(transactionRequest);
    }

}