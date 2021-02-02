package com.comviva.interop.txnengine.model;

import javax.annotation.Generated;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;

/**
 * TransactionResponse
 */
@Setter
@Getter
@Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2019-03-05T10:52:25.503+05:30")
public class ReceiveTransactionResponse extends BaseReceiveTransaction{

	private String lengthOfTheAuthorizationCode;
	private String identificationCodeOfTheAcquiringOrganization;
	private String identificationCodeOfTheSendingOrganization;
	private String referenceNumberOfTheRecovery;
	private String authorizationCode;
	private String actionCode;
	private String securityCheckInfo;
	private String currencyCodeOfTheTransaction;
	private String currencyCodeOfTheConsolidation;
	private String currencyCodeOfTheCardholderInvoice;
	private String sourceAccountNumber;
	private String destinationAccountNumber;
	
	public ReceiveTransactionResponse() {
	}
	
	public String convertToJSON(ReceiveTransactionResponse receiveTransactionResponse) throws JsonProcessingException {
        return new ObjectMapper().writer(new DefaultPrettyPrinter()).writeValueAsString(receiveTransactionResponse);
    }
}