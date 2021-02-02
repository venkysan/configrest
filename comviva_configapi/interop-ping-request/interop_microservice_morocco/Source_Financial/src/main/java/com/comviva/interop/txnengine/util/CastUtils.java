package com.comviva.interop.txnengine.util;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.comviva.interop.txnengine.enums.LogConstants;
import com.comviva.interop.txnengine.model.AccountIdentifierBase;
import com.comviva.interop.txnengine.model.ConfirmTransactionRequest;
import com.comviva.interop.txnengine.model.PendingTransactionRequest;
import com.comviva.interop.txnengine.model.QuotationRequest;
import com.comviva.interop.txnengine.model.ReceiveTransactionRequest;
import com.comviva.interop.txnengine.model.RequestWrapper;
import com.comviva.interop.txnengine.model.TransactionRequest;
import com.comviva.interop.txnengine.model.TransactionStatusRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CastUtils {

	private CastUtils() {
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(CastUtils.class);
	public static final ObjectMapper mapper = new ObjectMapper();

	public static final String ENGLISH_LANGUAGE_CODE = "en";

	public static final String FRANCE_LANGUAGE_CODE = "fr";

	public static QuotationRequest toTransactionQuotationRequest(RequestWrapper requestAttr) {
		return (QuotationRequest) requestAttr;
	}

	public static TransactionRequest toTransactionRequest(RequestWrapper requestAttr) {
		return (TransactionRequest) requestAttr;
	}

	public static TransactionStatusRequest toTransactionStatusRequest(RequestWrapper requestAttr) {
		return (TransactionStatusRequest) requestAttr;
	}

	public static ReceiveTransactionRequest toReceiveTransactionRequest(RequestWrapper requestAttr) {
		return (ReceiveTransactionRequest) requestAttr;
	}

	public static PendingTransactionRequest toPendingTransactionRequest(RequestWrapper requestAttr) {
		return (PendingTransactionRequest) requestAttr;
	}

	public static ConfirmTransactionRequest toConfirmTransactionRequest(RequestWrapper requestAttr) {
		return (ConfirmTransactionRequest) requestAttr;
	}

	public static Map<String, String> stringToMap(String payload) {
		Map<String, String> map = null;
		try {
			map = mapper.readValue(payload, new TypeReference<Map<String, Object>>() {
			});
		} catch (Exception e) {
			String message = LoggerUtil.printLog(LogConstants.CASTING_FAILED_EVENT.getValue(), e);
			LOGGER.info("Exception in CastUtils while casting string to map..{}", message);
		}
		return map;
	}

	public static String joinStatusCode(String entity, String statusCode) {
		return entity + "_" + statusCode;
	}

	public static String getLanguageWithCountryCode(String language) {
		switch (language) {
		case ENGLISH_LANGUAGE_CODE:
			return Locale.US.toString();
		case FRANCE_LANGUAGE_CODE:
			return Locale.FRANCE.toString();
		default:
			return language;
		}
	}

	public static boolean isSenderRequiredDetailsExists(List<AccountIdentifierBase> party,
			AccountIdentifierBase.KeyEnum key) {
		boolean valid = false;
		if (null != party && !party.isEmpty()) {
			Iterator<AccountIdentifierBase> iterator = party.iterator();
			while (iterator.hasNext()) {
				if (iterator.next().getKey().equals(key)) {
					valid = true;
					break;
				}
			}
		}
		return valid;
	}
	
	
}
