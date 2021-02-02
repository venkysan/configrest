package com.comviva.interop.txnengine.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfirmTransactionResponse {

	  private String interopRefId = null;

	  private Date transactionSubmitTime = null;
	  
	  private TransactionTypeEnum transactionType = null;

	  private List<AccountIdentifierBase> debitParty = new ArrayList<>();

	  private List<AccountIdentifierBase> creditParty = new ArrayList<>();

	  private String status = null;

	  private String txnId = null;

	  private String txnMode = null;

	  private String mappedCode = null;

	  private String code = null;

	  private String message = null;

	  private String href = null;
	  
	  public ConfirmTransactionResponse() {
	  }
	  
	  public ConfirmTransactionResponse(String message, String code, String mappedCode) {
		     this.message = message;
		     this.code = code;
		     this.mappedCode = mappedCode;
	 }

	  /**
	   * Type of transaction like p2p, merchantPayment
	   */
	  public enum TransactionTypeEnum {
	    P2P("p2p"),
	    
	    MERCHANTPAYMENT("merchantPayment");

	    private String value;

	    TransactionTypeEnum(String value) {
	      this.value = value;
	    }

	    public String getValue() {
	      return value;
	    }

	    @Override
	    public String toString() {
	      return String.valueOf(value);
	    }

	    public static TransactionTypeEnum fromValue(String text) {
	      for (TransactionTypeEnum b : TransactionTypeEnum.values()) {
	        if (String.valueOf(b.value).equals(text)) {
	          return b;
	        }
	      }
	      return null;
	    }

	  }
	  
	  public String convertToJSON(ConfirmTransactionResponse confirmTransactionResponse) throws JsonProcessingException {
	        return new ObjectMapper().writer(new DefaultPrettyPrinter()).writeValueAsString(confirmTransactionResponse);
	  }
}
