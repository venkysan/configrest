package com.comviva.interop.txnengine.mobiquity.service.handlers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.comviva.interop.txnengine.configuration.BrokerServiceProperties;
import com.comviva.interop.txnengine.configuration.BrokerServiceURLProperties;
import com.comviva.interop.txnengine.configuration.Resource;
import com.comviva.interop.txnengine.configuration.ServiceTemplateNames;
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
import com.comviva.interop.txnengine.enums.TransactionTypes;
import com.comviva.interop.txnengine.enums.UnicId;
import com.comviva.interop.txnengine.events.CreateTransactionResponseEvent;
import com.comviva.interop.txnengine.model.Request;
import com.comviva.interop.txnengine.model.Response;
import com.comviva.interop.txnengine.model.TransactionRequest;
import com.comviva.interop.txnengine.model.TransactionResponse;
import com.comviva.interop.txnengine.model.TxnMode;
import com.comviva.interop.txnengine.repositories.InteropTransactionDetailsRepository;
import com.comviva.interop.txnengine.repositories.InteropTransactionsRepository;
import com.comviva.interop.txnengine.request.validations.GetDescriptionForCode;
import com.comviva.interop.txnengine.util.LoggerUtil;
import com.comviva.interop.txnengine.util.MobiquityConst;
import com.comviva.interop.txnengine.util.StringUtils;
import com.comviva.interop.txnengine.util.ThirdPartyCaller;
import com.comviva.interop.txnengine.util.TransactionDataPreparationUtil;

