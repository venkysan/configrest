package com.comviva.interop.txnengine.enums;

public enum Constants {

    P2P_PROCESSING_CODE("230000"),
    P2P_PROCESSING_CODE_ACCOUNT_TYPE1("238000"),//
    P2P_PROCESSING_CODE_ACCOUNT_TYPE2("238100"),
    P2P_PROCESSING_CODE_ACCOUNT_TYPE3("238200"),
    SERVICE_POINT_DATA_CODE("000001A00014"),//
    MESSAGE_REASON_CODE("0000"),//
    FUNCTION_CODE("200"),
    NETWORK_FUNCTION_CODE("802"),
    ZERO("0"), //
    ONE("1"), //
    MP_PULL_PROCESSING_CODE("240000"),
    MP_PROCESSING_CODE("000000"),
    ; //

    private String value;

    Constants(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
