package com.comviva.interop.txnengine.enums;

public enum ConfirmTransactionActionTypes {
	
    ACCEPT("accept"), //
    REJECT("reject"),//
    
    ; //
    
    private String actionType;
    
    ConfirmTransactionActionTypes(String actionType){
        this.actionType = actionType;
    }
    
    public String getActionType() {
        return actionType;
    }

}
