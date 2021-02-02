package com.comviva.interop.txnengine.services;

import java.net.SocketTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import com.comviva.interop.txnengine.configuration.BrokerServiceURLProperties;
import com.comviva.interop.txnengine.entities.InteropTransactions;
import com.comviva.interop.txnengine.enums.InteropResponseCodes;
import com.comviva.interop.txnengine.enums.LogConstants;
import com.comviva.interop.txnengine.enums.TransactionStatus;
import com.comviva.interop.txnengine.enums.TransactionSubTypes;
import com.comviva.interop.txnengine.events.CreateTransactionResponseEvent;
import com.comviva.interop.txnengine.exception.InteropException;
import com.comviva.interop.txnengine.mobiquity.service.handlers.MerchantPaymentInitHandler;
import com.comviva.interop.txnengine.model.Request;
import com.comviva.interop.txnengine.model.TransactionRequest;
import com.comviva.interop.txnengine.model.TransactionResponse;
import com.comviva.interop.txnengine.repositories.InteropTransactionsRepository;
import com.comviva.interop.txnengine.request.validations.GetDescriptionForCode;
import com.comviva.interop.txnengine.util.CastUtils;
import com.comviva.interop.txnengine.util.LoggerUtil;
import com.comviva.interop.txnengine.util.TransactionDataPreparationUtil;

@Service
public class MPOnUsTransactionService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MerchantPaymentInitHandler.class);

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private GetDescriptionForCode getDescriptionForCode;

    @Autowired
    private MerchantPaymentInitHandler merchantPaymentHandler;

    @Autowired
    private InteropTransactionsRepository interOpTransactionRepository;
    
    @Autowired
    private BrokerServiceURLProperties brokerServiceURLProperties;

    
    @Async("ThirdPartyCallsAsyncPool")
    public void doMPOnUsTransaction(Request request, InteropTransactions interopTransaction) {
        TransactionRequest requestBody = CastUtils.toTransactionRequest(request.getRequestAttr());
        try {
        	interopTransaction.setTransactionSubType(TransactionSubTypes.MP_ON_US.toString());
            merchantPaymentHandler.callBrokerForMerchantPaymentInit(request, interopTransaction, requestBody);
        } catch (InteropException exception) {
            TransactionResponse transactionResponse = new TransactionResponse(getDescriptionForCode.getDescription(exception.getEntity(), exception.getStatusCode(),
                    requestBody.getLang()),
                    CastUtils.joinStatusCode(exception.getEntity(), exception.getStatusCode()),
                    getDescriptionForCode.getMappingCode(exception.getStatusCode()));
            this.publishResponseEvent(transactionResponse, request.getInteropReferenceId(), requestBody, exception);
        } catch (RestClientException restException) {
            interOpTransactionRepository.save(TransactionDataPreparationUtil.updateTransactionStatus(interopTransaction,
                    TransactionStatus.TRANSACTION_FAIL.getStatus()));
            TransactionResponse transactionResponse = null;
            if(restException.getCause() instanceof SocketTimeoutException) {
                String description = getDescriptionForCode.getDescription(
                        InteropResponseCodes.READ_TIMEOUT_FROM_BROKER.getEntity().toString(),
                        InteropResponseCodes.READ_TIMEOUT_FROM_BROKER.getStatusCode(), requestBody.getLang());
                transactionResponse = new TransactionResponse(description,
                        CastUtils.joinStatusCode(
                                InteropResponseCodes.READ_TIMEOUT_FROM_BROKER.getEntity().toString(),
                                InteropResponseCodes.READ_TIMEOUT_FROM_BROKER.getStatusCode()),
                        getDescriptionForCode
                                .getMappingCode(InteropResponseCodes.READ_TIMEOUT_FROM_BROKER.getStatusCode()));
            }
            else {
            	 transactionResponse = new TransactionResponse(getDescriptionForCode.getDescription(
                         InteropResponseCodes.NOT_ABLE_TO_CONNECT_THIRD_PARTY.getEntity().toString(),
                         InteropResponseCodes.NOT_ABLE_TO_CONNECT_THIRD_PARTY.getStatusCode(), requestBody.getLang()),
                         CastUtils.joinStatusCode(
                                 InteropResponseCodes.NOT_ABLE_TO_CONNECT_THIRD_PARTY.getEntity().toString(),
                                 InteropResponseCodes.NOT_ABLE_TO_CONNECT_THIRD_PARTY.getStatusCode()),
                         getDescriptionForCode
                                 .getMappingCode(InteropResponseCodes.NOT_ABLE_TO_CONNECT_THIRD_PARTY.getStatusCode()));   
            }
            this.publishResponseEvent(transactionResponse, request.getInteropReferenceId(), requestBody, restException);
        } catch (Exception e) {
        	 interOpTransactionRepository.save(TransactionDataPreparationUtil.updateTransactionStatus(interopTransaction,
                     TransactionStatus.TRANSACTION_FAIL.getStatus()));
            TransactionResponse transactionResponse = new TransactionResponse(getDescriptionForCode.getDescription(
                    InteropResponseCodes.INTERNAL_ERROR.getEntity().toString(),
                    InteropResponseCodes.INTERNAL_ERROR.getStatusCode(), requestBody.getLang()),
                    CastUtils.joinStatusCode(InteropResponseCodes.INTERNAL_ERROR.getEntity().toString(),
                            InteropResponseCodes.INTERNAL_ERROR.getStatusCode()),
                    getDescriptionForCode.getMappingCode(InteropResponseCodes.INTERNAL_ERROR.getStatusCode()));
            this.publishResponseEvent(transactionResponse, request.getInteropReferenceId(), requestBody, e);
        }
    }

    private void publishResponseEvent(TransactionResponse transactionResponse, String reqId, TransactionRequest req, Exception ex) {
        CreateTransactionResponseEvent createTransactionResponseEvent = new CreateTransactionResponseEvent(this,
                transactionResponse, reqId);
        String message = LoggerUtil.prepareLogDetailForCreateTransactionResponse(req, brokerServiceURLProperties.getUrlCountryIdValue(),
                reqId, LogConstants.OUTGOING_RESPONSE_EVENT_TYPE.getValue(),  transactionResponse, ex);
        LOGGER.info("Create Transaction Merchant Payment On Us service response: {}", message);
        applicationEventPublisher.publishEvent(createTransactionResponseEvent);
    }
}
