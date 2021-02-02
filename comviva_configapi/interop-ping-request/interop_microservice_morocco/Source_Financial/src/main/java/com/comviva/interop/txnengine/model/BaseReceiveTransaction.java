package com.comviva.interop.txnengine.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BaseReceiveTransaction {

	protected String pan;
	protected String processingCode;
	protected String transactionAmount;
	protected String consolidationAmount;
	protected String cardholderBillAmount;
	protected String dateAndTimeOfTransmission;
	protected String cardholderBillingEexchangeRate;
	protected String systemAuditNumber;
	protected String dateAndTimeOfTheTransaction;
	protected String settlementDate;
	protected String exchangeDate;
	protected String businessType;
	protected String countryCodeOfTheAcquiringOrganization;
	protected String countryCodeOftheSenderOrganization;
	protected String servicePointDataCode;
	protected String functionCode;
}
