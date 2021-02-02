package com.comviva.interop.txnengine.enums;

public enum InteropResponseCodes {

    SUCCESS("00000", Sources.INTEROP),//
    INTERNAL_ERROR("50001", Sources.INTEROP),//
    NO_RECORDS_FOUND("50016", Sources.INTEROP), //
    FAILURE_RESPONSE_FROM_BROKER("50024", Sources.INTEROP), //
    NOT_ABLE_TO_CONNECT_THIRD_PARTY("50027", Sources.INTEROP), //
    NOT_ABLE_TO_PARSE_THE_BROKER_RESPONSE("50028", Sources.INTEROP), //
    READ_TIMEOUT_FROM_BROKER("50029", Sources.BROKER), //
    CHANNEL_USER_DETAILS_NOT_FOUND("50030", Sources.INTEROP), //
    HPS_TXN_SUCCESS_ACTION_CODE("000", Sources.HPS), //
    HPS_TXN_FAILED_ACTION_CODE("100", Sources.HPS), //
    TXN_INITIATED_SUCCESSFULLY("50046", Sources.INTEROP), //
    HPS_TXN_NETWORK_MESSAGE_SUCCESS_ACTION_CODE("800", Sources.HPS), //
    ;

    private final String statusCode;
    private final Sources entity;

    private InteropResponseCodes(String statusCode, Sources entity) {
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
