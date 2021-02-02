package com.comviva.interop.txnengine.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class OrangeMoneyOperationDetail {

    private String creationDate;
    
    private String serviceType;
    
    private String payerChannelUserCode;
    
    private String payerMsisdn;
    
    private String payerProvider;
    
    private String payerPayId;
    
    private boolean isPayerTechnical;
    
    private String payeeChannelUserCode;
    
    private String payeeMsisdn;
    
    private String payeeProvider;
    
    private String payeePayId;
    
    private boolean isPayeeTechnical;
    
    private double amount;
    
    private String currency;
    
    private String txnId;
    
    private String txnStatus;
    
    private String txnMode;
    
    private String txnDate;
    
    private String addonStatus;
    
    private String optional;    
}
