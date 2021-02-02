package com.comviva.interop.txnengine.model;

import com.comviva.interop.txnengine.util.StringUtils;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TxnMode {

	public static final String FIELD_SEPARATOR = "#";

	public static final String VERSION = "01";

	public static final String TXN_TYPE_REG_FEES = "REGSC";
	public static final String TXN_TYPE_POS_PAYMENT = "CARDPOS";
	public static final String TXN_TYPE_POS_REVERSAL = "CARDPOS";
	public static final String TXN_TYPE_REFUND = "CARDREFUND";
	public static final String TXN_TYPE_ATM_WITHDRAW = "CARDATM";
	public static final String TXN_TYPE_ATM_REVERSAL = "CARDATM";
	public static final String TXN_TYPE_ATM_BALANCE = "SC";
	public static final String TXN_TYPE_WEB_PAYMENT = "CARDWEB";
	public static final String TXN_TYPE_WEB_REVERSAL = "CARDWEB";
	public static final String TXN_TYPE_P2P_ONUS = "P2PONUS";
	public static final String TXN_TYPE_P2P_OFFUS = "P2POFFUS";

	public static final String TXN_STEP_FINANCIAL = "TRN";
	public static final String TXN_STEP_ROLLBACK = "RBT";

	public static final String ADDONS_LOCALLY_HOSTED = "0";
	public static final String ADDONS_GROUP_HOSTED = "1";
	public static final String ADDON_USSD_REQUEST = "2";
	public static final String ADDON_TYPE_EXTERNAL = "3";

	public static final String OM_TXN_UNIQUE_IDENTIFIER_SUCCESS = "1";
	public static final String OM_TXN_UNIQUE_IDENTIFIER_ROLLBACK = "2";
	
	private String version;
	private String transactionType;
	private String transactionStep;
	private String addonID;
	private String countryCode;
	private String currency;
	private String timestampUTC;
	private String addonTransactionID;
	private String omTxnUniqueidentifier;
	private String optionalFields;
	
	public TxnMode(String transactionType,String countryCode,String currency,String addonID,String addonTransactionID,String omTxnUniqueidentifier) {
		this.transactionType=transactionType;
		this.addonID =addonID;
		this.countryCode=countryCode;
		this.currency=currency;
		this.addonTransactionID=addonTransactionID;
		this.omTxnUniqueidentifier=omTxnUniqueidentifier;
	}
	
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		long txnDate = System.currentTimeMillis() / 1000;
		buffer.append(version != null ? StringUtils.leftPad(version, 2, '0') : VERSION);
		buffer.append(FIELD_SEPARATOR);
		buffer.append(transactionType != null ? transactionType : TXN_TYPE_P2P_ONUS);
		buffer.append(FIELD_SEPARATOR);
		buffer.append(transactionStep != null ? transactionStep : TXN_STEP_FINANCIAL);
		buffer.append(FIELD_SEPARATOR);
		buffer.append(addonID != null ? ADDONS_GROUP_HOSTED + addonID : "");
		buffer.append(FIELD_SEPARATOR);
		buffer.append(countryCode != null ? countryCode.toUpperCase() : "");
		buffer.append(FIELD_SEPARATOR);
		buffer.append(currency != null ? currency.toUpperCase() : "");
		buffer.append(FIELD_SEPARATOR);
		buffer.append(txnDate);
		buffer.append(FIELD_SEPARATOR);
		buffer.append(addonTransactionID != null ? addonTransactionID : "");
		buffer.append(FIELD_SEPARATOR);
		buffer.append(omTxnUniqueidentifier != null ? omTxnUniqueidentifier : OM_TXN_UNIQUE_IDENTIFIER_SUCCESS);
		buffer.append(FIELD_SEPARATOR);
		buffer.append(optionalFields != null ? optionalFields : "");
		return buffer.toString().length() > 128 ? "" + txnDate : buffer.toString();
	}

}