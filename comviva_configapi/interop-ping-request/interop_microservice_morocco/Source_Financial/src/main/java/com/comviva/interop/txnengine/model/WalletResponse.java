package com.comviva.interop.txnengine.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@XmlRootElement(name = "wallet_response")
@XmlAccessorType(XmlAccessType.FIELD)
public class WalletResponse {

    @XmlElement(name = "type")
    private String type;
    @XmlElement(name = "service_type")
    private String serviceType;
    @XmlElement(name = "txnid")
    private String txnid;
    @XmlElement(name = "txnstatus")
    private String txnstatus;
    @XmlElement(name = "trid")
    private String trid;
    @XmlElement(name = "fees_payer_paid")
    private String feesPayerPaid;
    @XmlElement(name = "fees_payee_paid")
    private String feesPayeePaid;
    @XmlElement(name = "comm_payer_paid")
    private String commPayerPaid;
    @XmlElement(name = "comm_payer_rec")
    private String commPayerRec;
    @XmlElement(name = "comm_payee_paid")
    private String commPayeePaid;
    @XmlElement(name = "comm_payee_rec")
    private String commPayeeRec;
    @XmlElement(name = "tax_payer_paid")
    private String taxPayerPaid;
    @XmlElement(name = "tax_payee_paid")
    private String taxPayeePaid;
    @XmlElement(name = "message")
    private String message;
    @XmlElement(name = "txnmode")
    private String txnmode;
    @XmlElement(name = "msisdn")
    private String msisdn;
    @XmlElement(name = "lang")
    private String lang;
    @XmlElement(name = "reqstatus")
    private String reqStatus;
    @XmlElement(name = "userid")
    private String userId;
    @XmlElement(name = "userstatus")
    private String userStatus;
    @XmlElement(name = "barredtype")
    private String barredType;
    @XmlElement(name = "token")
    private String token;
    @XmlElement(name = "frozenbal")
    private String frozenbal;
    @XmlElement(name = "domain")
    private String domain;
    @XmlElement(name = "category")
    private String category;
    @XmlElement(name = "fname")
    private String fname;
    @XmlElement(name = "lname")
    private String lname;
    @XmlElement(name = "idno")
    private String idno;
    @XmlElement(name = "balance")
    private String balance;
    @XmlElement(name = "frbalance")
    private String frbalance;
    @XmlElement(name = "dob")
    private String dob;
    @XmlElement(name = "suspendstatus")
    private String suspendstatus;
    @XmlElement(name = "barred")
    private String barred;
    @XmlElement(name = "nooftxn")
    private String nooftxn;
    @XmlElement(name = "noofdata")
    private String noofdata;

    @XmlElement
    private List<WalletData> data;

}