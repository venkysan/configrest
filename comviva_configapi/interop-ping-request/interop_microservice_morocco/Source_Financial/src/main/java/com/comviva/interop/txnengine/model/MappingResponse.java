package com.comviva.interop.txnengine.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@XmlRootElement(name = "mapping_response")
@XmlAccessorType(XmlAccessType.FIELD)
public class MappingResponse {

    @XmlElement(name = "mapping_code")
    private String mappingCode;

}