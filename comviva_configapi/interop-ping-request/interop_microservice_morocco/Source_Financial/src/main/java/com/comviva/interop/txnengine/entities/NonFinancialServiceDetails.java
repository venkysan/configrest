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
@Table(name = "NON_FINANCIAL_SERVICE_DETAILS")
@Setter
@Getter
public class NonFinancialServiceDetails {

	 @Id
	 @Column(name = "id")
	 @GeneratedValue(strategy = GenerationType.AUTO, generator = "non_fin_service_seqid")
	 private Long id;
	 
	 @Column(name = "OPTIONAL")
	 private String optional;
	 
	 @Column(name = "INTEROP_TXN_ID")
	 private String interopTxnId;

	 @Column(name = "SERVICE_TYPE")
	 private String serviceType;

	 @Column(name = "PAYER_MSISDN")
	 private String payerMsisdn;

	 @Column(name = "PAYEE_MSISDN")
	 private String payeeMsisdn;

	 @Column(name = "RESPONSE_CODE")
	 private String responseCode;

	 @Column(name = "MAPPING_CODE")
	 private String mappingCode;

	 @Column(name = "RESPONSE_MESSAGE")
	 private String responseMessage;

	 @Column(name = "CREATED_DATE")
	 private Date createdDate;

	 @Column(name = "UPDATED_DATE")
	 private Date updatedDate;
}
