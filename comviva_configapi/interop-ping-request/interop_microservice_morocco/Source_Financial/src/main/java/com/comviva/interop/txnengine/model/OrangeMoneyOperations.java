package com.comviva.interop.txnengine.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class OrangeMoneyOperations {

    private String metadata;
    
    private List<OrangeMoneyOperationDetail>  data;
}
