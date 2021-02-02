package com.comviva.interop.txnengine.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Request {

    private String interopReferenceId;
    private RequestWrapper requestAttr;

    public Request(RequestWrapper requestAttr) {
        this.requestAttr = requestAttr;
    }

}