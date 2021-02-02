package com.comviva.interop.txnengine.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class TransactionData {

    private String status = null;
    private List<AccountIdentifierBase> debitParty = new ArrayList<>();
    private List<AccountIdentifierBase> creditParty = new ArrayList<>();
    private String currency = null;
    private String transactionType = null;
    private String amount = null;
    private String extOrgRefId = null;
    private String requestSource = null;
    private String interopRefId = null;
    private Date transactionSubmitTime = null;

}