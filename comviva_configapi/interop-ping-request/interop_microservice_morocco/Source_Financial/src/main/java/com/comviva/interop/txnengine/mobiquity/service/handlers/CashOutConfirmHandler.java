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
import com.comviva.interop.txnengine.configuration.SMSServerProperties;
import com.comviva.interop.txnengine.configuration.ServiceTemplateNames;
import com.comviva.interop.txnengine.entities.InteropTransactionDetails;
import com.comviva.interop.txnengine.entities.InteropTransactions;
import com.comviva.interop.txnengine.entities.SmsTemplates;
import com.comviva.interop.txnengine.enums.Constants;
import com.comviva.interop.txnengine.enums.InteropResponseCodes;
import com.comviva.interop.txnengine.enums.LogConstants;
import com.comviva.interop.txnengine.enums.RequestType;
import com.comviva.interop.txnengine.enums.SMSNotificationCodes;
import com.comviva.interop.txnengine.enums.Sources;
import com.comviva.interop.txnengine.enums.ThirdPartyResponseCodes;
import com.comviva.interop.txnengine.enums.TransactionStatus;
import com.comviva.interop.txnengine.events.CreateTransactionResponseEvent;
import com.comviva.interop.txnengine.model.Request;
import com.comviva.interop.txnengine.model.Response;
import com.comviva.interop.txnengine.model.TransactionRequest;
import com.comviva.interop.txnengine.model.TransactionResponse;
import com.comviva.interop.txnengine.repositories.InteropTransactionDetailsRepository;
import com.comviva.interop.txnengine.repositories.InteropTransactionsRepository;
import com.comviva.interop.txnengine.repositories.SmsDeliveryRepository;
import com.comviva.interop.txnengine.repositories.SmsTemplatesRepository;
import com.comviva.interop.txnengine.request.validations.GetDescriptionForCode;
import com.comviva.interop.txnengine.services.OffUsTransactionHPSHandler;
import com.comviva.interop.txnengine.util.CastUtils;
import com.comviva.interop.txnengine.util.LoggerUtil;
import com.comviva.interop.txnengine.util.MobiquityConst;
import com.comviva.interop.txnengine.util.SMSUtility;
import com.comviva.interop.txnengine.util.StringUtils;
import com.comviva.interop.txnengine.util.ThirdPartyCaller;
import com.comviva.interop.txnengine.util.TransactionDataPreparationUtil;

