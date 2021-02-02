package com.comviva.interop.txnengine.model;

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
@XmlRootElement(name = "WALLETTYPE")
@XmlAccessorType(XmlAccessType.FIELD)
public class WalletType {

    @XmlElement(name = "WTNAME")
    private String wtName;
    @XmlElement(name = "WID")
    private String wId;
    @XmlElement(name = "GRADE")
    private String grade;

}