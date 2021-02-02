package com.comviva.interop.txnengine.enums;

public enum ErrorStatus {
    VELOCITY_RESOURCE_NOT_FOUND_EXCEPTION("9004", Sources.INTEROP), //
    VELOCITY_PARSE_ERROR_EXCEPTION("9005", Sources.INTEROP), //
    VELOCITY_METHOD_INVOCATION_EXCEPTION("9006", Sources.INTEROP),//
    ;

    private final String statusCode;
    private final Sources entity;

    private ErrorStatus(String statusCode, Sources entity) {
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
