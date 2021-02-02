package com.comviva.interop.txnengine.model;

import javax.annotation.Generated;

import org.joda.time.DateTime;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * QuotationResponse
 */
@Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2019-03-05T10:52:25.503+05:30")
@Setter
@Getter
@ToString
public class QuotationResponse {

    private String extOrgRefId = null;
    private String interopRefId = null;
    private DateTime transactionSubmitTime = null;
    private String feesPayerPaid = null;
    private String feesPayeePaid = null;
    private String commissionPayerPaid = null;
    private String commissionPayerReceived = null;
    private String commissionPayeePaid = null;
    private String commissionPayeeReceived = null;
    private String taxPayerPaid = null;
    private String taxPayeePaid = null;
    private String message = null;
    private String code = null;
    private String mappedCode = null;

    public QuotationResponse(String message, String code, String mappedCode) {
        super();
        this.message = message;
        this.code = code;
        this.mappedCode = mappedCode;
    }

    public QuotationResponse() {
    }
    
    public String convertToJSON(QuotationResponse quotationResponse) throws JsonProcessingException {
        return new ObjectMapper().writer(new DefaultPrettyPrinter()).writeValueAsString(quotationResponse);
    }

}