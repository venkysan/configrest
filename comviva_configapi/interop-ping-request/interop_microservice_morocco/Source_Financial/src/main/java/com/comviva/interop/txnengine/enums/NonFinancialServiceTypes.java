package com.comviva.interop.txnengine.enums;

public enum NonFinancialServiceTypes {
	
    GET_DEFAULT_WALLET_STATUS("GET_DEFAULT_WALLET_STATUS"),
    GET_FEE("GET_FEE"),
    GET_SUBSCRIBER_LANG("GET_SUBSCRIBER_LANG"),
    GET_RETAILER_LANG("GET_RETAILER_LANG"),
    USER_ENQUIRY("USER_ENQUIRY"),
    USER_AUTHENTICATION("USER_AUTHENTICATION"),
    ;
	
	private String serviceType;
    
	NonFinancialServiceTypes(String serviceType){
        this.serviceType = serviceType;
    }
    
    public String getServiceType() {
        return serviceType;
    }
}