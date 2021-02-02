package com.comviva.interop.txnengine.enums;

public enum ISOFields {

    PAN("pan"), //
    PROCESSING_CODE("processingCode"), //
    TRANSACTION_AMOUNT("transactionAmount"), //
    CONSOLIDATION_AMOUNT("consolidationAmount"), //
    CARD_HOLDER_BILL_AMOUNT("cardholderBillAmount"), //
    DATE_AND_TIME_OF_TRANSMISSION("dateAndTimeOfTransmission"), //
    CARD_HOLDER_BILLING_EXCHANGE_RATE("cardholderBillingExchangeRate"), //
    SYSTEM_AUDIT_NUMBER("systemAuditNumber"), //
    DATE_AND_TIME_OF_TXN("dateAndTimeOfTheTransaction"), //
    SETTLEMENT_DATE("settlementDate"), //
    EXCHANGE_DATE("exchangeDate"), //
    BUSINNES_TYPE("businessType"), //
    COUNTRY_CODE_OF_THE_ACQUIRING_ORG("countryCodeOfTheAcquiringOrganization"), //
    COUNTRY_CODE_OF_THE_SENDER_ORG("countryCodeOftheSenderOrganization"), //
    SERVICE_POINT_DATA_CODE("servicePointDataCode"), //
    FUNCTION_CODE("functionCode"), //
    IDENTIFICATION_CODE_OF_THE_ACQUIRING_ORG("identificationCodeOfTheAcquiringOrganization"), //
    IDENTIFICATION_CODE_OF_THE_SENDING_ORG("identificationCodeOfTheSendingOrganization"), //
    REFERENCE_NUMBER_OF_THE_RECOVERY("referenceNumberOfTheRecovery"), //
    PRIVATE_ADDITIONAL_DATA("privateAdditionalData"),
    SECURITY_CHECK_INFO("securityCheckInfo"),
    CURRENCY_CODE_OF_THE_TXN("currencyCodeOfTheTransaction"), //
    CURRENCY_CODE_OF_THE_CONSOLIDATION("currencyCodeOfTheConsolidation"), //
    CURRENCY_CODE_OF_THE_CARD_HOLDER_INVOICE("currencyCodeOfTheCardholderInvoice"), //
    SECURITY_CHECK_INFORMATION("securityCheckInformation"),
    SOURCE_ACCOUNT_NUMBER("sourceAccountNumber"), //
    DESTINATION_ACCOUNT_NUMBER("destinationAccountNumber"), //
    CARD_ACCEPTER_TERMINAL_ID("cardAccepterTerminalId"),    //
    IDENTIFICATION_CODE_OF_CARD_ACCEPTOR("identificationCodeOfCardAcceptor"),   //
    NAME_AND_ADDRESS_OF_CARD_ACCEPTOR("nameAndAddressOfCardAcceptor"),  //
    FINANCIAL_TRANSACTION("FINANCIAL_TRANSACTION"),//
    FINANCIAL_TRANSACTION_MP_PUSH("FINANCIAL_TRANSACTION_MP_PUSH"),//
    FINANCIAL_TRANSACTION_MP_PULL("FINANCIAL_TRANSACTION_MP_PULL"),//
    SERVICE_ENTRY_MODE("serviceEntryMode"),
    SOURCE_ACCOUNT_TYPE("sourceAccountType"),
    NETWORK_ACTION("networkAction"),
    NETWORK_MESSAGE_SERVICE_TYPE("NETWORK_MESSAGE"),
    MESSAGE_REASON_CODE("messageReasonCode")
    ;
    private String value;
    
    private ISOFields(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
}
