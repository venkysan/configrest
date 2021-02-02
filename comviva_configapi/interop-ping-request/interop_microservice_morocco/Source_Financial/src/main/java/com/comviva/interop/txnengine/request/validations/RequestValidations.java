package com.comviva.interop.txnengine.request.validations;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comviva.interop.txnengine.configuration.CurrencyLoader;
import com.comviva.interop.txnengine.configuration.LanguageLoader;
import com.comviva.interop.txnengine.configuration.Resource;
import com.comviva.interop.txnengine.enums.RequestSource;
import com.comviva.interop.txnengine.enums.SourceAccountType;
import com.comviva.interop.txnengine.enums.TransactionTypes;
import com.comviva.interop.txnengine.enums.ValidationErrors;
import com.comviva.interop.txnengine.model.AccountIdentifierBase;
import com.comviva.interop.txnengine.model.RequestValidationResponse;

@Component
public class RequestValidations {

    private LanguageLoader languageLoader;
    private Set<String> languageCodes;

    @Autowired
    private Resource resource;

    @Autowired
    private CurrencyLoader currencyLoader;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    public void setLanguageCodes(Set<String> languageCodes) {
        writeLock.lock();
        try {
        	this.languageCodes=Collections.unmodifiableSet(languageCodes);
        } finally {
            writeLock.unlock();
        }
    }

    @Autowired
    public RequestValidations(LanguageLoader languageLoader) {
        this.languageLoader = languageLoader;
        this.languageCodes = languageLoader.loadLanguageCodes();
    }

    public ValidationErrors validateSenderMsisdn(String msisdn, int length) {
        if (isEmpty(msisdn)) {
            return ValidationErrors.SENDER_MSISDN_MISSING;
        }
        if (!isNumeric(msisdn)) {
            return ValidationErrors.SENDER_MSISDN_SHOULD_BE_NUMERIC;
        }
        if (!compareLength(msisdn, length)) {
            return ValidationErrors.INVALID_SENDER_MSISDN_LENGTH;
        }
        return ValidationErrors.VALID;
    }

    public ValidationErrors validateReceiverMsisdn(String msisdn, int length) {
        if (isEmpty(msisdn)) {
            return ValidationErrors.RECEVIER_MSISDN_MISSING;
        }
        if (!isNumeric(msisdn)) {
            return ValidationErrors.RECEVIER_MSISDN_SHOULD_BE_NUMERIC;
        }
        if (!compareLength(msisdn, length)) {
            return ValidationErrors.INVALID_RECEVIER_MSISDN_LENGTH;
        }
        return ValidationErrors.VALID;
    }

    public ValidationErrors validatePin(String pin, int length) {
        if (isEmpty(pin)) {
            return ValidationErrors.PIN_MISSING;
        }
       
        if (!compareLength(pin, length)) {
            return ValidationErrors.INVALID_PIN_LENGTH;
        }
        return ValidationErrors.VALID;
    }

    public ValidationErrors validateInteropreferenceid(String interopreferenceid) {
        if (isEmpty(interopreferenceid)) {
            return ValidationErrors.INTEROP_REFERENCE_ID_MISSING;
        }
        return ValidationErrors.VALID;
    }

    public ValidationErrors validateAmount(String amount) {
        if (isEmpty(amount) || !amount.matches("^[1-9]\\d*(\\.\\d+)?$")) {
            return ValidationErrors.INCORRECT_AMOUNT_FORMAT;
        }
        return ValidationErrors.VALID;
    }

    public ValidationErrors validateExtOrgRefId(String extOrgRefId) {
        if (isEmpty(extOrgRefId)) {
            return ValidationErrors.EXT_ORG_REF_ID_MISSING;
        }
        return ValidationErrors.VALID;
    }

    public ValidationErrors validateCurrency(String currency) {
        if (isEmpty(currency)) {
            return ValidationErrors.CURRENCY_MISSING;
        }
        if (!Optional.ofNullable(currencyLoader.getCurrencyByCode(currency)).isPresent()) {
            return ValidationErrors.INVALID_CURRENCY;
        }
        return ValidationErrors.VALID;
    }

    public boolean isEmpty(String str) {
        return (null == str || "".equals(str));
    }

    public boolean isNumeric(String str) {
        String regex = "\\d+";
        return str.matches(regex);
    }

    public boolean compareLength(String str, int length) {
        return (length == str.length());
    }

    public ValidationErrors validateTransactionType(String type) {
        if (isEmpty(type)) {
            return ValidationErrors.TRANSACTION_TYPE_MISSING;
        }
        
        if (!isValidTransactionType(type)) {
            return ValidationErrors.INVALID_TRANSACTION_TYPE;
        }
        return ValidationErrors.VALID;
    }

