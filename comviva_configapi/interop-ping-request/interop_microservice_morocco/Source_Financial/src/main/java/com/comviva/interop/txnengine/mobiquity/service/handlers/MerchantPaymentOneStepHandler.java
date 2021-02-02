package com.comviva.interop.txnengine.mobiquity.service.handlers;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
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
import com.comviva.interop.txnengine.enums.TransactionSubTypes;
import com.comviva.interop.txnengine.enums.UnicId;
import com.comviva.interop.txnengine.events.ConfirmTransactionResponseEvent;
import com.comviva.interop.txnengine.model.ConfirmTransactionRequest;
import com.comviva.interop.txnengine.model.ConfirmTransactionResponse;
import com.comviva.interop.txnengine.model.Response;
import com.comviva.interop.txnengine.model.TxnMode;
import com.comviva.interop.txnengine.repositories.InteropTransactionDetailsRepository;
import com.comviva.interop.txnengine.repositories.InteropTransactionsRepository;
import com.comviva.interop.txnengine.repositories.SmsDeliveryRepository;
import com.comviva.interop.txnengine.repositories.SmsTemplatesRepository;
import com.comviva.interop.txnengine.request.validations.GetDescriptionForCode;
import com.comviva.interop.txnengine.util.LoggerUtil;
import com.comviva.interop.txnengine.util.MobiquityConst;
import com.comviva.interop.txnengine.util.SMSUtility;
import com.comviva.interop.txnengine.util.StringUtils;
import com.comviva.interop.txnengine.util.ThirdPartyCaller;
import com.comviva.interop.txnengine.util.TransactionDataPreparationUtil;

