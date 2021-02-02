package com.comviva.interop.txnengine.enums;

public enum SuccessStatus {

    SUCCEEDED("40078", Sources.INTEROP),//
    SUCCESSFUL_REQUEST_VALIDATION("2003", Sources.INTEROP), //
    ;

    private final String statusCode;
    private final Sources entity;

    private SuccessStatus(String statusCode, Sources entity) {
        this.statusCode = statusCode;
        this.entity = entity;

    }

    public String getStatusCode() {
        return this.statusCode;
    }

    public Sources getEntity() {
        return this.entity;
    }

}
