package com.comviva.interop.txnengine.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@XmlRootElement(name = "broker_response")
@XmlAccessorType(XmlAccessType.FIELD)
public class BrokerResponse {

    @XmlElement(name = "broker_code")
    private String brokerCode;
    @XmlElement(name = "broker_msg")
    private String brokerMsg;
    @XmlElement(name = "call_wallet_id")
    private String callWalletId;
    @XmlElement(name = "session_id")
    private String sessionId;

}