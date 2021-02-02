package com.comviva.interop.txnengine.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TransactionCorrectionResponse {

	 private String interopTxnId;
	 
	 private String mappedCode;
	 
	 private String transactionId;

	 private String message;

	 private String code;
	 
	 public String convertToJSON(TransactionCorrectionResponse transactionCorrectionResponse) throws JsonProcessingException {
	        return new ObjectMapper().writer(new DefaultPrettyPrinter()).writeValueAsString(transactionCorrectionResponse);
	 }
}
