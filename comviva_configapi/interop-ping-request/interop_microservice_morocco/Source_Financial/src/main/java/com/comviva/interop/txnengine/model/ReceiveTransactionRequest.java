package com.comviva.interop.txnengine.model;

import javax.annotation.Generated;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;

/**
 * TransactionRequest
 */
@Setter
@Getter
@Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2019-03-05T10:52:25.503+05:30")
public class ReceiveTransactionRequest extends BaseReceiveTransaction implements RequestWrapper {

	private String identificationCodeOfTheAcquiringOrganization;
	private String identificationCodeOfTheSendingOrganization;
	private String referenceNumberOfTheRecovery;
	private String currencyCodeOfTheTransaction;
	private String currencyCodeOfTheConsolidation;
	private String currencyCodeOfTheCardholderInvoice;
	private String sourceAccountNumber;
	private String destinationAccountNumber;
	private String securityCheckInfo;
	
	public String convertToJSON(ReceiveTransactionRequest receiveTransactionRequest) throws JsonProcessingException {
        return new ObjectMapper().writer(new DefaultPrettyPrinter()).writeValueAsString(receiveTransactionRequest);
    }
}