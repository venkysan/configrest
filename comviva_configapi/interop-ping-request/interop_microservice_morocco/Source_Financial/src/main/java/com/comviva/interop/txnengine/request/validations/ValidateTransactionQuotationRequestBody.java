package com.comviva.interop.txnengine.request.validations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comviva.interop.txnengine.enums.SuccessStatus;
import com.comviva.interop.txnengine.enums.ValidationErrors;
import com.comviva.interop.txnengine.model.QuotationRequest;
import com.comviva.interop.txnengine.model.RequestValidationResponse;

@Service
public class ValidateTransactionQuotationRequestBody {

    @Autowired
    private RequestValidations requestValidations;

    public RequestValidationResponse validate(QuotationRequest request) {
        ValidationErrors code = null;
        RequestValidationResponse response = null;
        code = requestValidations.validateLanguage(request.getLang());
        if (!code.equals(ValidationErrors.VALID)) {
            return requestValidations.createRequestValidationResponse(code);
        }
        
        code = requestValidations.validateTransactionType(request.getTransactionType());
        if (!code.equals(ValidationErrors.VALID)) {
            return requestValidations.createRequestValidationResponse(code);
        }
       
        code = requestValidations.validateCurrency(request.getCurrency());
        if (!code.equals(ValidationErrors.VALID)) {
            return requestValidations.createRequestValidationResponse(code);
        }
        
        code = requestValidations.validateAmount(request.getAmount());
        if (!code.equals(ValidationErrors.VALID)) {
            return requestValidations.createRequestValidationResponse(code);
        }
        
        
        response = requestValidations.partyValidation(request.getDebitParty(), Boolean.TRUE);
        if (response != null) {
            return response;
        }
        response = requestValidations.partyValidation(request.getCreditParty(), Boolean.FALSE);
        if (response != null) {
            return response;
        }
        return new RequestValidationResponse(SuccessStatus.SUCCESSFUL_REQUEST_VALIDATION.getStatusCode(),
                SuccessStatus.SUCCESSFUL_REQUEST_VALIDATION.getEntity().toString());
    }
}
