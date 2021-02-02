package com.comviva.interop.txnengine.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PendingTransactions {

	  private String msisdn;

	  private String interopRefId;

	  private String txnAmount = null;

	  private String txnDate = null;
	  
	  private StatusEnum status = null;

	  private String from = null;

	  private String href = null;
	  
	  private ServiceTypeEnum serviceType = null;

	  /**
	   * Service type like merchant payment
	   */
	  public enum ServiceTypeEnum {
	    MERCHANTPAYMENT("merchantPayment");

	    private String value;

	    ServiceTypeEnum(String value) {
	      this.value = value;
	    }

	    public String getValue() {
	      return value;
	    }

	    @Override
	    public String toString() {
	      return String.valueOf(value);
	    }

	    public static ServiceTypeEnum fromValue(String text) {
	      for (ServiceTypeEnum b : ServiceTypeEnum.values()) {
	        if (String.valueOf(b.value).equals(text)) {
	          return b;
	        }
	      }
	      return null;
	    }
	  }

	 

	  /**
	   * status of transaction like merchant payment initiated
	   */
	  public enum StatusEnum {
	    TI("TI");

	    private String value;

	    StatusEnum(String value) {
	      this.value = value;
	    }

	    public String getValue() {
	      return value;
	    }

	    @Override
	    public String toString() {
	      return String.valueOf(value);
	    }

	    public static StatusEnum fromValue(String text) {
	      for (StatusEnum b : StatusEnum.values()) {
	        if (String.valueOf(b.value).equals(text)) {
	          return b;
	        }
	      }
	      return null;
	    }

	  }
}
