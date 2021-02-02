package com.comviva.interop.txnengine.services;

import java.net.SocketTimeoutException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import com.comviva.interop.txnengine.configuration.BrokerServiceProperties;
import com.comviva.interop.txnengine.configuration.BrokerServiceURLProperties;
import com.comviva.interop.txnengine.entities.InteropTransactionDetails;
import com.comviva.interop.txnengine.entities.InteropTransactions;
import com.comviva.interop.txnengine.enums.Constants;
import com.comviva.interop.txnengine.enums.InteropResponseCodes;
import com.comviva.interop.txnengine.enums.LogConstants;
import com.comviva.interop.txnengine.enums.RequestType;
import com.comviva.interop.txnengine.enums.Sources;
import com.comviva.interop.txnengine.enums.ThirdPartyResponseCodes;
import com.comviva.interop.txnengine.enums.TransactionStatus;
import com.comviva.interop.txnengine.enums.TransactionSubTypes;
import com.comviva.interop.txnengine.enums.UnicId;
import com.comviva.interop.txnengine.events.CreateTransactionResponseEvent;
import com.comviva.interop.txnengine.mobiquity.service.handlers.P2PHandler;
import com.comviva.interop.txnengine.model.Request;
import com.comviva.interop.txnengine.model.Response;
import com.comviva.interop.txnengine.model.TransactionRequest;
import com.comviva.interop.txnengine.model.TransactionResponse;
import com.comviva.interop.txnengine.model.TxnMode;
import com.comviva.interop.txnengine.repositories.InteropTransactionDetailsRepository;
import com.comviva.interop.txnengine.repositories.InteropTransactionsRepository;
import com.comviva.interop.txnengine.request.validations.GetDescriptionForCode;
import com.comviva.interop.txnengine.util.CastUtils;
import com.comviva.interop.txnengine.util.LoggerUtil;
import com.comviva.interop.txnengine.util.TransactionDataPreparationUtil;

