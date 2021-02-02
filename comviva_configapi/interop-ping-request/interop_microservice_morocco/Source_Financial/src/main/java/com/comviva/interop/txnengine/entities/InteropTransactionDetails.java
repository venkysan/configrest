package com.comviva.interop.txnengine.entities;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "INTEROP_TRANSACTION_DETAILS")
@Setter
@Getter

public class InteropTransactionDetails {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "id_seq")
    private Long id;

    @Column(name = "PAYER_PAY_ID")
    private String payerPayId;

    @Column(name = "PAYEE_PAY_ID")
    private String payeePayId;

    @Column(name = "THIRD_PARTY_REF_ID")
    private String thirdPartyRefId;

    @Column(name = "THIRD_PARTY_TXN_TYPE")
    private String thirdPartyTxnType;

    @Column(name = "THIRD_PARTY_REQ_TYPE")
    private String thirdPartyReqType;

    @Column(name = "THIRD_PARTY_PAYER")
    private String thirdPartyPayer;

    @Column(name = "THIRD_PARTY_PAYEE")
    private String thirdPartyPayee;

    @Column(name = "AMOUNT")
    private BigDecimal amount;

    @Column(name = "CURRENCY")
    private String currency;

    @Column(name = "TXN_STATUS")
    private String txnStatus;

    @Column(name = "THIRD_PARTY_RESPONSE_CODE")
    private String thirdPartyReponseCode;

    @Column(name = "THIRD_PARTY_MAPPING_CODE")
    private String thirdPartyMappingCode;

    @Column(name = "THIRD_PARTY_RESPONSE_MESSAGE")
    private String thirdPartyResponseMessage;

    @Column(name = "TXN_MODE")
    private String txnMode;

    @Column(name = "REMARKS")
    private String remarks;

    @Column(name = "CREATED_DATE")
    private Date createdDate;

    @Column(name = "UPDATED_DATE")
    private Date updatedDate;
    
    @ManyToOne
    @JoinColumn(name = "INTEROP_TXN_ID")
    private InteropTransactions interopTransactions;
    
    @Column(name = "PAYER_CHANNEL_USER_CODE")
    private String payerChannelUserCode;
	
	@Column(name = "PAYER_PROVIDER")
    private String payerProvider;

    @Column(name = "IS_PAYER_TECHNICAL")
    private String isPayerTechnical;
	
	@Column(name = "PAYEE_CHANNEL_USER_CODE")
    private String payeeChannelUserCode;
	
	@Column(name = "PAYEE_PROVIDER")
    private String payeeProvider;

    @Column(name = "IS_PAYEE_TECHNICAL")
    private String isPayeeTechnical;

}