@Service
public class MerchantPaymentOneStepHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(MerchantPaymentOneStepHandler.class);
	 
	@Autowired
	private BrokerServiceProperties brokerServiceProperties;
	    
	@Autowired
	private ThirdPartyCaller thirdPartyCaller;

	@Autowired
	private InteropTransactionDetailsRepository interopTransactionDetailsRepository;
	
	@Autowired
	private InteropTransactionsRepository interOpTransactionRepository;
	
	@Autowired
	private ServiceTemplateNames serviceTemplateNames;
	
	@Autowired
    private BrokerServiceURLProperties brokerServiceURLProperties;
	
	@Autowired
    private ApplicationEventPublisher applicationEventPublisher;
	
	@Autowired
    private GetDescriptionForCode getDescriptionForCode;
	
	@Autowired
    private SmsTemplatesRepository smsTemplatesRepository;
	
	@Autowired
    private SMSServerProperties smsServerProperties;
	
	@Autowired
    private SmsDeliveryRepository smsDeliveryRepository;
	
	@Autowired
	private Resource resource;

	@Async("ThirdPartyCallsAsyncPool")
    public void callBrokerForMerchantPayment(InteropTransactions interopTransaction, ConfirmTransactionRequest requestBody, String interopRefId) {
        InteropTransactionDetails txnDetails = TransactionDataPreparationUtil
                .prepareRequestTransactionDetails(interopTransaction, RequestType.CMPREQ.toString(),brokerServiceProperties, Constants.ZERO.getValue(), Constants.ZERO.getValue());
        TxnMode txnMode= new TxnMode(TransactionSubTypes.MP_PULL.toString(),brokerServiceURLProperties.getUrlCountryIdValue(),brokerServiceURLProperties.getUrlCurrencyValue(),
                brokerServiceURLProperties.getUrlAddonIdValue(),interopTransaction.getInteropTxnId(),UnicId.ONE.getVal());
        txnDetails.setTxnMode(txnMode.toString());
        interopTransactionDetailsRepository.save(txnDetails);
        Response response = execute(interopTransaction,interopTransaction.getInteropTxnId(),txnMode.toString(), requestBody.getPin());
        if (!Optional.ofNullable(response.getMappingResponse()).isPresent()) {
            interOpTransactionRepository.save(TransactionDataPreparationUtil.updateTransactionStatus(
                    interopTransaction, TransactionStatus.TRANSACTION_FAIL.getStatus()));
            interopTransactionDetailsRepository.save(TransactionDataPreparationUtil.prepareResponseTransactionDetails(
                    interopTransaction, RequestType.CMPRRESP.toString(), response,brokerServiceProperties, Constants.ZERO.getValue(), Constants.ZERO.getValue()));
            this.publishResponseEvent(TransactionDataPreparationUtil.prepareConfirmTransactionResponse(interopTransaction,
                    InteropResponseCodes.READ_TIMEOUT_FROM_BROKER.getStatusCode(),
                    getDescriptionForCode.getMappingCode(InteropResponseCodes.READ_TIMEOUT_FROM_BROKER.getStatusCode()),
                    getDescriptionForCode.getDescription(Sources.BROKER.toString(),
                            InteropResponseCodes.READ_TIMEOUT_FROM_BROKER.getStatusCode(), requestBody.getLang())),
            		interopRefId, requestBody, null);
        } else if (ThirdPartyResponseCodes.SUCCESS.getMappedCode()
                .equals(response.getMappingResponse().getMappingCode())) {
        	 interOpTransactionRepository.save(TransactionDataPreparationUtil.updateTransactionStatus(
                     interopTransaction, TransactionStatus.TRANSACTION_SUCCESS.getStatus()));
            interopTransactionDetailsRepository.save(TransactionDataPreparationUtil.prepareResponseTransactionDetails(
                    interopTransaction, RequestType.CMPRRESP.toString(), response,brokerServiceProperties, Constants.ZERO.getValue(), Constants.ZERO.getValue()));
            addSmsDelivery(interopTransaction.getPayerMsisdn(), SMSNotificationCodes.MP_PULL_SUCCESS.toString(), requestBody.getLang(),
            		response.getWalletResponse().getBalance());
            this.publishResponseEvent(TransactionDataPreparationUtil.prepareConfirmTransactionResponse(interopTransaction,
            		response.getBrokerResponse().getBrokerCode(), response.getMappingResponse().getMappingCode(),
            		response.getWalletResponse().getMessage()), interopRefId, requestBody, null);
        } else {
            interOpTransactionRepository.save(TransactionDataPreparationUtil.updateTransactionStatus(interopTransaction, TransactionStatus.TRANSACTION_FAIL.getStatus()));
            interopTransactionDetailsRepository.save(TransactionDataPreparationUtil.prepareResponseTransactionDetails(
                    interopTransaction, RequestType.CMPRRESP.toString(), response,brokerServiceProperties, Constants.ZERO.getValue(), Constants.ZERO.getValue()));
            this.publishResponseEvent(TransactionDataPreparationUtil.prepareConfirmTransactionResponse(interopTransaction,
            		response.getBrokerResponse().getBrokerCode(), response.getMappingResponse().getMappingCode(),
            		response.getWalletResponse().getMessage()), interopRefId, requestBody, null);
        }
        
    }
    
    public Response execute(InteropTransactions interopTransactions, String interopReferenceId,String txnMode, String pin) {
        Map<String, String> request = new HashMap<>();
        request.put(MobiquityConst.SESSION_ID.getValue(), interopReferenceId);
        request.put(MobiquityConst.TXNMODE.getValue(), txnMode);
        request.put(MobiquityConst.MERCHANT_CODE.getValue(), brokerServiceProperties.getMerchantCode());
        request.put(MobiquityConst.MSISDN2.getValue(), interopTransactions.getPayerMsisdn());
        request.put(MobiquityConst.AMOUNT.getValue(), interopTransactions.getAmount().toString());
        request.put(MobiquityConst.PROVIDER.getValue(), brokerServiceProperties.getPayeeProviderId());
        request.put(MobiquityConst.PROVIDER2.getValue(), brokerServiceProperties.getPayerProviderId());
        request.put(MobiquityConst.PAYID.getValue(), brokerServiceProperties.getPayerPayId());
        request.put(MobiquityConst.PAYID2.getValue(), brokerServiceProperties.getPayeePayId());
        request.put(MobiquityConst.PIN2.getValue(), pin);
        return thirdPartyCaller.postMobiquityServiceRequest(request,
                serviceTemplateNames.getMerchantPaymentOneStepRequestTemplate(), getMerchantPaymentUrl(), interopReferenceId, LogConstants.MERCHANT_PAYMENT.getValue());
    }

    private String getMerchantPaymentUrl() {
        Object[] urlArgs = { resource.getWalletBaseUrl(), brokerServiceURLProperties.getUrlAddonIdValue(),
                brokerServiceURLProperties.getUrlCountryIdValue(), brokerServiceURLProperties.getUrlCurrencyValue() };
        return StringUtils.msgFormat(brokerServiceURLProperties.getMerchantPaymentURL(), urlArgs);
    }
    
    private void publishResponseEvent(ConfirmTransactionResponse transactionResponse, String reqId, ConfirmTransactionRequest req, Exception ex) {
        String message = LoggerUtil.prepareLogDetailForConfirmTxnResponse(req, brokerServiceURLProperties.getUrlCountryIdValue(),
                reqId, LogConstants.OUTGOING_RESPONSE_EVENT_TYPE.getValue(),  transactionResponse, ex);
        LOGGER.info("Confirm Transactions service response: {}", message);
        applicationEventPublisher.publishEvent(new ConfirmTransactionResponseEvent(this, transactionResponse, reqId));
    }
    
    private void addSmsDelivery(String msisdn, String serviceType, String languageCode, String balance) {
        SmsTemplates smsTemplates = smsTemplatesRepository.findSmsTemplateByTypeAndLang(serviceType, languageCode);
        Object[] args = { balance };
        smsDeliveryRepository
                .save(SMSUtility.prepareSMSDelivery(new MessageFormat(smsTemplates.getDescription()).format(args),
                        msisdn, languageCode, serviceType, smsServerProperties.getNodeName()));
    }
}
