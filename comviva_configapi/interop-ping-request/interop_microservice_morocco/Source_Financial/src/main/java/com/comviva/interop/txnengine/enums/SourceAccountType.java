package com.comviva.interop.txnengine.enums;

public enum SourceAccountType {

	UNSPECIFIED("unspecified","00","230000"),
	KYC_LEVEL_1("kycLevel1","80","238000"),
	KYC_LEVEL_2("kycLevel2","81","238100"),
	KYC_LEVEL_3("kycLevel3 ","82","238200"),
	;

	private String accountType;
	private String accountTypeValue;
	private String processingCode;

	private SourceAccountType(String accountType,String accountTypeValue,String processingCode) {
		this.accountType = accountType;
		this.accountTypeValue = accountTypeValue;
		this.processingCode = processingCode;
	}

	public String getAccountType() {
		return accountType;
	}
	
	public String getAccountTypeValue() {
		return accountTypeValue;
	}
	
	public String getProcessingCode() {
		return processingCode;
	}

	public static SourceAccountType getType(String accountType) {
		for(SourceAccountType sourceAccountType:SourceAccountType.values()) {
			if(sourceAccountType.getAccountType().equals(accountType)) {
				return sourceAccountType;
			}
		}
		return null;
	}
	
	public static boolean isPaymentAccoutType(String accountType) {
		for(SourceAccountType sourceAccountType:SourceAccountType.values()) {
			if(sourceAccountType.getAccountType().equals(accountType)) {
				return true;
			}
		}
		return false;
	}
}
