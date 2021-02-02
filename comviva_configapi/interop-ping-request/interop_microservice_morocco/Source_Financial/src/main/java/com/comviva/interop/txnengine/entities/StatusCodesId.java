package com.comviva.interop.txnengine.entities;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StatusCodesId implements Serializable {
    private static final long serialVersionUID = -2374148590321956289L;

    private String statuscode;
    private String entity;
    private String langCode;

}