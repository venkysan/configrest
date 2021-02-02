package com.comviva.interop.txnengine.enums;

public enum ThirdPartyResponseCodes {

    SUCCESS("SUCCESS", Sources.BROKER),//
    USER_INVALID("USER_INVALID", Sources.BROKER),//
    EIG_REQUEST_TIMEOUT("TIMEOUT_SWITCH", Sources.INTEROP), //
    EIG_GENERAL_ERROR("ERRORDEFAULT_INTERNAL", Sources.INTEROP),//
    HPS_SUCCESS("SUCCESS", Sources.HPS), //
    ;

    private final String mappedCode;
    private final Sources entity;

    private ThirdPartyResponseCodes(String mappedCode, Sources entity) {
        this.mappedCode = mappedCode;
        this.entity = entity;
    }

    public String getMappedCode() {
        return this.mappedCode;
    }

    public Sources getEntity() {
        return this.entity;
    }
}