@Service
public class CashOutConfirmHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CashOutConfirmHandler.class);
    
    @Autowired
    private BrokerServiceProperties thirdPartyProperties;

    @Autowired
    private ThirdPartyCaller thirdPartyCaller;

    @Autowired
    private GetDescriptionForCode getDescriptionForCode;

    @Autowired
    private InteropTransactionDetailsRepository interopTransactionDetailsRepository;

    @Autowired
    private InteropTransactionsRepository interOpTransactionRepository;

    @Autowired
    private SmsTemplatesRepository smsTemplatesRepository;

    @Autowired
    private SmsDeliveryRepository smsDeliveryRepository;

    @Autowired
    private SMSServerProperties smsServerProperties;

    @Autowired
    private OffUsTransactionHPSHandler transactionRequestHPSHandler;
    
    @Autowired
    private BrokerServiceURLProperties brokerServiceURLProperties;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    
    @Autowired
    private ServiceTemplateNames serviceTemplateNames;
    
    @Autowired
    private Resource resource;
    
    
    public void doCashOutConfirm(InteropTransactions interopTransaction, TransactionRequest requestBody,Request request, String txnId,String txnMode) {
    	InteropTransactionDetails txnDetails=TransactionDataPreparationUtil.prepareRequestTransactionDetails(interopTransaction, RequestType.RCORREQ.toString(),thirdPartyProperties,Constants.ZERO.getValue(), Constants.ONE.getValue());
    	txnDetails.setTxnMode(txnMode);
        interopTransactionDetailsRepository.save(txnDetails);
        Response cashOutConfirmResponse = execute(requestBody.getAmount(), txnId,
        		thirdPartyProperties.getConfirmStatus(), request.getInteropReferenceId(),txnMode,
        		requestBody.getDebitPartyCredentials().getPin(), requestBody.getDebitPartyCredentials().getEm(), interopTransaction.getPayerMsisdn());
        if (!Optional.ofNullable(cashOutConfirmResponse.getMappingResponse()).isPresent()) {
            interOpTransactionRepository.save(TransactionDataPreparationUtil.updateTransactionStatus(interopTransaction,
                    TransactionStatus.TRANSACTION_AMBIGUOUS.getStatus()));
            interopTransactionDetailsRepository.save(TransactionDataPreparationUtil.prepareResponseTransactionDetails(
                    interopTransaction, RequestType.RCORESP.toString(), cashOutConfirmResponse,thirdPartyProperties,Constants.ZERO.getValue(), Constants.ONE.getValue()));
            this.publishResponseEvent(TransactionDataPreparationUtil.prepareTransactionResponse(interopTransaction,
                    InteropResponseCodes.READ_TIMEOUT_FROM_BROKER.getStatusCode(),
                    getDescriptionForCode.getMappingCode(InteropResponseCodes.READ_TIMEOUT_FROM_BROKER.getStatusCode()),
                    getDescriptionForCode.getDescription(Sources.BROKER.toString(),
                            InteropResponseCodes.READ_TIMEOUT_FROM_BROKER.getStatusCode(), requestBody.getLang())),
                    request.getInteropReferenceId(), requestBody, null);            
        } else if (ThirdPartyResponseCodes.SUCCESS.getMappedCode()
                .equals(cashOutConfirmResponse.getMappingResponse().getMappingCode())) {
            interOpTransactionRepository.save(TransactionDataPreparationUtil.updateTransactionStatus(interopTransaction,
                    TransactionStatus.TRANSACTION_SUCCESS_AT_MOBIQUITY.getStatus()));
            interopTransactionDetailsRepository.save(TransactionDataPreparationUtil.prepareResponseTransactionDetails(
                    interopTransaction, RequestType.RCORESP.toString(), cashOutConfirmResponse,thirdPartyProperties,Constants.ZERO.getValue(), Constants.ONE.getValue()));
            transactionRequestHPSHandler.execute(request, txnId,txnMode, interopTransaction);
            TransactionResponse transactionResponse = TransactionDataPreparationUtil.prepareTransactionResponse(interopTransaction,"","","");
            transactionResponse.setCode(CastUtils.joinStatusCode(InteropResponseCodes.TXN_INITIATED_SUCCESSFULLY.getEntity().toString(), InteropResponseCodes.TXN_INITIATED_SUCCESSFULLY.getStatusCode()));
            transactionResponse.setMappedCode( getDescriptionForCode.getMappingCode(InteropResponseCodes.TXN_INITIATED_SUCCESSFULLY.getStatusCode()));
            transactionResponse.setMessage(getDescriptionForCode.getDescription(InteropResponseCodes.TXN_INITIATED_SUCCESSFULLY.getEntity().toString(),
            		InteropResponseCodes.TXN_INITIATED_SUCCESSFULLY.getStatusCode(), requestBody.getLang()));
            this.publishResponseEvent(transactionResponse, request.getInteropReferenceId(), requestBody, null);
        } else {
            interOpTransactionRepository.save(TransactionDataPreparationUtil.updateTransactionStatus(interopTransaction,
                    TransactionStatus.TRANSACTION_FAIL.getStatus()));
            interopTransactionDetailsRepository.save(TransactionDataPreparationUtil.prepareResponseTransactionDetails(
                    interopTransaction, RequestType.RCORESP.toString(), cashOutConfirmResponse,thirdPartyProperties,Constants.ZERO.getValue(), Constants.ONE.getValue()));
            addSmsDelivery(requestBody.getDebitParty().get(0).getValue(),
                    SMSNotificationCodes.OFF_US_CASH_OUT_CONFIRM_FAIL.toString(), requestBody.getLang()); 
            this.publishResponseEvent(TransactionDataPreparationUtil.prepareTransactionResponse(interopTransaction,
                    cashOutConfirmResponse.getBrokerResponse().getBrokerCode(),
                    cashOutConfirmResponse.getMappingResponse().getMappingCode(),
                    cashOutConfirmResponse.getWalletResponse().getMessage()), request.getInteropReferenceId(), requestBody, null);
        }
    }

    public Response execute(String amount, String txnid, String status, String interopReferenceId,String txnMode,
    		String pin, String em, String msisdn) {
        Map<String, String> cashOutConfirmRequest = new HashMap<>();
        cashOutConfirmRequest.put(MobiquityConst.SESSION_ID.getValue(), interopReferenceId);
        cashOutConfirmRequest.put(MobiquityConst.TXNMODE.getValue(), txnMode);
        cashOutConfirmRequest.put(MobiquityConst.MSISDN.getValue(), msisdn);
        cashOutConfirmRequest.put(MobiquityConst.PIN.getValue(), pin );
        cashOutConfirmRequest.put(MobiquityConst.AMOUNT.getValue(), amount);
        cashOutConfirmRequest.put(MobiquityConst.PROVIDER.getValue(), thirdPartyProperties.getPayeeProviderId());
        cashOutConfirmRequest.put(MobiquityConst.TXNID.getValue(), txnid);
        cashOutConfirmRequest.put(MobiquityConst.STATUS.getValue(), status);
        cashOutConfirmRequest.put(MobiquityConst.EM.getValue(), em);
        return thirdPartyCaller.postMobiquityServiceRequest(cashOutConfirmRequest,
                serviceTemplateNames.getCashOutConfirmRequestTemplate(), getWalletCashOutCofirmUrl(), interopReferenceId, LogConstants.CASHOUT_CONFIRM.getValue());
    }

    private String getWalletCashOutCofirmUrl() {
        Object[] urlArgs = { resource.getWalletBaseUrl(), brokerServiceURLProperties.getUrlAddonIdValue(),
                brokerServiceURLProperties.getUrlCountryIdValue(), brokerServiceURLProperties.getUrlCurrencyValue() };
        return StringUtils.msgFormat(brokerServiceURLProperties.getCashOutConfirmUrl(), urlArgs);
    }


    private void addSmsDelivery(String msisdn, String serviceType, String languageCode) {
        SmsTemplates smsTemplates = smsTemplatesRepository.findSmsTemplateByTypeAndLang(serviceType, languageCode);
        smsDeliveryRepository.save(SMSUtility.prepareSMSDelivery(smsTemplates.getDescription(), msisdn, languageCode,
                serviceType, smsServerProperties.getNodeName()));
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
