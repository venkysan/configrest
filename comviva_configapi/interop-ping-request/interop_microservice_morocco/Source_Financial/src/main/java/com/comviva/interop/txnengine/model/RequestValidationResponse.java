package com.comviva.interop.txnengine.model;

import lombok.Getter;

@Getter
public class RequestValidationResponse extends StepResponse {

    public RequestValidationResponse(String statusCode, String entity) {
        super(statusCode, entity);
    }

}