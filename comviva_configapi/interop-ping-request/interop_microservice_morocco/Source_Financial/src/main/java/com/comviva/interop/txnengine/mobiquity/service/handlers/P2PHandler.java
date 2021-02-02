package com.comviva.interop.txnengine.mobiquity.service.handlers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comviva.interop.txnengine.configuration.BrokerServiceURLProperties;
import com.comviva.interop.txnengine.configuration.Resource;
import com.comviva.interop.txnengine.configuration.ServiceTemplateNames;
import com.comviva.interop.txnengine.configuration.BrokerServiceProperties;
import com.comviva.interop.txnengine.enums.LogConstants;
import com.comviva.interop.txnengine.model.Response;
import com.comviva.interop.txnengine.model.TransactionRequest;
import com.comviva.interop.txnengine.util.MobiquityConst;
import com.comviva.interop.txnengine.util.StringUtils;
import com.comviva.interop.txnengine.util.ThirdPartyCaller;

@Service
public class P2PHandler {

	@Autowired
    private ServiceTemplateNames serviceTemplateNames;
    
    @Autowired
    private BrokerServiceURLProperties brokerServiceURLProperties;
    
    @Autowired
    private BrokerServiceProperties thirdPartyProperties;

    @Autowired
    private ThirdPartyCaller thirdPartyCaller;
    
    @Autowired
    private Resource resource;

    public Response execute(TransactionRequest req, String interopReferenceId,String txnMode, String em) {
        Map<String, String> request = new HashMap<>();
        request.put(MobiquityConst.SESSION_ID.getValue(), interopReferenceId);
        request.put(MobiquityConst.TXNMODE.getValue(), txnMode);
        request.put(MobiquityConst.EM.getValue(), em);
        
        request.put(MobiquityConst.MSISDN.getValue(), req.getDebitParty().get(0).getValue());
        request.put(MobiquityConst.MSISDN2.getValue(), req.getCreditParty().get(0).getValue());
        request.put(MobiquityConst.AMOUNT.getValue(), req.getAmount());
        request.put(MobiquityConst.PIN.getValue(), req.getDebitPartyCredentials().getPin());
        request.put(MobiquityConst.PROVIDER.getValue(), thirdPartyProperties.getPayeeProviderId());
        request.put(MobiquityConst.PROVIDER2.getValue(), thirdPartyProperties.getPayerProviderId());
        request.put(MobiquityConst.PAYID.getValue(), thirdPartyProperties.getPayeePayId());
        request.put(MobiquityConst.PAYID2.getValue(), thirdPartyProperties.getPayerPayId());

        return thirdPartyCaller.postMobiquityServiceRequest(request, serviceTemplateNames.getP2PRequestTemplate(),
                getMobiquityP2PUrl(), interopReferenceId, LogConstants.P2P.getValue());

    }

    private String getMobiquityP2PUrl() {
        Object[] urlArgs = { resource.getWalletBaseUrl(), brokerServiceURLProperties.getUrlAddonIdValue(),
                brokerServiceURLProperties.getUrlCountryIdValue(), brokerServiceURLProperties.getUrlCurrencyValue() };
        return StringUtils.msgFormat(brokerServiceURLProperties.getP2pUrl(), urlArgs);
    }
}
