package com.comviva.interop.txnengine.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class OrangeMoneyTechnicalAccountDetail {

    private String creationDate;
    
    private String type;
    
    private String description;
    
    private String msisdn;
    
    private String channelUserCode;
    
    private String optional;    
}
