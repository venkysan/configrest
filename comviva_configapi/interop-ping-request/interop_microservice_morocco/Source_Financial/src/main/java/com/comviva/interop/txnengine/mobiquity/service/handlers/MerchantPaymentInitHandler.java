package com.comviva.interop.txnengine.mobiquity.service.handlers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.comviva.interop.txnengine.configuration.BrokerServiceURLProperties;
import com.comviva.interop.txnengine.configuration.Resource;
import com.comviva.interop.txnengine.configuration.ServiceTemplateNames;
import com.comviva.interop.txnengine.configuration.BrokerServiceProperties;
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
public class MerchantPaymentInitHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(MerchantPaymentInitHandler.class);
	
	@Autowired
	private BrokerServiceProperties thirdPartyProperties;
		
	@Autowired
    private GetDescriptionForCode getDescriptionForCode;
    
	@Autowired
	private ThirdPartyCaller thirdPartyCaller;
	
	@Autowired
	private InteropTransactionsRepository interOpTransactionRepository;
	
	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;
	
	@Autowired
	private InteropTransactionDetailsRepository interopTransactionDetailsRepository;
	
	@Autowired
	private MerchantPaymentConfirmHandler merchantPaymentConfirmHandler;
	
	@Autowired
	private ServiceTemplateNames serviceTemplateNames;
	
	@Autowired
    private BrokerServiceURLProperties brokerServiceURLProperties;
	
	@Autowired
	private Resource resource;

    public void callBrokerForMerchantPaymentInit(Request request, InteropTransactions interopTransaction, TransactionRequest requestBody) {
        InteropTransactionDetails txnDetails = TransactionDataPreparationUtil
                .prepareRequestTransactionDetails(interopTransaction, RequestType.RMPREQ.toString(),thirdPartyProperties, Constants.ZERO.getValue(), Constants.ZERO.getValue());
        TxnMode txnMode= new TxnMode(TransactionSubTypes.MP_ON_US.toString(),brokerServiceURLProperties.getUrlCountryIdValue(),brokerServiceURLProperties.getUrlCurrencyValue(),
                brokerServiceURLProperties.getUrlAddonIdValue(),request.getInteropReferenceId(),UnicId.ONE.getVal());
        txnDetails.setTxnMode(txnMode.toString());
        interopTransactionDetailsRepository.save(txnDetails);
        Response merchantPaymentInitResponse = execute(requestBody,request.getInteropReferenceId(),txnMode.toString());
        if (!Optional.ofNullable(merchantPaymentInitResponse.getMappingResponse()).isPresent()) {
            interOpTransactionRepository.save(TransactionDataPreparationUtil.updateTransactionStatus(
                    interopTransaction, TransactionStatus.TRANSACTION_FAIL.getStatus()));
            interopTransactionDetailsRepository.save(TransactionDataPreparationUtil.prepareResponseTransactionDetails(
                    interopTransaction, RequestType.RMRESP.toString(), merchantPaymentInitResponse,thirdPartyProperties, Constants.ZERO.getValue(), Constants.ZERO.getValue()));
            this.publishResponseEvent(TransactionDataPreparationUtil.prepareTransactionResponse(interopTransaction,
                    InteropResponseCodes.READ_TIMEOUT_FROM_BROKER.getStatusCode(),
                    getDescriptionForCode
                            .getMappingCode(InteropResponseCodes.READ_TIMEOUT_FROM_BROKER.getStatusCode()),
                    getDescriptionForCode.getDescription(Sources.BROKER.toString(),
                            InteropResponseCodes.READ_TIMEOUT_FROM_BROKER.getStatusCode(), requestBody.getLang())),
                    request.getInteropReferenceId(), requestBody, null);
        } else if (ThirdPartyResponseCodes.SUCCESS.getMappedCode()
                .equals(merchantPaymentInitResponse.getMappingResponse().getMappingCode())) {
            interopTransactionDetailsRepository.save(TransactionDataPreparationUtil.prepareResponseTransactionDetails(
                    interopTransaction, RequestType.RMRESP.toString(), merchantPaymentInitResponse,thirdPartyProperties, Constants.ZERO.getValue(), Constants.ZERO.getValue()));
            merchantPaymentConfirmHandler.callBrokerForMerchantPaymentConfirm(request, interopTransaction, requestBody, merchantPaymentInitResponse.getWalletResponse().getTxnid());
        } else {
            interOpTransactionRepository.save(TransactionDataPreparationUtil.updateTransactionStatus(interopTransaction, TransactionStatus.TRANSACTION_FAIL.getStatus()));
            interopTransactionDetailsRepository.save(TransactionDataPreparationUtil.prepareResponseTransactionDetails(
                    interopTransaction, RequestType.RMRESP.toString(), merchantPaymentInitResponse,thirdPartyProperties, Constants.ZERO.getValue(), Constants.ZERO.getValue()));
            this.publishResponseEvent(TransactionDataPreparationUtil.prepareTransactionResponse(interopTransaction,
                    merchantPaymentInitResponse.getBrokerResponse().getBrokerCode(), merchantPaymentInitResponse.getMappingResponse().getMappingCode(),
                    merchantPaymentInitResponse.getWalletResponse().getMessage()), request.getInteropReferenceId(), requestBody, null);
        }
    }
    
    public Response execute(TransactionRequest req, String interopReferenceId,String txnMode) {
        Map<String, String> request = new HashMap<>();
        request.put(MobiquityConst.SESSION_ID.getValue(), interopReferenceId);
        request.put(MobiquityConst.TXNMODE.getValue(), txnMode);
        request.put(MobiquityConst.MSISDN.getValue(), req.getDebitParty().get(0).getValue());
        request.put(MobiquityConst.MSISDN2.getValue(), req.getCreditParty().get(0).getValue());
        request.put(MobiquityConst.AMOUNT.getValue(), req.getAmount());
        request.put(MobiquityConst.PROVIDER.getValue(), thirdPartyProperties.getPayeeProviderId());
        request.put(MobiquityConst.PROVIDER2.getValue(), thirdPartyProperties.getPayerProviderId());
        request.put(MobiquityConst.PAYID.getValue(), thirdPartyProperties.getPayerPayId());
        request.put(MobiquityConst.PAYID2.getValue(), thirdPartyProperties.getPayeePayId());
        request.put(MobiquityConst.CONNECTOR.getValue(), thirdPartyProperties.getConnector());
        return thirdPartyCaller.postMobiquityServiceRequest(request,
                serviceTemplateNames.getMerchantPaymentInitRequestTemplate(), getMerchantPaymentInitUrl(), interopReferenceId, LogConstants.MERCHANT_PAYMENT_INIT.getValue());
    }

    private String getMerchantPaymentInitUrl() {
        Object[] urlArgs = { resource.getWalletBaseUrl(), brokerServiceURLProperties.getUrlAddonIdValue(),
                brokerServiceURLProperties.getUrlCountryIdValue(), brokerServiceURLProperties.getUrlCurrencyValue() };
        return StringUtils.msgFormat(brokerServiceURLProperties.getMerchantPaymentInitURL(), urlArgs);
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
