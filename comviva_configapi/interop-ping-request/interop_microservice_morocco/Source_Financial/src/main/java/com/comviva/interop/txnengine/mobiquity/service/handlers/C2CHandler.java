package com.comviva.interop.txnengine.mobiquity.service.handlers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comviva.interop.txnengine.configuration.BrokerServiceProperties;
import com.comviva.interop.txnengine.configuration.BrokerServiceURLProperties;
import com.comviva.interop.txnengine.configuration.Resource;
import com.comviva.interop.txnengine.configuration.ServiceTemplateNames;
import com.comviva.interop.txnengine.enums.LogConstants;
import com.comviva.interop.txnengine.model.Response;
import com.comviva.interop.txnengine.util.MobiquityConst;
import com.comviva.interop.txnengine.util.StringUtils;
import com.comviva.interop.txnengine.util.ThirdPartyCaller;

@Service
public class C2CHandler {

    @Autowired
    private BrokerServiceProperties thirdPartyProperties;
    
    @Autowired
    private BrokerServiceURLProperties brokerServiceURLProperties;
    
    @Autowired
    private ServiceTemplateNames serviceTemplateNames;

    @Autowired
    private ThirdPartyCaller thirdPartyCaller;
    
    @Autowired
    private Resource resource;

    public Response execute(String msisdn,String msisdn2,String amount, String interopReferenceId, String txnMode) {
        Map<String, String> request = new HashMap<>();
        request.put(MobiquityConst.SESSION_ID.getValue(), interopReferenceId);
        request.put(MobiquityConst.MSISDN.getValue(), msisdn);
        request.put(MobiquityConst.MSISDN2.getValue(), msisdn2);
        request.put(MobiquityConst.AMOUNT.getValue(), amount);
        request.put(MobiquityConst.PROVIDER.getValue(), thirdPartyProperties.getPayeeProviderId());
        request.put(MobiquityConst.PROVIDER2.getValue(), thirdPartyProperties.getPayerProviderId());
        request.put(MobiquityConst.PAYID.getValue(), thirdPartyProperties.getPayeePayId());
        request.put(MobiquityConst.PAYID2.getValue(), thirdPartyProperties.getPayerPayId());
        request.put(MobiquityConst.CONNECTOR.getValue(), thirdPartyProperties.getConnector());
        request.put(MobiquityConst.TXNMODE.getValue(), txnMode);
        return thirdPartyCaller.postMobiquityServiceRequest(request, serviceTemplateNames.getC2CRequestTemplate(),
                getMobiquityC2CUrl(), interopReferenceId, LogConstants.C2C.getValue());
    }

    private String getMobiquityC2CUrl() {
        Object[] urlArgs = { resource.getWalletBaseUrl(), brokerServiceURLProperties.getUrlAddonIdValue(),
                brokerServiceURLProperties.getUrlCountryIdValue(), brokerServiceURLProperties.getUrlCurrencyValue() };
        return StringUtils.msgFormat(brokerServiceURLProperties.getC2cUrl(), urlArgs);
    }

}