package com.comviva.interop.txnengine.enums;

public enum TransactionTypes {
    P2P("p2p"), //
    MERCHPAY("merchantPayment"),//
    MERCHPAY_PULL("merchantPaymentPull"), //
    
    ; //
    
    private String transactionType;
    
    TransactionTypes(String transactionType){
        this.transactionType = transactionType;
    }
    
    public String getTransactionType() {
        return transactionType;
    }

}
