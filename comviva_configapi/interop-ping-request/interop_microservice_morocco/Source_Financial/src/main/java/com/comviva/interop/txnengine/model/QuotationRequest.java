package com.comviva.interop.txnengine.model;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;

/**
 * QuotationRequest
 */
@Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2019-03-05T10:52:25.503+05:30")
@Setter
@Getter
public class QuotationRequest implements RequestWrapper {

    private String amount = null;
    private String currency = null;
    private String transactionType = null;
    private List<AccountIdentifierBase> debitParty = new ArrayList<AccountIdentifierBase>();
    private List<AccountIdentifierBase> creditParty = new ArrayList<AccountIdentifierBase>();
    private String lang = null;
    private String requestSource = null;
    private String extOrgRefId = null;

    public QuotationRequest() {
    }

    public String convertToJSON(QuotationRequest quotationRequest) throws JsonProcessingException {
        return new ObjectMapper().writer(new DefaultPrettyPrinter()).writeValueAsString(quotationRequest);
    }
}