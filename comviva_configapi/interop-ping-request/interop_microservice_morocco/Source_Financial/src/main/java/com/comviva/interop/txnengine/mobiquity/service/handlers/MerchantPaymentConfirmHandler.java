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
public class MerchantPaymentConfirmHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(MerchantPaymentConfirmHandler.class);

	@Autowired
	private Resource resource;
	 
	@Autowired
    private GetDescriptionForCode getDescriptionForCode;
	
	@Autowired
    private BrokerServiceURLProperties brokerServiceURLProperties;
	
	@Autowired
    private ThirdPartyCaller thirdPartyCaller;
	
	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;
	
	@Autowired
    private BrokerServiceProperties thirdPartyProperties;
    
    @Autowired
    private ServiceTemplateNames serviceTemplateNames;
    
    @Autowired
	private InteropTransactionsRepository interOpTransactionRepository;
	
    
    @Autowired
	private InteropTransactionDetailsRepository interopTransactionDetailsRepository;
	
   

    public void callBrokerForMerchantPaymentConfirm(Request request, InteropTransactions interopTransaction, TransactionRequest requestBody, String txnId) {
        InteropTransactionDetails txnDetails = TransactionDataPreparationUtil
                .prepareRequestTransactionDetails(interopTransaction, RequestType.RMPRREQ.toString(),thirdPartyProperties, Constants.ZERO.getValue(), Constants.ZERO.getValue());
        TxnMode txnMode= new TxnMode(TransactionSubTypes.MP_ON_US.toString(),brokerServiceURLProperties.getUrlCountryIdValue(),brokerServiceURLProperties.getUrlCurrencyValue(),
                brokerServiceURLProperties.getUrlAddonIdValue(),request.getInteropReferenceId(),UnicId.ONE.getVal());
        txnDetails.setTxnMode(txnMode.toString());
        interopTransactionDetailsRepository.save(txnDetails);
        Response merchantPaymentResponse = execute(requestBody,request.getInteropReferenceId(),txnMode.toString(), txnId);
        if (!Optional.ofNullable(merchantPaymentResponse.getMappingResponse()).isPresent()) {
            interOpTransactionRepository.save(TransactionDataPreparationUtil.updateTransactionStatus(
                    interopTransaction, TransactionStatus.TRANSACTION_AMBIGUOUS.getStatus()));
            interopTransactionDetailsRepository.save(TransactionDataPreparationUtil.prepareResponseTransactionDetails(
                    interopTransaction, RequestType.RMPRRESP.toString(), merchantPaymentResponse,thirdPartyProperties, Constants.ZERO.getValue(), Constants.ZERO.getValue()));
            this.publishResponseEvent(TransactionDataPreparationUtil.prepareTransactionResponse(interopTransaction,
                    InteropResponseCodes.READ_TIMEOUT_FROM_BROKER.getStatusCode(),
                    getDescriptionForCode
                            .getMappingCode(InteropResponseCodes.READ_TIMEOUT_FROM_BROKER.getStatusCode()),
                    getDescriptionForCode.getDescription(Sources.BROKER.toString(),
                            InteropResponseCodes.READ_TIMEOUT_FROM_BROKER.getStatusCode(), requestBody.getLang())),
                    request.getInteropReferenceId(), requestBody, null);
        } else if (ThirdPartyResponseCodes.SUCCESS.getMappedCode()
                .equals(merchantPaymentResponse.getMappingResponse().getMappingCode())) {
            interOpTransactionRepository.save(TransactionDataPreparationUtil.updateTransactionStatus(interopTransaction, TransactionStatus.TRANSACTION_SUCCESS.getStatus()));
            interopTransactionDetailsRepository.save(TransactionDataPreparationUtil.prepareResponseTransactionDetails(
                    interopTransaction, RequestType.RMPRRESP.toString(), merchantPaymentResponse,thirdPartyProperties, Constants.ZERO.getValue(), Constants.ZERO.getValue()));
            this.publishResponseEvent(TransactionDataPreparationUtil.prepareTransactionResponse(interopTransaction,
                    merchantPaymentResponse.getBrokerResponse().getBrokerCode(), merchantPaymentResponse.getMappingResponse().getMappingCode(),
                    merchantPaymentResponse.getWalletResponse().getMessage()), request.getInteropReferenceId(), requestBody, null);
        } else {
            interOpTransactionRepository.save(TransactionDataPreparationUtil.updateTransactionStatus(interopTransaction, TransactionStatus.TRANSACTION_FAIL.getStatus()));
            interopTransactionDetailsRepository.save(TransactionDataPreparationUtil.prepareResponseTransactionDetails(
                    interopTransaction, RequestType.RMPRRESP.toString(), merchantPaymentResponse,thirdPartyProperties, Constants.ZERO.getValue(), Constants.ZERO.getValue()));
            this.publishResponseEvent(TransactionDataPreparationUtil.prepareTransactionResponse(interopTransaction,
                    merchantPaymentResponse.getBrokerResponse().getBrokerCode(), merchantPaymentResponse.getMappingResponse().getMappingCode(),
                    merchantPaymentResponse.getWalletResponse().getMessage()), request.getInteropReferenceId(), requestBody, null);
        }
    }
    
    public Response execute(TransactionRequest req, String interopReferenceId,String txnMode, String txnId) {
        Map<String, String> merchantPaymentConfirmRequest = new HashMap<>();
        merchantPaymentConfirmRequest.put(MobiquityConst.SESSION_ID.getValue(), interopReferenceId);
        merchantPaymentConfirmRequest.put(MobiquityConst.TXNMODE.getValue(), txnMode);
        merchantPaymentConfirmRequest.put(MobiquityConst.MSISDN.getValue(), req.getDebitParty().get(0).getValue());
        merchantPaymentConfirmRequest.put(MobiquityConst.MSISDN.getValue(), req.getCreditParty().get(0).getValue());
        merchantPaymentConfirmRequest.put(MobiquityConst.AMOUNT.getValue(), req.getAmount());
        merchantPaymentConfirmRequest.put(MobiquityConst.PROVIDER.getValue(), thirdPartyProperties.getPayeeProviderId());
        merchantPaymentConfirmRequest.put(MobiquityConst.TXNID.getValue(), txnId);
        merchantPaymentConfirmRequest.put(MobiquityConst.STATUS.getValue(), thirdPartyProperties.getConfirmStatus());
        merchantPaymentConfirmRequest.put(MobiquityConst.PIN.getValue(), req.getDebitPartyCredentials().getPin());
        merchantPaymentConfirmRequest.put(MobiquityConst.EM.getValue(), req.getDebitPartyCredentials().getPin());
        return thirdPartyCaller.postMobiquityServiceRequest(merchantPaymentConfirmRequest,
                serviceTemplateNames.getMerchantPaymentConfirmRequestTemplate(), getMerchantPaymentConfirmUrl(), interopReferenceId, LogConstants.MERCHANT_PAYMENT_CONFIRM.getValue());
    }

    private String getMerchantPaymentConfirmUrl() {
        Object[] urlArgs = { resource.getWalletBaseUrl(), brokerServiceURLProperties.getUrlAddonIdValue(),
                brokerServiceURLProperties.getUrlCountryIdValue(), brokerServiceURLProperties.getUrlCurrencyValue() };
        return StringUtils.msgFormat(brokerServiceURLProperties.getMerchantPaymentConfirmURL(), urlArgs);
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
