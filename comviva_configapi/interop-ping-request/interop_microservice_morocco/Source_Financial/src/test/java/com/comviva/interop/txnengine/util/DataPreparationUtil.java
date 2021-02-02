package com.comviva.interop.txnengine.util;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.comviva.interop.txnengine.entities.ChannelUserDetails;
import com.comviva.interop.txnengine.entities.InteropTransactions;
import com.comviva.interop.txnengine.enums.RequestSource;
import com.comviva.interop.txnengine.enums.SourceAccountType;
import com.comviva.interop.txnengine.enums.TestCaseConstants;
import com.comviva.interop.txnengine.enums.TransactionStatus;
import com.comviva.interop.txnengine.model.AccountIdentifierBase;
import com.comviva.interop.txnengine.model.ConfirmTransactionRequest;
import com.comviva.interop.txnengine.model.PendingTransactionRequest;
import com.comviva.interop.txnengine.model.QuotationRequest;
import com.comviva.interop.txnengine.model.ReceiveTransactionRequest;
import com.comviva.interop.txnengine.model.Request;
import com.comviva.interop.txnengine.model.TransactionRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.comviva.interop.txnengine.model.ConfirmTransactionRequest.ActionEnum;
import com.comviva.interop.txnengine.model.DebitPartyCredentialsDetails;
import com.comviva.interop.txnengine.model.NetworkMessageRequest;

public class DataPreparationUtil {

	private DataPreparationUtil() {
	}

