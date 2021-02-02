package com.comviva.interop.txnengine.enums;

public enum TransactionStatus {

    TRANSACTION_IN_PROGRESS("TIP", "Transaction In Progress"), //
    TRANSACTION_SUCCESS("TS","Transaction Success"), //
    TRANSACTION_FAIL("TF","Transaction Fail"),//
    TRANSACTION_AMBIGUOUS("TA", "Transaction Ambiguous"),//
    TRANSACTION_SUCCESS_AT_MOBIQUITY("TSM","Transaction Success At Mobiquity"), //
    TRANSACTION_AMBIGUOUS_AT_HPS("TAHPS", "Transaction Ambiguous At HPS"),//
    TRANSACTION_FAIL_AT_HPS("TFHPS", "Transaction Ambiguous At HPS"),//
    TRANSACTION_CORRECTION_INITIATED("TCI", "Transaction Correction Initated"),//
    TRANSACTION_CORRECTION_AMBIGUOUS("TCA", "Transaction Correction Ambiguous"),//
    TRANSACTION_INITIATED("TI", "Transaction Initiated"), //
    TRANSACTION_REJECTED("TR", "Transaction Rejected"), //
    ;

    private final String status;
    private final String description;

    private TransactionStatus(String status, String description) {
        this.status = status;
        this.description = description;
    }

    public static TransactionStatus getStatus(String status) {
        TransactionStatus[] transactionStatusus = values();
        for (TransactionStatus transactionStatus : transactionStatusus) {
            if (status.equals(transactionStatus.status)) {
                return transactionStatus;
            }
        }
        return null;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

}