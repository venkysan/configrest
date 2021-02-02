package com.comviva.interop.txnengine.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StepResponse {

    private String statusCode;
    private String entity;

    public StepResponse(String statusCode, String entity) {
        super();
        this.statusCode = statusCode;
        this.entity = entity;
    }

}