	public static String prepareTransactionRequest(String amount, String recevierMsisdn, String currency,
			String senderMSISDN, String senderPin, String extOrgRefId, String transactionType) {

		return new StringBuffer().append(TestCaseConstants.LEFT_CURLY_BRACE.getValue())
				.append(TestCaseConstants.AMOUNT_KEY.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(amount).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.CREDIT_PARTY_KEY.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(TestCaseConstants.LEFT_ARRAY.getValue()).append(TestCaseConstants.LEFT_CURLY_BRACE.getValue())
				.append(TestCaseConstants.KEY_NAME.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(TestCaseConstants.MSISDN_KEY.getValue()).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.VALUE_NAME.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(recevierMsisdn).append(TestCaseConstants.RIGHT_CURLY_BRACE.getValue())
				.append(TestCaseConstants.RIGHT_ARRAY.getValue()).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.CURRENCY_NAME.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(currency).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.DEBIT_PARTY_KEY.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(TestCaseConstants.LEFT_ARRAY.getValue()).append(TestCaseConstants.LEFT_CURLY_BRACE.getValue())
				.append(TestCaseConstants.KEY_NAME.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(TestCaseConstants.MSISDN_KEY.getValue()).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.VALUE_NAME.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(senderMSISDN).append(TestCaseConstants.RIGHT_CURLY_BRACE.getValue())
				.append(TestCaseConstants.RIGHT_ARRAY.getValue()).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.DEBITPARTY_CREDENTIALS_KEY.getValue())
				.append(TestCaseConstants.COLON.getValue()).append(TestCaseConstants.LEFT_CURLY_BRACE.getValue())
				.append(TestCaseConstants.PIN_NAME.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(senderPin).append(TestCaseConstants.COMMA.getValue()).append(TestCaseConstants.EM.getValue())
				.append(TestCaseConstants.COLON.getValue()).append("\"1234\"")
				.append(TestCaseConstants.RIGHT_CURLY_BRACE.getValue()).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.EXT_ORG_REF_ID_NAME.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(extOrgRefId).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.LANGUAGE_KEY.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(TestCaseConstants.LANGUAGE_VALUE.getValue()).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.REQUEST_SOURCE_KEY.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(TestCaseConstants.REQUEST_SOURCE_VALUE.getValue()).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.TXN_TYPE_NAME.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(transactionType).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.SOURCE_ACCOUNT_TYPE_KEY.getValue()).append(TestCaseConstants.COLON.getValue())
				.append("\"unspecified\"").append(TestCaseConstants.RIGHT_CURLY_BRACE.getValue()).toString();
	}

	public static String prepareTransactionRequestWithoutCreditParty(String amount, String currency,
			String senderMSISDN, String senderPin, String extOrgRefId, String transactionType) {

		return new StringBuffer().append(TestCaseConstants.LEFT_CURLY_BRACE.getValue())
				.append(TestCaseConstants.AMOUNT_KEY.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(amount).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.CURRENCY_NAME.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(currency).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.DEBIT_PARTY_KEY.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(TestCaseConstants.LEFT_ARRAY.getValue()).append(TestCaseConstants.LEFT_CURLY_BRACE.getValue())
				.append(TestCaseConstants.KEY_NAME.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(TestCaseConstants.MSISDN_KEY.getValue()).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.VALUE_NAME.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(senderMSISDN).append(TestCaseConstants.RIGHT_CURLY_BRACE.getValue())
				.append(TestCaseConstants.RIGHT_ARRAY.getValue()).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.DEBITPARTY_CREDENTIALS_KEY.getValue())
				.append(TestCaseConstants.COLON.getValue()).append(TestCaseConstants.LEFT_CURLY_BRACE.getValue())
				.append(TestCaseConstants.PIN_NAME.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(senderPin).append(TestCaseConstants.COMMA.getValue()).append(TestCaseConstants.EM.getValue())
				.append(TestCaseConstants.COLON.getValue()).append("\"1234\"")
				.append(TestCaseConstants.RIGHT_CURLY_BRACE.getValue()).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.EXT_ORG_REF_ID_NAME.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(extOrgRefId).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.LANGUAGE_KEY.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(TestCaseConstants.LANGUAGE_VALUE.getValue()).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.REQUEST_SOURCE_KEY.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(TestCaseConstants.REQUEST_SOURCE_VALUE.getValue()).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.TXN_TYPE_NAME.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(transactionType).append(TestCaseConstants.RIGHT_CURLY_BRACE.getValue()).toString();
	}

	public static String prepareTransactionRequestWithoutDebitParty(String amount, String recevierMsisdn,
			String currency, String extOrgRefId, String transactionType) {

		return new StringBuffer().append(TestCaseConstants.LEFT_CURLY_BRACE.getValue())
				.append(TestCaseConstants.AMOUNT_KEY.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(amount).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.CREDIT_PARTY_KEY.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(TestCaseConstants.LEFT_ARRAY.getValue()).append(TestCaseConstants.LEFT_CURLY_BRACE.getValue())
				.append(TestCaseConstants.KEY_NAME.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(TestCaseConstants.MSISDN_KEY.getValue()).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.VALUE_NAME.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(recevierMsisdn).append(TestCaseConstants.RIGHT_CURLY_BRACE.getValue())
				.append(TestCaseConstants.RIGHT_ARRAY.getValue()).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.CURRENCY_NAME.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(currency).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.DEBITPARTY_CREDENTIALS_KEY.getValue())
				.append(TestCaseConstants.COLON.getValue()).append(TestCaseConstants.LEFT_CURLY_BRACE.getValue())
				.append(TestCaseConstants.PIN_NAME.getValue()).append(TestCaseConstants.COLON.getValue())
				.append("\"3456\"").append(TestCaseConstants.COMMA.getValue()).append(TestCaseConstants.EM.getValue())
				.append(TestCaseConstants.COLON.getValue()).append("\"1234\"")
				.append(TestCaseConstants.RIGHT_CURLY_BRACE.getValue()).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.EXT_ORG_REF_ID_NAME.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(extOrgRefId).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.LANGUAGE_KEY.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(TestCaseConstants.LANGUAGE_VALUE.getValue()).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.REQUEST_SOURCE_KEY.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(TestCaseConstants.REQUEST_SOURCE_VALUE.getValue()).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.TXN_TYPE_NAME.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(transactionType).append(TestCaseConstants.RIGHT_CURLY_BRACE.getValue()).toString();
	}

	public static String prepareTransactionRequestWithOutAmount(String recevierMsisdn, String currency,
			String senderMSISDN, String senderPin, String extOrgRefId, String transactionType) {

		return new StringBuffer().append(TestCaseConstants.LEFT_CURLY_BRACE.getValue())
				.append(TestCaseConstants.COMMA.getValue()).append(TestCaseConstants.CREDIT_PARTY_KEY.getValue())
				.append(TestCaseConstants.COLON.getValue()).append(TestCaseConstants.LEFT_ARRAY.getValue())
				.append(TestCaseConstants.LEFT_CURLY_BRACE.getValue()).append(TestCaseConstants.KEY_NAME.getValue())
				.append(TestCaseConstants.COLON.getValue()).append(TestCaseConstants.MSISDN_KEY.getValue())
				.append(TestCaseConstants.COMMA.getValue()).append(TestCaseConstants.VALUE_NAME.getValue())
				.append(TestCaseConstants.COLON.getValue()).append(recevierMsisdn)
				.append(TestCaseConstants.RIGHT_CURLY_BRACE.getValue()).append(TestCaseConstants.RIGHT_ARRAY.getValue())
				.append(TestCaseConstants.COMMA.getValue()).append(TestCaseConstants.CURRENCY_NAME.getValue())
				.append(TestCaseConstants.COLON.getValue()).append(currency).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.DEBIT_PARTY_KEY.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(TestCaseConstants.LEFT_ARRAY.getValue()).append(TestCaseConstants.LEFT_CURLY_BRACE.getValue())
				.append(TestCaseConstants.KEY_NAME.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(TestCaseConstants.MSISDN_KEY.getValue()).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.VALUE_NAME.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(senderMSISDN).append(TestCaseConstants.RIGHT_CURLY_BRACE.getValue())
				.append(TestCaseConstants.COMMA.getValue()).append(TestCaseConstants.LEFT_CURLY_BRACE.getValue())
				.append(TestCaseConstants.KEY_NAME.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(TestCaseConstants.PIN_NAME.getValue()).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.VALUE_NAME.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(senderPin).append(TestCaseConstants.RIGHT_CURLY_BRACE.getValue())
				.append(TestCaseConstants.RIGHT_ARRAY.getValue()).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.EXT_ORG_REF_ID_NAME.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(extOrgRefId).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.LANGUAGE_KEY.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(TestCaseConstants.LANGUAGE_VALUE.getValue()).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.REQUEST_SOURCE_KEY.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(TestCaseConstants.REQUEST_SOURCE_VALUE.getValue()).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.TXN_TYPE_NAME.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(transactionType).append(TestCaseConstants.RIGHT_CURLY_BRACE.getValue()).toString();
	}

	public static String prepareTransactionRequestWithoutKeyInCreditParty(String amount, String recevierMsisdn,
			String currency, String senderMSISDN, String senderPin, String extOrgRefId, String transactionType) {

		return new StringBuffer().append(TestCaseConstants.LEFT_CURLY_BRACE.getValue())
				.append(TestCaseConstants.AMOUNT_KEY.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(amount).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.CREDIT_PARTY_KEY.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(TestCaseConstants.LEFT_ARRAY.getValue()).append(TestCaseConstants.LEFT_CURLY_BRACE.getValue())
				.append(TestCaseConstants.COLON.getValue()).append(TestCaseConstants.MSISDN_KEY.getValue())
				.append(TestCaseConstants.COMMA.getValue()).append(TestCaseConstants.VALUE_NAME.getValue())
				.append(TestCaseConstants.COLON.getValue()).append(recevierMsisdn)
				.append(TestCaseConstants.RIGHT_CURLY_BRACE.getValue()).append(TestCaseConstants.RIGHT_ARRAY.getValue())
				.append(TestCaseConstants.COMMA.getValue()).append(TestCaseConstants.CURRENCY_NAME.getValue())
				.append(TestCaseConstants.COLON.getValue()).append(currency).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.DEBIT_PARTY_KEY.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(TestCaseConstants.LEFT_ARRAY.getValue()).append(TestCaseConstants.LEFT_CURLY_BRACE.getValue())
				.append(TestCaseConstants.KEY_NAME.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(TestCaseConstants.MSISDN_KEY.getValue()).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.VALUE_NAME.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(senderMSISDN).append(TestCaseConstants.RIGHT_CURLY_BRACE.getValue())
				.append(TestCaseConstants.COMMA.getValue()).append(TestCaseConstants.LEFT_CURLY_BRACE.getValue())
				.append(TestCaseConstants.KEY_NAME.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(TestCaseConstants.PIN_NAME.getValue()).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.VALUE_NAME.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(senderPin).append(TestCaseConstants.RIGHT_CURLY_BRACE.getValue())
				.append(TestCaseConstants.RIGHT_ARRAY.getValue()).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.EXT_ORG_REF_ID_NAME.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(extOrgRefId).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.LANGUAGE_KEY.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(TestCaseConstants.LANGUAGE_VALUE.getValue()).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.REQUEST_SOURCE_KEY.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(TestCaseConstants.REQUEST_SOURCE_VALUE.getValue()).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.TXN_TYPE_NAME.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(transactionType).append(TestCaseConstants.RIGHT_CURLY_BRACE.getValue()).toString();
	}

	public static String prepareTransactionRequestWithoutMSISDNAsKeyInCreditParty(String amount, String recevierMsisdn,
			String currency, String senderMSISDN, String senderPin, String extOrgRefId, String transactionType) {

		return new StringBuffer().append(TestCaseConstants.LEFT_CURLY_BRACE.getValue())
				.append(TestCaseConstants.AMOUNT_KEY.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(amount).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.CREDIT_PARTY_KEY.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(TestCaseConstants.LEFT_ARRAY.getValue()).append(TestCaseConstants.LEFT_CURLY_BRACE.getValue())
				.append(TestCaseConstants.KEY_NAME.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(TestCaseConstants.COMMA.getValue()).append(TestCaseConstants.VALUE_NAME.getValue())
				.append(TestCaseConstants.COLON.getValue()).append(recevierMsisdn)
				.append(TestCaseConstants.RIGHT_CURLY_BRACE.getValue()).append(TestCaseConstants.RIGHT_ARRAY.getValue())
				.append(TestCaseConstants.COMMA.getValue()).append(TestCaseConstants.CURRENCY_NAME.getValue())
				.append(TestCaseConstants.COLON.getValue()).append(currency).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.DEBIT_PARTY_KEY.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(TestCaseConstants.LEFT_ARRAY.getValue()).append(TestCaseConstants.LEFT_CURLY_BRACE.getValue())
				.append(TestCaseConstants.KEY_NAME.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(TestCaseConstants.MSISDN_KEY.getValue()).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.VALUE_NAME.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(senderMSISDN).append(TestCaseConstants.RIGHT_CURLY_BRACE.getValue())
				.append(TestCaseConstants.COMMA.getValue()).append(TestCaseConstants.LEFT_CURLY_BRACE.getValue())
				.append(TestCaseConstants.KEY_NAME.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(TestCaseConstants.PIN_NAME.getValue()).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.VALUE_NAME.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(senderPin).append(TestCaseConstants.RIGHT_CURLY_BRACE.getValue())
				.append(TestCaseConstants.RIGHT_ARRAY.getValue()).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.EXT_ORG_REF_ID_NAME.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(extOrgRefId).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.LANGUAGE_KEY.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(TestCaseConstants.LANGUAGE_VALUE.getValue()).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.REQUEST_SOURCE_KEY.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(TestCaseConstants.REQUEST_SOURCE_VALUE.getValue()).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.TXN_TYPE_NAME.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(transactionType).append(TestCaseConstants.RIGHT_CURLY_BRACE.getValue()).toString();
	}

	public static String prepareInitiateEnrollmentRequest(String msisdn, String language) {

		return new StringBuffer().append(TestCaseConstants.LEFT_CURLY_BRACE.getValue())
				.append(TestCaseConstants.MSISDN_KEY.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(msisdn).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.LANGUAGE_KEY.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(language).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.REQUEST_SOURCE_KEY.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(TestCaseConstants.REQUEST_SOURCE_VALUE.getValue()).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.EXTORG_REF_ID_KEY.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(TestCaseConstants.EXTORG_REF_ID_VALUE.getValue())
				.append(TestCaseConstants.RIGHT_CURLY_BRACE.getValue()).toString();
	}

	public static String prepareConfirmEnrollmentRequest(String otp, String language) {

		return new StringBuffer().append(TestCaseConstants.LEFT_CURLY_BRACE.getValue())
				.append(TestCaseConstants.OTP_KEY.getValue()).append(TestCaseConstants.COLON.getValue()).append(otp)
				.append(TestCaseConstants.COMMA.getValue()).append(TestCaseConstants.LANGUAGE_KEY.getValue())
				.append(TestCaseConstants.COLON.getValue()).append(language)
				.append(TestCaseConstants.RIGHT_CURLY_BRACE.getValue()).toString();
	}

	public static String prepareTransactionRequestWithoutValueInCreditParty(String amount, String recevierMsisdn,
			String currency, String senderMSISDN, String senderPin, String extOrgRefId, String transactionType) {

		return new StringBuffer().append(TestCaseConstants.LEFT_CURLY_BRACE.getValue())
				.append(TestCaseConstants.AMOUNT_KEY.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(amount).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.CREDIT_PARTY_KEY.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(TestCaseConstants.LEFT_ARRAY.getValue()).append(TestCaseConstants.LEFT_CURLY_BRACE.getValue())
				.append(TestCaseConstants.KEY_NAME.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(TestCaseConstants.MSISDN_KEY.getValue()).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.COLON.getValue()).append(recevierMsisdn)
				.append(TestCaseConstants.RIGHT_CURLY_BRACE.getValue()).append(TestCaseConstants.RIGHT_ARRAY.getValue())
				.append(TestCaseConstants.COMMA.getValue()).append(TestCaseConstants.CURRENCY_NAME.getValue())
				.append(TestCaseConstants.COLON.getValue()).append(currency).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.DEBIT_PARTY_KEY.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(TestCaseConstants.LEFT_ARRAY.getValue()).append(TestCaseConstants.LEFT_CURLY_BRACE.getValue())
				.append(TestCaseConstants.KEY_NAME.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(TestCaseConstants.MSISDN_KEY.getValue()).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.VALUE_NAME.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(senderMSISDN).append(TestCaseConstants.RIGHT_CURLY_BRACE.getValue())
				.append(TestCaseConstants.COMMA.getValue()).append(TestCaseConstants.LEFT_CURLY_BRACE.getValue())
				.append(TestCaseConstants.KEY_NAME.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(TestCaseConstants.PIN_NAME.getValue()).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.VALUE_NAME.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(senderPin).append(TestCaseConstants.RIGHT_CURLY_BRACE.getValue())
				.append(TestCaseConstants.RIGHT_ARRAY.getValue()).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.EXT_ORG_REF_ID_NAME.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(extOrgRefId).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.LANGUAGE_KEY.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(TestCaseConstants.LANGUAGE_VALUE.getValue()).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.REQUEST_SOURCE_KEY.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(TestCaseConstants.REQUEST_SOURCE_VALUE.getValue()).append(TestCaseConstants.COMMA.getValue())
				.append(TestCaseConstants.TXN_TYPE_NAME.getValue()).append(TestCaseConstants.COLON.getValue())
				.append(transactionType).append(TestCaseConstants.RIGHT_CURLY_BRACE.getValue()).toString();
	}

	@SuppressWarnings("rawtypes")
	public static ResponseEntity<Map> getResponseEntityP2POn() {
		return new ResponseEntity<Map>(
				getUserHandlerResponse(TestCaseConstants.ENROLMENT_REG_SUCCESS_STATUS.getValue()), HttpStatus.OK);
	}

	public static Map<String, String> getUserHandlerResponse(String status) {
		Map<String, String> map = new HashMap<>();
		map.put(TestCaseConstants.DEFAULT_WALLET_STATUE.getValue(), status);
		map.put(TestCaseConstants.MAPPED_CODE_NAME.getValue(), TestCaseConstants.MAPPED_CODE_VALUE.getValue());
		map.put(TestCaseConstants.CODE.getValue(), TestCaseConstants.CODE_VALUE_SUCCESS.getValue());
		return map;
	}

	public static Map<String, String> getHPSTransactionResponse(String statusCode) {
		Map<String, String> map = new HashMap<>();
		map.put("statusCode", statusCode);
		map.put("actionCode", "000");
		return map;
	}

	public static TransactionRequest prepareTransactionrequestObj() {
		TransactionRequest transactionRequest = new TransactionRequest();
		transactionRequest.setAmount(TestCaseConstants.DEFAULT_AMOUNT_VALUE.getValue());
		transactionRequest.setCreditParty(prepareAccountIdentifierBaseObj());
		transactionRequest.setCurrency(TestCaseConstants.DEFAULT_CURRENCY.getValue());
		transactionRequest.setDebitParty(prepareAccountIdentifierBaseObj());
		transactionRequest.setExtOrgRefId(TestCaseConstants.EXT_ORG_REF_ID_VALUE.getValue());
		transactionRequest.setLang(TestCaseConstants.LANGUAGE_VALUE.getValue());
		transactionRequest.setRequestSource(RequestSource.MOBILEAPP.getSource());
		transactionRequest.setTransactionType("p2p");
		DebitPartyCredentialsDetails debitPartyCredentialsDetails = new DebitPartyCredentialsDetails();
		debitPartyCredentialsDetails.setEm("1234");
		debitPartyCredentialsDetails.setPin("3456");
		transactionRequest.setDebitPartyCredentials(debitPartyCredentialsDetails);
		transactionRequest.setSourceAccountType(SourceAccountType.KYC_LEVEL_3.getAccountType());
		return transactionRequest;
	}

	public static QuotationRequest prepareTransactionQuotationrequestObj() {
		QuotationRequest transactionRequest = new QuotationRequest();
		transactionRequest.setAmount(TestCaseConstants.DEFAULT_AMOUNT_VALUE.getValue());
		transactionRequest.setCreditParty(prepareAccountIdentifierBaseObj());
		transactionRequest.setCurrency(TestCaseConstants.DEFAULT_CURRENCY.getValue());
		transactionRequest.setDebitParty(prepareAccountIdentifierBaseObj());
		transactionRequest.setExtOrgRefId(TestCaseConstants.EXT_ORG_REF_ID_VALUE.getValue());
		transactionRequest.setLang(TestCaseConstants.LANGUAGE_VALUE.getValue());
		transactionRequest.setRequestSource(TestCaseConstants.REQUEST_SOURCE_VALUE.getValue());
		transactionRequest.setTransactionType(TestCaseConstants.P2P_TXN_TYPE_VALUE.getValue());
		return transactionRequest;
	}

	public static List<AccountIdentifierBase> prepareAccountIdentifierBaseObj() {
		List<AccountIdentifierBase> accountIdentifierBases = new ArrayList<>();
		AccountIdentifierBase accountIdentifierBase = new AccountIdentifierBase();
		accountIdentifierBase.setKey(AccountIdentifierBase.KeyEnum.msisdn);
		accountIdentifierBase.setValue(TestCaseConstants.DEFAULT_MSISDN.getValue());
		accountIdentifierBases.add(accountIdentifierBase);
		return accountIdentifierBases;
	}

	public static long generateRandomMSISDN() {
		SecureRandom generator = new SecureRandom();
		generator.setSeed(System.currentTimeMillis());
		int i = generator.nextInt(10000000) % 10000000;
		java.text.DecimalFormat f = new java.text.DecimalFormat("1100000000");
		return Long.parseLong(f.format(i));
	}

	public static InteropTransactions prepareInteropTransaction(TransactionRequest transactionRequest, Request request,
			String transactionSubType) {
		InteropTransactions interopTransactions = new InteropTransactions();
		interopTransactions.setAmount(new BigDecimal(transactionRequest.getAmount()));
		interopTransactions.setCreatedDate(new Date());
		interopTransactions.setCurrency(transactionRequest.getCurrency());
		interopTransactions.setExtOrgRefId(transactionRequest.getExtOrgRefId());
		interopTransactions.setInteropTxnId(request.getInteropReferenceId());
		interopTransactions.setPayeeMsisdn(transactionRequest.getCreditParty().get(0).getValue());
		interopTransactions.setPayerMsisdn(transactionRequest.getDebitParty().get(0).getValue());
		interopTransactions.setRequestSource(transactionRequest.getRequestSource());
		interopTransactions.setTransactionType(transactionRequest.getTransactionType());
		interopTransactions.setTxnStatus(TransactionStatus.TRANSACTION_IN_PROGRESS.getStatus());
		interopTransactions.setUpdatedDate(new Date());
		interopTransactions.setTransactionSubType(transactionSubType);
		return interopTransactions;
	}

	public static ChannelUserDetails prepareChannelUserDetails() {
		ChannelUserDetails channelUserDetails = new ChannelUserDetails();
		channelUserDetails.setChannelUserCode(TestCaseConstants.CHANNEL_USER_CODE.getValue());
		channelUserDetails.setCountryId(TestCaseConstants.COUNTRY_ID.getValue());
		channelUserDetails.setCreatedDate(new Date());
		channelUserDetails.setDescription(TestCaseConstants.CHANNEL_USER.getValue());
		channelUserDetails.setMsisdn(TestCaseConstants.DEFAULT_MSISDN.getValue());
		channelUserDetails.setOptional(TestCaseConstants.OPTIONAL_MSG.getValue());
		channelUserDetails.setType(TestCaseConstants.CHANNEL_USER.getValue());
		channelUserDetails.setUpdatedDate(new Date());
		channelUserDetails.setUserId(TestCaseConstants.CHANNEL_USER_ID.getValue());
		return channelUserDetails;
	}

	public static ReceiveTransactionRequest prepareReceiveTransactionRequest() {
		ReceiveTransactionRequest request = new ReceiveTransactionRequest();
		request.setPan(TestCaseConstants.PAN.getValue());
		request.setProcessingCode(TestCaseConstants.PROCESSING_CODE.getValue());
		request.setTransactionAmount(TestCaseConstants.TRANSACTION_AMOUNT.getValue());
		request.setConsolidationAmount(TestCaseConstants.TRANSACTION_AMOUNT.getValue());
		request.setCardholderBillAmount(TestCaseConstants.TRANSACTION_AMOUNT.getValue());
		request.setDateAndTimeOfTheTransaction(TestCaseConstants.DATEANDTIME_TRANSMISSION.getValue());
		request.setCardholderBillingEexchangeRate(TestCaseConstants.CARDHOLDERBILLINGEEXCHANGERATE.getValue());
		request.setSystemAuditNumber(TestCaseConstants.SYSTEMAUDITNUMBER.getValue());
		request.setDateAndTimeOfTheTransaction(TestCaseConstants.DATEANDTIMEOFTHETRANSACTION.getValue());
		request.setSettlementDate(TestCaseConstants.SETTLEMENTDATE.getValue());
		request.setExchangeDate(TestCaseConstants.EXCHANGEDATE.getValue());
		request.setBusinessType(TestCaseConstants.BUSINESSTYPE.getValue());
		request.setCountryCodeOfTheAcquiringOrganization(TestCaseConstants.COUNTRY_CODE.getValue());
		request.setCountryCodeOftheSenderOrganization(TestCaseConstants.COUNTRY_CODE.getValue());
		request.setServicePointDataCode(TestCaseConstants.SERVICEPOINTDATACODE.getValue());
		request.setFunctionCode(TestCaseConstants.FUNCTIONCODE.getValue());
		request.setIdentificationCodeOfTheAcquiringOrganization(
				TestCaseConstants.IDENTIFICATIONCODEOFTHEACQUIRINGORGANIZATION.getValue());
		request.setIdentificationCodeOfTheSendingOrganization(
				TestCaseConstants.IDENTIFICATIONCODEOFTHESENDINGORGANIZATION.getValue());
		request.setReferenceNumberOfTheRecovery(TestCaseConstants.REFERENCENUMBEROFTHERECOVERY.getValue());
		request.setCurrencyCodeOfTheCardholderInvoice(TestCaseConstants.CURRENCYCODE.getValue());
		request.setCurrencyCodeOfTheConsolidation(TestCaseConstants.CURRENCYCODE.getValue());
		request.setCurrencyCodeOfTheTransaction(TestCaseConstants.CURRENCYCODE.getValue());
		request.setSourceAccountNumber(TestCaseConstants.SOURCEACCOUNTNUMBER.getValue());
		request.setDestinationAccountNumber(TestCaseConstants.DESTINATIONACCOUNTNUMBER.getValue());
		return request;
	}

	public static PendingTransactionRequest preparePendingTransactionRequest(String msisdn, String numberOfTransactions,
			String lang) {
		PendingTransactionRequest pendingTransactionRequest = new PendingTransactionRequest();
		pendingTransactionRequest.setLang(lang);
		pendingTransactionRequest.setMsisdn(msisdn);
		pendingTransactionRequest.setNumberOfTransactions(numberOfTransactions);
		return pendingTransactionRequest;
	}

	public static ConfirmTransactionRequest prepareConfirmTransactionRequest(String interopRefId, String pin,
			String lang, String action) {
		ConfirmTransactionRequest confirmTransactionRequest = new ConfirmTransactionRequest();
		confirmTransactionRequest.setLang(lang);
		confirmTransactionRequest.setAction(ActionEnum.fromValue(action));
		confirmTransactionRequest.setInteropRefId(interopRefId);
		confirmTransactionRequest.setPin(pin);
		return confirmTransactionRequest;
	}

	public static String convertObjectToJson(Object obj) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			return null;
		}
	}

	public static InteropTransactions getInteropTransaction() {
		InteropTransactions interopTransactions = new InteropTransactions();
		interopTransactions.setInteropTxnId("12345");
		interopTransactions.setAmount(new BigDecimal("100"));
		interopTransactions.setCurrency("XOF");
		interopTransactions.setCreatedDate(new Date());
		interopTransactions.setExtOrgRefId("sdad423234");
		interopTransactions.setPayeeMsisdn("1212121223");
		interopTransactions.setPayerMsisdn("1212121224");
		interopTransactions.setRequestSource("Interop");
		interopTransactions.setTransactionType("P2P");
		interopTransactions.setTxnStatus("TS");
		interopTransactions.setUpdatedDate(new Date());
		return interopTransactions;
	}

	public static NetworkMessageRequest getNetworkMessageRequest() {
		NetworkMessageRequest networkMessageRequest = new NetworkMessageRequest();
		networkMessageRequest.setNetworkAction("signOff");
		return networkMessageRequest;
	}

}
