package com.comviva.interop.txnengine.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfirmTransactionRequest implements RequestWrapper {

	  private String interopRefId;

	  private String pin;
	  
	  private ActionEnum action;

	  private String lang;

	  /**
	   * action on the mp pull transaction
	   */
	  public enum ActionEnum {
	    accept("accept"),
	    
	    reject("reject");

	    private String value;

	    ActionEnum(String value) {
	      this.value = value;
	    }

	    public String getValue() {
	      return value;
	    }

	    @Override
	    public String toString() {
	      return String.valueOf(value);
	    }

	    public static ActionEnum fromValue(String text) {
	      for (ActionEnum b : ActionEnum.values()) {
	        if (String.valueOf(b.value).equals(text)) {
	          return b;
	        }
	      }
	      return null;
	    }
	  }
	  
	  public String convertToJSON(ConfirmTransactionRequest transactionRequest) throws JsonProcessingException {
	        return new ObjectMapper().writer(new DefaultPrettyPrinter()).writeValueAsString(transactionRequest);
	  }
}