    public ValidationErrors validateLanguage(String str) {
        if (isEmpty(str)) {
            return ValidationErrors.LANGUAGE_MISSING;
        }

        readLock.lock();
        try {
            if (!languageCodes.contains(str)) {
                return ValidationErrors.LANGUAGE_NOT_SUPPORTED;
            }
        } finally {
            readLock.unlock();
        }
        return ValidationErrors.VALID;
    }

    public RequestValidationResponse createRequestValidationResponse(ValidationErrors code) {
        return new RequestValidationResponse(code.getStatusCode(), code.getEntity().toString());
    }

    public void updateLanguageCodes() {
        this.setLanguageCodes(languageLoader.loadLanguageCodes());

    }

    public ValidationErrors isDateValid(String dateToValidate, String dateFromat) {

        SimpleDateFormat sdf = new SimpleDateFormat(dateFromat);
        sdf.setLenient(false);
        try {
            sdf.parse(dateToValidate);
        } catch (ParseException e) {
            return ValidationErrors.INVALID_DATE_FORMAT;
        }
        return ValidationErrors.VALID;
    }

    public RequestValidationResponse partyValidation(List<AccountIdentifierBase> party, boolean isSenderValidations) {       
        for (AccountIdentifierBase acc : party) {
            if (acc.getKey().equals(AccountIdentifierBase.KeyEnum.msisdn)) {
                RequestValidationResponse response = validateMSISDN(isSenderValidations, acc.getValue());
                if (Optional.ofNullable(response).isPresent()) {
                    return response;
                }
            }
        }
        return null;
    }

    private RequestValidationResponse validateMSISDN(boolean isSenderValidations, String value) {
        ValidationErrors code = null;
        if (isSenderValidations) {
            code = validateSenderMsisdn(value, resource.getMsisdnLength());
        } else {
            code = validateReceiverMsisdn(value, resource.getMsisdnLength());
        }
        if (!code.equals(ValidationErrors.VALID)) {
            return createRequestValidationResponse(code);
        }
        return null;
    }

    public RequestValidationResponse validateSenderPIN(String pin) {
        ValidationErrors code = null;
            code = validatePin(pin, resource.getPinLength());
            if (!code.equals(ValidationErrors.VALID)) {
                return createRequestValidationResponse(code);
            }
        return null;
    }
    
    private boolean isValidTransactionType(String txnType) {

        for (TransactionTypes types : TransactionTypes.values()) {
            if (types.getTransactionType().equals(txnType)) {
                return true;
            }
        }

        return false;
    }
    
    public ValidationErrors validateProcessingCode(String processingCode) {
        if (isEmpty(processingCode)) {
            return ValidationErrors.PROCESSING_CODE_MISSING;
        }
        return ValidationErrors.VALID;
    }
    
    public ValidationErrors validateIsoAmount(String amount) {
        if (isEmpty(amount) || Long.parseLong(amount) == 0) {
            return ValidationErrors.INCORRECT_AMOUNT_FORMAT;
        }
        return ValidationErrors.VALID;
    }
    
    public ValidationErrors validateStan(String stan) {
        if (isEmpty(stan)) {
            return ValidationErrors.SYSTEMAUDITNUMBER_MISSING;
        }
        return ValidationErrors.VALID;
    }
    
    public ValidationErrors validateCountry(String country) {
        if (isEmpty(country)) {
            return ValidationErrors.COUNTRY_CODE_MISSING;
        }
        return ValidationErrors.VALID;
    }
    
    public ValidationErrors validateReferenceNumber(String referenceNumber) {
        if (isEmpty(referenceNumber)) {
            return ValidationErrors.REFERENCE_NUMBER_MISSING;
        }
        return ValidationErrors.VALID;
    }

	public ValidationErrors sourceAccountTypeValidation(String sourceAccountType) {
		if (isEmpty(sourceAccountType)) {
            return ValidationErrors.REQUEST_RESOURCE_MISSING;
        }

        if (!SourceAccountType.isPaymentAccoutType(sourceAccountType)) {
            return ValidationErrors.INVALID_REQUEST_RESOURCE;
        }
        return ValidationErrors.VALID;
	}

	public ValidationErrors requestSourceValidation(String requestSource) {
		if (isEmpty(requestSource)) {
            return ValidationErrors.SOURCE_ACCOUNT_TYPE_MISSING;
        }

        if (!RequestSource.isSource(requestSource)) {
            return ValidationErrors.INVALID_SOURCE_ACCOUNT_TYPE;
        }
        return ValidationErrors.VALID;
	}
	
	public ValidationErrors requestNewtorkActionValidation(String networkAction) {
		if (isEmpty(networkAction)) {
            return ValidationErrors.NETWORK_ACTION_MISSING;
        }
        return ValidationErrors.VALID;
	}
}