@Service
public class CashOutInitHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CashOutInitHandler.class);
    
    @Autowired
    private ThirdPartyCaller thirdPartyCaller;

    @Autowired
    private InteropTransactionDetailsRepository interopTransactionDetailsRepository;
   
    @Autowired
    private InteropTransactionsRepository interOpTransactionRepository;

    @Autowired
    private CashOutConfirmHandler cashOutConfirmHandler;
    
    @Autowired
    private GetDescriptionForCode getDescriptionForCode;

    
    @Autowired
    private BrokerServiceProperties thirdPartyProperties;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    
    @Autowired
    private ServiceTemplateNames serviceTemplateNames;
    
    @Autowired
    private BrokerServiceURLProperties brokerServiceURLProperties;
    
    @Autowired
    private Resource resource;


    public void doCashOutInit(Request request, InteropTransactions interopTransaction, TransactionRequest requestBody) {
        InteropTransactionDetails txnDetails=TransactionDataPreparationUtil.prepareRequestTransactionDetails(interopTransaction, RequestType.RCOREQ.toString(),thirdPartyProperties,Constants.ZERO.getValue(), Constants.ONE.getValue());
        TxnMode txnMode= new TxnMode(TransactionTypes.P2P.getTransactionType().equals(requestBody.getTransactionType()) ? TransactionSubTypes.P2P_OFF_US.toString() : TransactionSubTypes.MP_OFF_US.toString(),brokerServiceURLProperties.getUrlCountryIdValue(),brokerServiceURLProperties.getUrlCurrencyValue(),
                brokerServiceURLProperties.getUrlAddonIdValue(),request.getInteropReferenceId(),UnicId.ONE.getVal());
        txnDetails.setTxnMode(txnMode.toString());
        interopTransactionDetailsRepository.save(txnDetails);
        Response cashOutInitResponse = execute(requestBody.getDebitParty().get(0).getValue(), requestBody.getAmount(),request.getInteropReferenceId(),txnMode.toString());
        if (!Optional.ofNullable(cashOutInitResponse.getMappingResponse()).isPresent()) {
            interOpTransactionRepository.save(TransactionDataPreparationUtil.updateTransactionStatus(interopTransaction,TransactionStatus.TRANSACTION_FAIL.getStatus()));
            interopTransactionDetailsRepository.save(TransactionDataPreparationUtil.prepareResponseTransactionDetails(
                    interopTransaction, RequestType.RCORESP.toString(), cashOutInitResponse,thirdPartyProperties,Constants.ZERO.getValue(), Constants.ONE.getValue()));
            this.publishResponseEvent(TransactionDataPreparationUtil.prepareTransactionResponse(interopTransaction,
                    InteropResponseCodes.READ_TIMEOUT_FROM_BROKER.getStatusCode(),
                    getDescriptionForCode.getMappingCode(InteropResponseCodes.READ_TIMEOUT_FROM_BROKER.getStatusCode()),
                    getDescriptionForCode.getDescription(Sources.BROKER.toString(),
                            InteropResponseCodes.READ_TIMEOUT_FROM_BROKER.getStatusCode(), requestBody.getLang())),
                    request.getInteropReferenceId(), requestBody, null);
        } else if (ThirdPartyResponseCodes.SUCCESS.getMappedCode()
                .equals(cashOutInitResponse.getMappingResponse().getMappingCode())) {
        	interopTransaction.setMobiquityTransactionId(cashOutInitResponse.getWalletResponse().getTxnid());
        	interOpTransactionRepository.save(interopTransaction);
            interopTransactionDetailsRepository.save(TransactionDataPreparationUtil.prepareResponseTransactionDetails(
                    interopTransaction, RequestType.RCORESP.toString(), cashOutInitResponse,thirdPartyProperties,Constants.ZERO.getValue(), Constants.ONE.getValue()));
            cashOutConfirmHandler.doCashOutConfirm(interopTransaction, requestBody, request,cashOutInitResponse.getWalletResponse().getTxnid(),txnMode.toString());
        } else {
            interOpTransactionRepository.save(TransactionDataPreparationUtil.updateTransactionStatus(interopTransaction,
                    TransactionStatus.TRANSACTION_FAIL.getStatus()));
            interopTransactionDetailsRepository.save(TransactionDataPreparationUtil.prepareResponseTransactionDetails(
                    interopTransaction, RequestType.RCORESP.toString(), cashOutInitResponse,thirdPartyProperties,Constants.ZERO.getValue(), Constants.ONE.getValue()));
            this.publishResponseEvent(TransactionDataPreparationUtil.prepareTransactionResponse(interopTransaction,
                    cashOutInitResponse.getBrokerResponse().getBrokerCode(),
                    cashOutInitResponse.getMappingResponse().getMappingCode(),
                    cashOutInitResponse.getWalletResponse().getMessage()), request.getInteropReferenceId(), requestBody, null);
        }
    }

    public Response execute(String msisdn, String amount, String interopReferenceId,String txnMode) {
        Map<String, String> cashOutInit = new HashMap<>();
        cashOutInit.put(MobiquityConst.SESSION_ID.getValue(), interopReferenceId);
        cashOutInit.put(MobiquityConst.TXNMODE.getValue(), txnMode);
        cashOutInit.put(MobiquityConst.MSISDN.getValue(), thirdPartyProperties.getChannelUserMsisdn());
        cashOutInit.put(MobiquityConst.MSISDN2.getValue(), msisdn);
        cashOutInit.put(MobiquityConst.AMOUNT.getValue(), amount);
        cashOutInit.put(MobiquityConst.PROVIDER.getValue(), thirdPartyProperties.getPayeeProviderId());
        cashOutInit.put(MobiquityConst.PROVIDER2.getValue(), thirdPartyProperties.getPayerProviderId());
        cashOutInit.put(MobiquityConst.CONNECTOR.getValue(), thirdPartyProperties.getConnector());
        return thirdPartyCaller.postMobiquityServiceRequest(cashOutInit,
                serviceTemplateNames.getCashOutInitRequestTemplate(), getWalletGetCashOutUrl(), interopReferenceId, LogConstants.CASHOUT_INIT.getValue());
    }

    private String getWalletGetCashOutUrl() {
        Object[] urlArgs = { resource.getWalletBaseUrl(), brokerServiceURLProperties.getUrlAddonIdValue(),
                brokerServiceURLProperties.getUrlCountryIdValue(), brokerServiceURLProperties.getUrlCurrencyValue() };
        return StringUtils.msgFormat(brokerServiceURLProperties.getCashOutInitUrl(), urlArgs);
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
