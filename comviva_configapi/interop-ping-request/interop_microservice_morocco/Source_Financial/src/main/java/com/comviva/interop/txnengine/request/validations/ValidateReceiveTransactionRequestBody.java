package com.comviva.interop.txnengine.request.validations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comviva.interop.txnengine.configuration.Resource;
import com.comviva.interop.txnengine.enums.SuccessStatus;
import com.comviva.interop.txnengine.enums.ValidationErrors;
import com.comviva.interop.txnengine.model.ReceiveTransactionRequest;
import com.comviva.interop.txnengine.model.RequestValidationResponse;

@Service
public class ValidateReceiveTransactionRequestBody {

    @Autowired
    private RequestValidations requestValidations;
    
    @Autowired
    private Resource resource;

    public RequestValidationResponse validate(ReceiveTransactionRequest request) {
        ValidationErrors code = null;

     /*   code = requestValidations.validateSenderMsisdn(request.getPan(), resource.getMsisdnLength());
        if (!code.equals(ValidationErrors.VALID)) {
            return requestValidations.createRequestValidationResponse(code);
        }*/
        code = requestValidations.validateProcessingCode(request.getProcessingCode());
        if (!code.equals(ValidationErrors.VALID)) {
            return requestValidations.createRequestValidationResponse(code);
        }
        code = requestValidations.validateIsoAmount(request.getTransactionAmount());
        if (!code.equals(ValidationErrors.VALID)) {
            return requestValidations.createRequestValidationResponse(code);
        }
        code = requestValidations.validateStan(request.getSystemAuditNumber());
        if (!code.equals(ValidationErrors.VALID)) {
            return requestValidations.createRequestValidationResponse(code);
        }
        code = requestValidations.validateCurrency(request.getCurrencyCodeOfTheTransaction());
        if (!code.equals(ValidationErrors.VALID)) {
            return requestValidations.createRequestValidationResponse(code);
        }
        code = requestValidations.validateCountry(request.getCountryCodeOftheSenderOrganization());
        if (!code.equals(ValidationErrors.VALID)) {
            return requestValidations.createRequestValidationResponse(code);
        }
        code = requestValidations.validateReceiverMsisdn(request.getSourceAccountNumber(), resource.getMsisdnLength());
        if (!code.equals(ValidationErrors.VALID)) {
            return requestValidations.createRequestValidationResponse(code);
        }
        code = requestValidations.validateSenderMsisdn(request.getDestinationAccountNumber(), resource.getMsisdnLength());
        if (!code.equals(ValidationErrors.VALID)) {
            return requestValidations.createRequestValidationResponse(code);
        }
        code = requestValidations.validateReferenceNumber(request.getReferenceNumberOfTheRecovery());
        if (!code.equals(ValidationErrors.VALID)) {
            return requestValidations.createRequestValidationResponse(code);
        }
        return new RequestValidationResponse(SuccessStatus.SUCCESSFUL_REQUEST_VALIDATION.getStatusCode(),
                SuccessStatus.SUCCESSFUL_REQUEST_VALIDATION.getEntity().toString());
    }
}
