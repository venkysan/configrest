package com.comviva.interop.txnengine.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PendingTransactionsResponse {

	  private String mappedCode = null;

	  private String code = null;

	  private String message = null;

	  private List<PendingTransactions> data = new ArrayList<>();	  
	  
	  public PendingTransactionsResponse() {
	  }
	  
	  public PendingTransactionsResponse(String message, String code, String mappedCode) {
	     this.message = message;
	     this.code = code;
	     this.mappedCode = mappedCode;
	  }
	  
	  public String convertToJSON(PendingTransactionsResponse pendingTransactionsResponse) throws JsonProcessingException {
	        return new ObjectMapper().writer(new DefaultPrettyPrinter()).writeValueAsString(pendingTransactionsResponse);
	  }
}
