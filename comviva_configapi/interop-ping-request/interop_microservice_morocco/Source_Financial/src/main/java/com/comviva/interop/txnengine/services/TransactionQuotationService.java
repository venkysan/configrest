package com.comviva.interop.txnengine.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.comviva.interop.txnengine.configuration.BrokerServiceURLProperties;
import com.comviva.interop.txnengine.enums.InteropResponseCodes;
import com.comviva.interop.txnengine.enums.LogConstants;
import com.comviva.interop.txnengine.enums.SuccessStatus;
import com.comviva.interop.txnengine.events.TransactionQuotationResponseEvent;
import com.comviva.interop.txnengine.exception.InteropException;
import com.comviva.interop.txnengine.mobiquity.service.handlers.GetFeeHandler;
import com.comviva.interop.txnengine.model.QuotationRequest;
import com.comviva.interop.txnengine.model.QuotationResponse;
import com.comviva.interop.txnengine.model.Request;
import com.comviva.interop.txnengine.model.RequestValidationResponse;
import com.comviva.interop.txnengine.request.validations.GetDescriptionForCode;
import com.comviva.interop.txnengine.request.validations.ValidateTransactionQuotationRequestBody;
import com.comviva.interop.txnengine.util.CastUtils;
import com.comviva.interop.txnengine.util.LoggerUtil;

@Service("TransactionQuotationService")
public class TransactionQuotationService implements ExecutableServices {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionQuotationService.class);
    
    @Autowired
    private ValidateTransactionQuotationRequestBody requestValidations;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private GetDescriptionForCode getDescriptionForCode;

    @Autowired
    private GetDefaultWalletStatusHandler nonFinGetUserHandler;

    @Autowired
    private GetFeeHandler mobiquityGetFeeHandler;
    
    @Autowired
    private BrokerServiceURLProperties brokerServiceURLProperties;

    @Override
    @Async
    public void execute(Request request) {
        boolean isP2POnUs = false;
        QuotationRequest requestBody = CastUtils.toTransactionQuotationRequest(request.getRequestAttr());
        try {
            RequestValidationResponse requestValidationResponse = requestValidations.validate(requestBody);
            if (!requestValidationResponse.getStatusCode()
                    .equals(SuccessStatus.SUCCESSFUL_REQUEST_VALIDATION.getStatusCode())) {
                throw new InteropException(requestValidationResponse.getStatusCode(),
                        requestValidationResponse.getEntity());
            }
            isP2POnUs = nonFinGetUserHandler.isUserRegisteredWithSameMFS(requestBody.getDebitParty().get(0).getValue(), requestBody.getCreditParty().get(0).getValue(), 
            		request.getInteropReferenceId(), requestBody.getTransactionType());
            mobiquityGetFeeHandler.execute(isP2POnUs, requestBody, request.getInteropReferenceId());
        } catch (InteropException e) {
            String description = getDescriptionForCode.getDescription(e.getEntity(), e.getStatusCode(),
                    requestBody.getLang());
            QuotationResponse quotationResponse = new QuotationResponse(description, CastUtils.joinStatusCode(e.getEntity(), e.getStatusCode()),
                    getDescriptionForCode.getMappingCode(e.getStatusCode()));
            this.publishResponseEvent(quotationResponse, request.getInteropReferenceId(), requestBody, e);
        } catch (Exception e) {
            QuotationResponse quotationResponse = new QuotationResponse(
                    getDescriptionForCode.getDescription(InteropResponseCodes.INTERNAL_ERROR.getEntity().toString(),
                            InteropResponseCodes.INTERNAL_ERROR.getStatusCode(), requestBody.getLang()),
                    CastUtils.joinStatusCode(InteropResponseCodes.INTERNAL_ERROR.getEntity().toString(), InteropResponseCodes.INTERNAL_ERROR.getStatusCode()),
                    getDescriptionForCode.getMappingCode(InteropResponseCodes.INTERNAL_ERROR.getStatusCode()));
            quotationResponse.setMappedCode(
                    getDescriptionForCode.getMappingCode(InteropResponseCodes.INTERNAL_ERROR.getStatusCode()));
            this.publishResponseEvent(quotationResponse, request.getInteropReferenceId(), requestBody, e);
        }
    }

    private void publishResponseEvent(QuotationResponse quotationResponse, String reqId, QuotationRequest req, Exception exception) {
        TransactionQuotationResponseEvent initiateEnrolmentResponseEvent = new TransactionQuotationResponseEvent(this,
                quotationResponse, reqId);
        String message = LoggerUtil.prepareLogDetailForQuotationResponse(req, brokerServiceURLProperties.getUrlCountryIdValue(), 
                reqId, LogConstants.OUTGOING_RESPONSE_EVENT_TYPE.getValue(),  quotationResponse, exception);
        LOGGER.info("Transaction quotation  service response: {}", message);
        applicationEventPublisher.publishEvent(initiateEnrolmentResponseEvent);
    }
}
