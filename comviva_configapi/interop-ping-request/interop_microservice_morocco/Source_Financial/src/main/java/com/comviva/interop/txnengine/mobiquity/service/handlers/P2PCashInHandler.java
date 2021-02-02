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
public class P2PCashInHandler {
    

    @Autowired
    private BrokerServiceProperties thirdPartyProperties;
    
    @Autowired
    private BrokerServiceURLProperties brokerServiceURLProperties;
    
    @Autowired
    private Resource resource;
    
    @Autowired
    private ServiceTemplateNames serviceTemplateNames;
    
    @Autowired
    private ThirdPartyCaller thirdPartyCaller;

    @Autowired
    public P2PCashInHandler(BrokerServiceProperties thirdPartyProperties, ThirdPartyCaller thirdPartyCaller) {
        this.thirdPartyProperties = thirdPartyProperties;
        this.thirdPartyCaller = thirdPartyCaller;
    }

    public Response execute(String msisdn, String amount, String interopReferenceId,String txnMode) {
        Map<String, String> p2pCashIn = new HashMap<>();
        p2pCashIn.put(MobiquityConst.SESSION_ID.getValue(), interopReferenceId);
        p2pCashIn.put(MobiquityConst.TXNMODE.getValue(), txnMode);
        p2pCashIn.put(MobiquityConst.MSISDN.getValue(), thirdPartyProperties.getChannelUserMsisdn());
        p2pCashIn.put(MobiquityConst.MSISDN2.getValue(), msisdn);
        p2pCashIn.put(MobiquityConst.AMOUNT.getValue(), amount);
        p2pCashIn.put(MobiquityConst.PROVIDER.getValue(), thirdPartyProperties.getPayeeProviderId());
        p2pCashIn.put(MobiquityConst.PROVIDER2.getValue(), thirdPartyProperties.getPayerProviderId());
        p2pCashIn.put(MobiquityConst.PAYID.getValue(), thirdPartyProperties.getPayeePayId());
        p2pCashIn.put(MobiquityConst.PAYID2.getValue(), thirdPartyProperties.getPayerPayId());
        p2pCashIn.put(MobiquityConst.CONNECTOR.getValue(), thirdPartyProperties.getConnector());
        return thirdPartyCaller.postMobiquityServiceRequest(p2pCashIn,
                serviceTemplateNames.getP2PCashInRequestTemplate(), getWalletGetCashInUrl(), interopReferenceId, LogConstants.P2P_CASH_IN.getValue());
    }

    private String getWalletGetCashInUrl() {
        Object[] urlArgs = { resource.getWalletBaseUrl(), brokerServiceURLProperties.getUrlAddonIdValue(),
                brokerServiceURLProperties.getUrlCountryIdValue(), brokerServiceURLProperties.getUrlCurrencyValue() };
        return StringUtils.msgFormat(brokerServiceURLProperties.getP2PCashInUrl(), urlArgs);
    }
}
