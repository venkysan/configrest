package com.comviva.interop.txnengine.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class TxnCorrection {

    private String msisdn;
    private String msisdn2;
    private String amount;
    private String txnId;
    private String userType;
    private String userType2;
    private String remarks;
    private String scReversal;
    private String action;
    private String userId;
    private String provider;
    private String payId;
}
