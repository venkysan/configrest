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
@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
public class Response {

    @XmlElement(name = "broker_response")
    private BrokerResponse brokerResponse;

    @XmlElement(name = "mapping_response")
    private MappingResponse mappingResponse;

    @XmlElement(name = "wallet_response")
    private WalletResponse walletResponse;

}