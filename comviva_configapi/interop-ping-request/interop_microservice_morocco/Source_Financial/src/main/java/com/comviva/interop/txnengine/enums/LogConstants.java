package com.comviva.interop.txnengine.enums;

public enum LogConstants {
    
    HPS_REFERENCE_NUMBER("HPS_reference_number"), //
    DEFAULT_LANGUAGE_CODE("fr"), //
    DEFAULT_COUNTRY_CODE("IN"), //
    DEFAULT_WALLET_ID("12"), //
    TXN_TYPE_TANGO("TANGO"), //
    TXN_TYPE_PARTENER("PARTENER"), //
    INCOMING_REQUEST_EVENT_TYPE("incoming request"), //
    OUTGOING_RESPONSE_EVENT_TYPE("outgoing response"), //
    OUTGOING_REQUEST_EVENT_TYPE("outgoing request"), //
    INCOMING_RESPONSE_EVENT_TYPE("incoming response"), //
    INFO("INFO"), //
    GET_FEE("GET_FEE"), //
    GET_LANG("GET_LANG"), //
    P2P("P2P"), //
    OM_OPERATIONS("OM_OPERATIONS"), //
    TECHNICAL_WALLETS("TECHNICAL_WALLETS"), //
    EMPTY_STRING(""), //
    CHANNEL_USER_BY_MSISDN_EVENT("channel user details by msisdn"), //
    HTTP_CLIENT_CONFIGURATION_EVENT("HTTP client configuration"), //
    LOADING_LANGUAGE_CODES_EVENT("Loading Language Codes"), //
    LOADING_LANGUAGE_CODES_START_USE_CASE("Loading language codes from database initiated"), //
    LOADING_LANGUAGE_CODES_COMPLETED_USE_CASE("Loading language codes from database completed"), //
    LOADING_STATUS_CODES_EVENT("Loading Status Codes"), //
    LOADING_STATUS_CODES_COMPLETED_USE_CASE("Loading status codes from database completed"), //
    LOADING_STATUS_CODES_START_USE_CASE("Loading status codes from database initiated"), //
    IDLE_CONNECTION_CLOSING_EVENT("closing idle connections"), //
    IDLE_CONNECTION_NOT_INITIALIZATION_EVENT("Http Client Connection manager is not initialised"),
    SMS_NOTIFICATION("SMS Notification"), //
    CASTING_FAILED_EVENT("Failed while casting string to map"),//
    VELOCITY_TEMPLATE_ENGINE_INITIATION_FAILED_EVENT("Velocity template engine initiation failed"),//
    STRING_TO_DATE_PARSE_EXCEPTION("String to date parse exception"), //
    C2C("C2C"), //
    CASHOUT_INIT("CASH_OUT_INIT"), //
    CASHOUT_CONFIRM("CASH_OUT_CONFIRM"), //
    MERCHANT_PAYMENT_INIT("MERCHANT_PAYMENT_INIT"), //
    MERCHANT_PAYMENT_CONFIRM("MERCHANT_PAYMENT_CONFIRM"), //
    P2P_CASH_IN("P2P_CASH_IN"), //
    TXN_CORRECTION_INIT("TXN_CORRECTION_INIT"), //
    TXN_CORRECTION_APPORVE("TXN_CORRECTION_APPROVE"), //
    USER_AUTHENTICATION("USER_AUTHENTICATION"), //
    USER_ENQUIRY("USER_ENQUIRY"), //
    GET_RETAILER_LANG("GET_RETAILER_LANG"), //
    HPS_TRANSACTION("HPS_TRANSACTION"), //
    INTERNAL_ERROR("INTERNAL_ERROR"), //
    TXN_CORRECTION("TXN_CORRECTION"), //
    MERCHANT_PAYMENT("MERCHANT_PAYMENT"), //
    HPS_NETWORK_MESSAGE("HPS_NETWORK_MESSAGE"), //
    ;
    
    private String value;

    LogConstants(String value) {
      this.value = value;
    }
    
    public String getValue() {
    	return this.value;
    }
}
