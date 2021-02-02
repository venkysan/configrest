package com.comviva.interop.txnengine.enums;

public enum ServiceTypes {
    CREATE_TRANSACTION(ServiceCategories.FINANCIAL),//
    RECEIVE_TRANSACTION(ServiceCategories.FINANCIAL),//
    GET_TRANSACTION_STATUS(ServiceCategories.FINANCIAL),//
    GET_TRANSACTIONS(ServiceCategories.FINANCIAL),//
    TRANSACTION_QUOTATION(ServiceCategories.FINANCIAL), //
    PENDING_TRANSACTIONS(ServiceCategories.FINANCIAL), //
    ACTION_ON_TRANSACTION_CONFIRMATION(ServiceCategories.FINANCIAL), //
    NETWORK_MESSAGE(ServiceCategories.FINANCIAL), //
    ;

    private final ServiceCategories serviceCategory;

    private ServiceTypes(ServiceCategories serviceCategory) {
        this.serviceCategory = serviceCategory;
    }

    public ServiceCategories getServiceCategory() {
        return this.serviceCategory;
    }

}