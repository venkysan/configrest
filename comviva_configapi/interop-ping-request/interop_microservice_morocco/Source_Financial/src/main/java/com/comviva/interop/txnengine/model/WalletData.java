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
@XmlRootElement(name = "data")
@XmlAccessorType(XmlAccessType.FIELD)
public class WalletData {

    @XmlElement
    private List<WalletType> walletType;

    @XmlElement(name = "TXN_ID")
    private List<String> txnId;
    @XmlElement(name = "TXNAMT")
    private List<String> txnAmt;
    @XmlElement(name = "FROM")
    private List<String> from;
    @XmlElement(name = "TXNDT")
    private List<String> txnDt;
    @XmlElement(name = "SERVICE")
    private List<String> service;
    @XmlElement(name = "FROMTO")
    private List<String> fromTo;
    @XmlElement(name = "TXNTYPE")
    private List<String> txnType;
    @XmlElement(name = "TXN_STATUS")
    private List<String> txnStatus;
    @XmlElement(name = "PAYID")
    private List<String> payId;
    @XmlElement(name = "TXNMODE")
    private List<String> txnMode;

}