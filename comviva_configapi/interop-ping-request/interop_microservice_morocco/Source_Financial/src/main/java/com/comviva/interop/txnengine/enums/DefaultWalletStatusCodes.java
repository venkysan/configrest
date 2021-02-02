package com.comviva.interop.txnengine.enums;

public enum DefaultWalletStatusCodes {

    ENROLMENT_REG_SUCCESS_STATUS("00", "Y"),//
    NOT_REGISTERED("00","NO CLIENT IS REGISTERED TO THIS NUMBER."), //
    REGISTERED_WITH_SAME_MFS("01","THE CLIENT IS REGISTERED WITH EDP HAVING CARRIED OUT THE REQUEST"), //
    REGISTERED_WITH_OTHER_MFS("02","THE CLIENT IS REGISTERED WITH A FELLOW EDP"),//
    ;

    private String code;
    private String message;

    private DefaultWalletStatusCodes(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}