@Service
public class P2POnUsTransactionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(P2POnUsTransactionService.class);
    
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private GetDescriptionForCode getDescriptionForCode;

    @Autowired
    private P2PHandler mobiquityp2pHandler;

    @Autowired
    private InteropTransactionsRepository interOpTransactionRepository;

    @Autowired
    private InteropTransactionDetailsRepository interopTransactionDetailsRepository;

    @Autowired
    private BrokerServiceProperties thirdPartyProperties;
    
    @Autowired
    private BrokerServiceURLProperties brokerServiceURLProperties;

    @Async("ThirdPartyCallsAsyncPool")
    public void doP2POnUsTransaction(Request request, InteropTransactions interopTransaction) {
        TransactionRequest requestBody = CastUtils.toTransactionRequest(request.getRequestAttr());
        try {
        	interopTransaction.setTransactionSubType(TransactionSubTypes.P2P_ON_US.toString());
            TxnMode txnMode= new TxnMode(TransactionSubTypes.P2P_ON_US.toString(),brokerServiceURLProperties.getUrlCountryIdValue(),brokerServiceURLProperties.getUrlCurrencyValue(),
                    brokerServiceURLProperties.getUrlAddonIdValue(),request.getInteropReferenceId(),UnicId.ONE.getVal());
            InteropTransactionDetails txnDetails=TransactionDataPreparationUtil.prepareRequestTransactionDetails(interopTransaction, RequestType.CTMREQ.toString(),thirdPartyProperties, Constants.ZERO.getValue(), Constants.ZERO.getValue());
            txnDetails.setTxnMode(txnMode.toString());
            interopTransactionDetailsRepository.save(txnDetails);
            Response p2pResponse = mobiquityp2pHandler.execute(requestBody, request.getInteropReferenceId(),txnMode.toString(), requestBody.getDebitPartyCredentials().getEm());
            if (!Optional.ofNullable(p2pResponse.getMappingResponse()).isPresent()) {
                interOpTransactionRepository.save(TransactionDataPreparationUtil.updateTransactionStatus(
                        interopTransaction, TransactionStatus.TRANSACTION_AMBIGUOUS.getStatus()));
                interopTransactionDetailsRepository.save(TransactionDataPreparationUtil.prepareResponseTransactionDetails(
                        interopTransaction, RequestType.CTMRRESP.toString(), p2pResponse,thirdPartyProperties, Constants.ZERO.getValue(), Constants.ZERO.getValue()));
                this.publishResponseEvent(TransactionDataPreparationUtil.prepareTransactionResponse(interopTransaction,
                        InteropResponseCodes.READ_TIMEOUT_FROM_BROKER.getStatusCode(),
                        getDescriptionForCode.getMappingCode(InteropResponseCodes.READ_TIMEOUT_FROM_BROKER.getStatusCode()),
                        getDescriptionForCode.getDescription(Sources.BROKER.toString(),
                                InteropResponseCodes.READ_TIMEOUT_FROM_BROKER.getStatusCode(), requestBody.getLang())),
                        request.getInteropReferenceId(), requestBody, null);
            } else if (ThirdPartyResponseCodes.SUCCESS.getMappedCode()
                    .equals(p2pResponse.getMappingResponse().getMappingCode())) {
                interOpTransactionRepository.save(TransactionDataPreparationUtil.updateTransactionStatus(interopTransaction, TransactionStatus.TRANSACTION_SUCCESS.getStatus()));
                interopTransactionDetailsRepository.save(TransactionDataPreparationUtil.prepareResponseTransactionDetails(
                        interopTransaction, RequestType.CTMRRESP.toString(), p2pResponse,thirdPartyProperties, Constants.ZERO.getValue(), Constants.ZERO.getValue()));
                this.publishResponseEvent(TransactionDataPreparationUtil.prepareTransactionResponse(interopTransaction,
                        p2pResponse.getBrokerResponse().getBrokerCode(), p2pResponse.getMappingResponse().getMappingCode(),
                        p2pResponse.getWalletResponse().getMessage()), request.getInteropReferenceId(), requestBody, null);
            } else {
                interOpTransactionRepository.save(TransactionDataPreparationUtil.updateTransactionStatus(interopTransaction, TransactionStatus.TRANSACTION_FAIL.getStatus()));
                interopTransactionDetailsRepository.save(TransactionDataPreparationUtil.prepareResponseTransactionDetails(
                        interopTransaction, RequestType.CTMRRESP.toString(), p2pResponse,thirdPartyProperties, Constants.ZERO.getValue(), Constants.ZERO.getValue()));
                this.publishResponseEvent(TransactionDataPreparationUtil.prepareTransactionResponse(interopTransaction,
                        p2pResponse.getBrokerResponse().getBrokerCode(), p2pResponse.getMappingResponse().getMappingCode(),
                        p2pResponse.getWalletResponse().getMessage()), request.getInteropReferenceId(), requestBody, null);
            }
        
        } catch (RestClientException restException) {
            TransactionResponse transactionResponse = null;
            interOpTransactionRepository.save(TransactionDataPreparationUtil.updateTransactionStatus(interopTransaction,
                    TransactionStatus.TRANSACTION_FAIL.getStatus()));
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
                String description = getDescriptionForCode.getDescription(
                        InteropResponseCodes.NOT_ABLE_TO_CONNECT_THIRD_PARTY.getEntity().toString(),
                        InteropResponseCodes.NOT_ABLE_TO_CONNECT_THIRD_PARTY.getStatusCode(), requestBody.getLang());
                transactionResponse = new TransactionResponse(description,
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
            String description = getDescriptionForCode.getDescription(
                    InteropResponseCodes.INTERNAL_ERROR.getEntity().toString(),
                    InteropResponseCodes.INTERNAL_ERROR.getStatusCode(), requestBody.getLang());
            TransactionResponse transactionResponse = new TransactionResponse(description,
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
        LOGGER.info("Create Transaction  service response: {}", message);
        applicationEventPublisher.publishEvent(createTransactionResponseEvent);
    }
}
