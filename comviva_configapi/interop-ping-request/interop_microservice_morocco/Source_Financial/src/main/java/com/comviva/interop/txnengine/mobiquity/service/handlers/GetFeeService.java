package com.comviva.interop.txnengine.mobiquity.service.handlers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comviva.interop.txnengine.configuration.BrokerServiceProperties;
import com.comviva.interop.txnengine.configuration.BrokerServiceURLProperties;
import com.comviva.interop.txnengine.configuration.Resource;
import com.comviva.interop.txnengine.configuration.ServiceTemplateNames;
import com.comviva.interop.txnengine.enums.NonFinancialServiceTypes;
import com.comviva.interop.txnengine.enums.RequestType;
import com.comviva.interop.txnengine.enums.TransactionTypes;
import com.comviva.interop.txnengine.model.QuotationRequest;
import com.comviva.interop.txnengine.model.Response;
import com.comviva.interop.txnengine.util.MobiquityConst;
import com.comviva.interop.txnengine.util.StringUtils;
import com.comviva.interop.txnengine.util.ThirdPartyCaller;

@Service
public class GetFeeService {
	
	@Autowired
    private ServiceTemplateNames serviceTemplateNames;
	
    @Autowired
    private ThirdPartyCaller thirdPartyCaller;
    
    @Autowired
    private BrokerServiceURLProperties brokerServiceURLProperties;
    
    @Autowired
    private BrokerServiceProperties thirdPartyProperties;
    
    @Autowired
    private Resource resource;

    public Response execute(boolean isUserRegisteredWithTheSameMFS, QuotationRequest req, String interopReferenceId) {
        Map<String, String> feeRequest = new HashMap<>();
        feeRequest.put(MobiquityConst.AMOUNT.getValue(), req.getAmount());
        if(TransactionTypes.P2P.getTransactionType().equals(req.getTransactionType())) {
            if (isUserRegisteredWithTheSameMFS) {
                feeRequest.put(MobiquityConst.SERVICE_TYPE.getValue(), RequestType.CTMREQ.toString());
                feeRequest.put(MobiquityConst.PAYEE_ACCOUNT_ID.getValue(), req.getCreditParty().get(0).getValue());
                feeRequest.put(MobiquityConst.PAYEE_USER_TYPE.getValue(), thirdPartyProperties.getSubscriberUserType());
            }
            else {
                feeRequest.put(MobiquityConst.SERVICE_TYPE.getValue(), RequestType.RCOREQ.toString());
                feeRequest.put(MobiquityConst.PAYEE_ACCOUNT_ID.getValue(), thirdPartyProperties.getChannelUserMsisdn());
                feeRequest.put(MobiquityConst.PAYEE_USER_TYPE.getValue(), thirdPartyProperties.getPayeeUserType());
            }
        }
        else if(TransactionTypes.MERCHPAY.getTransactionType().equals(req.getTransactionType())) {
            if(isUserRegisteredWithTheSameMFS) {
                feeRequest.put(MobiquityConst.SERVICE_TYPE.getValue(), RequestType.RMPREQ.toString());
                feeRequest.put(MobiquityConst.PAYEE_ACCOUNT_ID.getValue(), req.getCreditParty().get(0).getValue());
                feeRequest.put(MobiquityConst.PAYEE_USER_TYPE.getValue(), thirdPartyProperties.getSubscriberUserType());
            }
            else {
                feeRequest.put(MobiquityConst.SERVICE_TYPE.getValue(), RequestType.RCOREQ.toString());
                feeRequest.put(MobiquityConst.PAYEE_ACCOUNT_ID.getValue(), thirdPartyProperties.getChannelUserMsisdn());
                feeRequest.put(MobiquityConst.PAYEE_USER_TYPE.getValue(), thirdPartyProperties.getSubscriberUserType());
            }
        }
        else if(TransactionTypes.MERCHPAY_PULL.getTransactionType().equals(req.getTransactionType())) {
        	if(isUserRegisteredWithTheSameMFS) {
        		feeRequest.put(MobiquityConst.SERVICE_TYPE.getValue(), RequestType.CMPREQ.toString());
                feeRequest.put(MobiquityConst.PAYEE_ACCOUNT_ID.getValue(), req.getCreditParty().get(0).getValue());
            }
            else {
                feeRequest.put(MobiquityConst.SERVICE_TYPE.getValue(), RequestType.CMPREQ.toString());
                feeRequest.put(MobiquityConst.PAYEE_ACCOUNT_ID.getValue(), thirdPartyProperties.getChannelUserMsisdn());
            }
        }
        feeRequest.put(MobiquityConst.SESSION_ID.getValue(), interopReferenceId);
        feeRequest.put(MobiquityConst.AMOUNT.getValue(), req.getAmount());
        /*feeRequest.put(MobiquityConst.SERVICE_TYPE.getValue(), MobiquityConst.SERVICE_TYPE_SUBSCRIBER.getValue());*/
        feeRequest.put(MobiquityConst.PAYER_ACCOUNT_ID.getValue(), req.getDebitParty().get(0).getValue());
        feeRequest.put(MobiquityConst.PAYER_USER_TYPE.getValue(), thirdPartyProperties.getSubscriberUserType());
        feeRequest.put(MobiquityConst.PAYER_PROVIDER_ID.getValue(), thirdPartyProperties.getPayerProviderId());
        feeRequest.put(MobiquityConst.PAYER_PAY_ID.getValue(), thirdPartyProperties.getPayerPayId());
        feeRequest.put(MobiquityConst.PAYEE_ACCOUNT_ID.getValue(), req.getCreditParty().get(0).getValue());
        feeRequest.put(MobiquityConst.PAYEE_PROVIDER_ID.getValue(), thirdPartyProperties.getPayeeProviderId());
        feeRequest.put(MobiquityConst.PAYEE_PAY_ID.getValue(), thirdPartyProperties.getPayeePayId());
        return thirdPartyCaller.postMobiquityServiceRequest(feeRequest,
                serviceTemplateNames.getGetFeeRequestTemplate(), getMobiquityGetFeeUrl(),interopReferenceId, NonFinancialServiceTypes.GET_FEE.getServiceType());
    }

    private String getMobiquityGetFeeUrl() {
        Object[] urlArgs = { resource.getWalletBaseUrl(), brokerServiceURLProperties.getUrlAddonIdValue(),
                brokerServiceURLProperties.getUrlCountryIdValue(), brokerServiceURLProperties.getUrlCurrencyValue() };
        return StringUtils.msgFormat(brokerServiceURLProperties.getGetFeeUrl(), urlArgs);
    }
}
