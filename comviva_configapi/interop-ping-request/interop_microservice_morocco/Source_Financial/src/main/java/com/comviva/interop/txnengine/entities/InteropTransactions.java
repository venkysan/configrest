package com.comviva.interop.txnengine.entities;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "INTEROP_TRANSACTIONS")
@Setter
@Getter
public class InteropTransactions {

    @Id
    @Column(name = "INTEROP_TXN_ID")
    private String interopTxnId;

    @Column(name = "TRANSACTION_TYPE")
    private String transactionType;

    @Column(name = "AMOUNT")
    private BigDecimal amount;

    @Column(name = "CURRENCY")
    private String currency;

    @Column(name = "REQUEST_SOURCE")
    private String requestSource;

    @Column(name = "EXT_ORG_REF_ID")
    private String extOrgRefId;

    @Column(name = "TXN_STATUS")
    private String txnStatus;

    @Column(name = "PAYER_MSISDN")
    private String payerMsisdn;

    @Column(name = "PAYEE_MSISDN")
    private String payeeMsisdn;

    @Column(name = "CREATED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Column(name = "UPDATED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDate;
    
    @Column(name = "TRANSACTION_SUBTYPE")
    private String transactionSubType;
    
    @Column(name = "SYSTEM_TRACE_AUDIT_NUMBER")
    private String systemTraceAuditNumber;

    @Column(name = "AUTHORIZATION_CODE")
    private String authorizationCode;

    @Column(name = "RETRIEVAL_REFERENCE_NUMBER")
    private String retrievalReferenceNumber;
    
    @Column(name = "PROCESSING_CODE")
    private String processingCode;
    
    @OneToMany(mappedBy = "interopTransactions", cascade = CascadeType.ALL)
    private Set<InteropTransactionDetails> interopTransactionDetails;    
    
    @Column(name = "MOBIQUITY_TXN_ID")
    private String mobiquityTransactionId;
    
    @Column(name = "TXN_CORRECTION_ID")
    private String txnCorrectionId;
}