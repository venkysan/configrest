package com.comviva.interop.txnengine.services;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.comviva.interop.txnengine.configuration.BrokerServiceURLProperties;
import com.comviva.interop.txnengine.configuration.Resource;
import com.comviva.interop.txnengine.entities.InteropTransactions;
import com.comviva.interop.txnengine.enums.ConfirmTransactionActionTypes;
import com.comviva.interop.txnengine.enums.InteropResponseCodes;
import com.comviva.interop.txnengine.enums.LogConstants;
import com.comviva.interop.txnengine.enums.TransactionStatus;
import com.comviva.interop.txnengine.enums.ValidationErrors;
import com.comviva.interop.txnengine.events.ConfirmTransactionResponseEvent;
import com.comviva.interop.txnengine.exception.InteropException;
import com.comviva.interop.txnengine.mobiquity.service.handlers.MerchantPaymentOneStepHandler;
import com.comviva.interop.txnengine.model.ConfirmTransactionRequest;
import com.comviva.interop.txnengine.model.ConfirmTransactionResponse;
import com.comviva.interop.txnengine.model.Request;
import com.comviva.interop.txnengine.repositories.InteropTransactionsRepository;
import com.comviva.interop.txnengine.request.validations.GetDescriptionForCode;
import com.comviva.interop.txnengine.request.validations.RequestValidations;
import com.comviva.interop.txnengine.util.CastUtils;
import com.comviva.interop.txnengine.util.LoggerUtil;
import com.comviva.interop.txnengine.util.TransactionDataPreparationUtil;

@Service("ConfirmTransactionService")
public class ConfirmTransactionService implements ExecutableServices {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfirmTransactionService.class);
    
    @Autowired
    private RequestValidations requestValidations;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private GetDescriptionForCode getDescriptionForCode;
    
    @Autowired
    private BrokerServiceURLProperties brokerServiceURLProperties;
    
    @PersistenceContext
    @Autowired
    private EntityManager entityManager;
    
    @Autowired
    private MerchantPaymentOneStepHandler merchantPaymentOneStepHandler;
    
    @Autowired
    private InteropTransactionsRepository interopTransactionsRepository;
    
    @Autowired
    private Resource resource;

    @Override
    @Async
    public void execute(Request request) {
        ConfirmTransactionRequest requestBody = CastUtils.toConfirmTransactionRequest(request.getRequestAttr());
        try {
        	 validateRequest(requestBody);
        	 InteropTransactions interopTransaction = interopTransactionsRepository.findInteropTransactionsByTxnId(requestBody.getInteropRefId());
        	 if(!Optional.ofNullable(interopTransaction).isPresent()) {
        		 throw new InteropException(ValidationErrors.INVALID_TRANSACTION_ID.getStatusCode(), ValidationErrors.INVALID_TRANSACTION_ID.getEntity().toString());
        	 }
        	 if(ConfirmTransactionActionTypes.ACCEPT.getActionType().equals(requestBody.getAction().getValue())) {
        		 merchantPaymentOneStepHandler.callBrokerForMerchantPayment(interopTransaction, requestBody, request.getInteropReferenceId());
        	 }
        	 else if(ConfirmTransactionActionTypes.REJECT.getActionType().equals(requestBody.getAction().getValue())) {
        		 interopTransactionsRepository.save(TransactionDataPreparationUtil.updateTransactionStatus
        				 (interopTransaction, TransactionStatus.TRANSACTION_REJECTED.getStatus()));
        		 String description = getDescriptionForCode.getDescription(ValidationErrors.TXN_REJECTED.getEntity().toString(), ValidationErrors.TXN_REJECTED.getStatusCode(),
                         requestBody.getLang());
        		 ConfirmTransactionResponse transactionResponse = new ConfirmTransactionResponse(description,
                         CastUtils.joinStatusCode(ValidationErrors.TXN_REJECTED.getEntity().toString(), ValidationErrors.TXN_REJECTED.getStatusCode()),
                                  getDescriptionForCode.getMappingCode(ValidationErrors.TXN_REJECTED.getStatusCode()));
                 this.publishResponseEvent(transactionResponse, request.getInteropReferenceId(), requestBody, null);
        	 }
        } catch (InteropException e) {
            String description = getDescriptionForCode.getDescription(e.getEntity(), e.getStatusCode(),
                    requestBody.getLang());
            ConfirmTransactionResponse transactionResponse = new ConfirmTransactionResponse(description,
                    CastUtils.joinStatusCode(e.getEntity(), e.getStatusCode()),
                    Optional.ofNullable(e.getMappedCode()).isPresent() ? e.getMappedCode()
                            : getDescriptionForCode.getMappingCode(e.getStatusCode()));
            this.publishResponseEvent(transactionResponse, request.getInteropReferenceId(), requestBody, e);
        } catch (Exception ex) {
            String description = getDescriptionForCode.getDescription(
                    InteropResponseCodes.INTERNAL_ERROR.getEntity().toString(),
                    InteropResponseCodes.INTERNAL_ERROR.getStatusCode(), requestBody.getLang());
            ConfirmTransactionResponse transactionResponse = new ConfirmTransactionResponse(description,
                    CastUtils.joinStatusCode(InteropResponseCodes.INTERNAL_ERROR.getEntity().toString(),
                            InteropResponseCodes.INTERNAL_ERROR.getStatusCode()),
                    getDescriptionForCode.getMappingCode(InteropResponseCodes.INTERNAL_ERROR.getStatusCode()));
            this.publishResponseEvent(transactionResponse, request.getInteropReferenceId(), requestBody, ex);
        }
    }
    
    private void validateRequest(ConfirmTransactionRequest requestBody) {
    	ValidationErrors validationResponse = null;
    	validationResponse = requestValidations.validateInteropreferenceid(requestBody.getInteropRefId());
   	 	if (!ValidationErrors.VALID.getStatusCode().equals(validationResponse.getStatusCode())) {
            throw new InteropException(validationResponse.getStatusCode(), validationResponse.getEntity().toString());
        }
   	 
   	 	validationResponse = requestValidations.validateLanguage(requestBody.getLang());
        if (!ValidationErrors.VALID.getStatusCode().equals(validationResponse.getStatusCode())) {
            throw new InteropException(validationResponse.getStatusCode(), validationResponse.getEntity().toString());
        }
        
        if (null == requestBody.getAction()) {
            throw new InteropException(ValidationErrors.INVALID_ACTION_TYPE.getStatusCode(), ValidationErrors.INVALID_ACTION_TYPE.getEntity().toString());
        }
        
        validationResponse  = requestValidations.validatePin(requestBody.getPin(), resource.getPinLength());
        if (!ValidationErrors.VALID.getStatusCode().equals(validationResponse.getStatusCode())) {
            throw new InteropException(validationResponse.getStatusCode(), validationResponse.getEntity().toString());
        }
    }

    private void publishResponseEvent(ConfirmTransactionResponse transactionResponse, String reqId, ConfirmTransactionRequest req, Exception ex) {
        String message = LoggerUtil.prepareLogDetailForConfirmTxnResponse(req, brokerServiceURLProperties.getUrlCountryIdValue(),
                reqId, LogConstants.OUTGOING_RESPONSE_EVENT_TYPE.getValue(),  transactionResponse, ex);
        LOGGER.info("Confirm Transactions service response: {}", message);
        applicationEventPublisher.publishEvent(new ConfirmTransactionResponseEvent(this, transactionResponse, reqId));
    }
}
