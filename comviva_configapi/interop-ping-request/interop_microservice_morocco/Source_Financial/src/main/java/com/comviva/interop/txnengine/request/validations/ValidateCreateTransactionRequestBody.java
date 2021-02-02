package com.comviva.interop.txnengine.request.validations;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comviva.interop.txnengine.enums.SuccessStatus;
import com.comviva.interop.txnengine.enums.TransactionTypes;
import com.comviva.interop.txnengine.enums.ValidationErrors;
import com.comviva.interop.txnengine.model.AccountIdentifierBase;
import com.comviva.interop.txnengine.model.RequestValidationResponse;
import com.comviva.interop.txnengine.model.TransactionRequest;

@Service
public class ValidateCreateTransactionRequestBody {

    @Autowired
    private RequestValidations requestValidations;

    public RequestValidationResponse validate(TransactionRequest request) {
        ValidationErrors code = null;
        RequestValidationResponse response = null;

        code = requestValidations.validateLanguage(request.getLang());
        if (!code.equals(ValidationErrors.VALID)) {
            return requestValidations.createRequestValidationResponse(code);
        }

        code = requestValidations.validateExtOrgRefId(request.getExtOrgRefId());
        if (!code.equals(ValidationErrors.VALID)) {
            return requestValidations.createRequestValidationResponse(code);
        }

        code = requestValidations.validateAmount(request.getAmount());
        if (!code.equals(ValidationErrors.VALID)) {
            return requestValidations.createRequestValidationResponse(code);
        }
        code = requestValidations.validateCurrency(request.getCurrency());
        if (!code.equals(ValidationErrors.VALID)) {
            return requestValidations.createRequestValidationResponse(code);
        }
        code = requestValidations.validateTransactionType(request.getTransactionType());
        if (!code.equals(ValidationErrors.VALID)) {
            return requestValidations.createRequestValidationResponse(code);
        }
        
        if (!TransactionTypes.MERCHPAY_PULL.getTransactionType().equals(request.getTransactionType())) {
            response = requestValidations.validateSenderPIN(request.getDebitPartyCredentials().getPin());
            if (Optional.ofNullable(response).isPresent()) {
                return response;
            }
        }

        return supportValidate(request);
    }
    
    
    public RequestValidationResponse supportValidate(TransactionRequest request) {
    	ValidationErrors code = null;
        RequestValidationResponse response = null;
    	if (null == request.getDebitParty() || request.getDebitParty().isEmpty()) {
            return requestValidations.createRequestValidationResponse(ValidationErrors.SENDER_PARTY_DETAILS_MISSING);
        } else {
            RequestValidationResponse vlidationResponse = validateSenderRequiredDetails(request);
            if(Optional.ofNullable(vlidationResponse).isPresent()) {
                return vlidationResponse;
            }
        }

        if (null == request.getCreditParty() || request.getCreditParty().isEmpty()) {
            return requestValidations.createRequestValidationResponse(ValidationErrors.RECEIVER_PARTY_DETAILS_MISSING);
        }

        response = requestValidations.partyValidation(request.getDebitParty(), Boolean.TRUE);
        if (response != null) {
            return response;
        }
        response = requestValidations.partyValidation(request.getCreditParty(), Boolean.FALSE);
        if (response != null) {
            return response;
        }
        code = requestValidations.sourceAccountTypeValidation(request.getSourceAccountType());
        if (!code.equals(ValidationErrors.VALID)) {
            return requestValidations.createRequestValidationResponse(code);
        }
        code = requestValidations.requestSourceValidation(request.getRequestSource());
        if (!code.equals(ValidationErrors.VALID)) {
            return requestValidations.createRequestValidationResponse(code);
        }

        return new RequestValidationResponse(SuccessStatus.SUCCESSFUL_REQUEST_VALIDATION.getStatusCode(),
                SuccessStatus.SUCCESSFUL_REQUEST_VALIDATION.getEntity().toString());
    }
    
    

    private boolean isSenderRequiredDetailsExists(List<AccountIdentifierBase> party,
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
    
    private RequestValidationResponse validateSenderRequiredDetails(TransactionRequest request) {
        if (!isSenderRequiredDetailsExists(request.getDebitParty(), AccountIdentifierBase.KeyEnum.msisdn)) {
            return requestValidations.createRequestValidationResponse(ValidationErrors.SENDER_PARTY_MSISDN_MISSING);
        }
        return null;
    }

}
