package com.comviva.interop.txnengine.util;

public enum MobiquityConst {

    SEPARATOR("?"), //
    EQUAL_OPERATOR("="), //
    AND_OPERATOR("&"), //
    AMOUNT("amount"), //
    SERVICE_TYPE("serviceType"), //
    PAYER_USER_TYPE("payerUserType"), //
    PAYER_ACCOUNT_ID("payerAccountId"), //
    PAYER_PROVIDER_ID("payerProviderId"), //
    PAYER_PAY_ID("payerPayId"), //
    PAYEE_USER_TYPE("payeeUserType"), //
    PAYEE_ACCOUNT_ID("payeeAccountId"), //
    PAYEE_PROVIDER_ID("payeeProviderId"), //
    PAYEE_PAY_ID("payeePayId"), //
    SERVICE_TYPE_SUBSCRIBER("CTMREQ"), //
    SERVICE_TYPE_RETAILER("RCOREQ"), //
    MSISDN("msisdn"), //
    MSISDN2("msisdn2"), //
    PIN("pin"), //
    PROVIDER("provider"), //
    PROVIDER2("provider2"), //
    PAYID("payId"), //
    PAYID2("payId2"), //
    TXNID("txnId"), //
    STATUS("status"), //
    USER_TYPE("userType"), //
    USER_TYPE2("userType2"), //
    REMARKS("remarks"), //
    SCREVERSAL("screversal"), //
    ACTION("action"), //
    USER_ID("userId"), //
    LOGIN_ID("loginId"), //
    PASSWORD("password"),//
    SESSION_ID("session_id"),//
    TXNMODE("txnMode"),//
    CONNECTOR("connector"),//
    BLOCKSMS("blocksms"),//
    MERCHANT_CODE("mercode"), //
    PIN2("pin2"), //
    EM("em"), //
    ;

    private String value;

    MobiquityConst(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
