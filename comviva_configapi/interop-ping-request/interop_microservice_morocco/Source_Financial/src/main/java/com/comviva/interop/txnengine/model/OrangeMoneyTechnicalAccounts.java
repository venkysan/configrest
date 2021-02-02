package com.comviva.interop.txnengine.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class OrangeMoneyTechnicalAccounts {

    private String metadata;
    
    private List<OrangeMoneyTechnicalAccountDetail>  data;
}
