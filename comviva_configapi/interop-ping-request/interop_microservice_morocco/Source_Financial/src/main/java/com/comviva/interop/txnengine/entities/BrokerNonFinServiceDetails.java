package com.comviva.interop.txnengine.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "BROKER_NON_FIN_SERVICES")
@Setter
@Getter

public class BrokerNonFinServiceDetails {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "broker_non_fin_service_seqid")
    private Long id;
    
	 @Column(name = "SERVICE_TYPE")
	 private String serviceType;
	 
	 @Column(name = "INTEROP_TXN_ID")
	 private String interopTxnId;

	 @Column(name = "PAYEE_MSISDN")
	 private String payeeMsisdn;
	 
	 @Column(name = "PAYER_MSISDN")
	 private String payerMsisdn;

	 @Column(name = "MAPPING_CODE")
	 private String mappingCode;

	 @Column(name = "RESPONSE_MESSAGE")
	 private String responseMessage;
	 
	 @Column(name = "RESPONSE_CODE")
	 private String responseCode;

	 @Column(name = "UPDATED_DATE")
	 private Date updatedDate;
	 
	 @Column(name = "CREATED_DATE")
	 private Date createdDate;
        
}