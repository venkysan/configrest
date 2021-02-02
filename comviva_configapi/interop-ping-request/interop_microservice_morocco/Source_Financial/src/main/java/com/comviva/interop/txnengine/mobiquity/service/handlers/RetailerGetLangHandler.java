package com.comviva.interop.txnengine.mobiquity.service.handlers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comviva.interop.txnengine.configuration.BrokerServiceURLProperties;
import com.comviva.interop.txnengine.configuration.Resource;
import com.comviva.interop.txnengine.enums.NonFinancialServiceTypes;
import com.comviva.interop.txnengine.model.Response;
import com.comviva.interop.txnengine.util.MobiquityConst;
import com.comviva.interop.txnengine.util.StringUtils;
import com.comviva.interop.txnengine.util.ThirdPartyCaller;

@Service
public class RetailerGetLangHandler {

    @Autowired
    private ThirdPartyCaller thirdPartyCaller;
    
    @Autowired
    private BrokerServiceURLProperties brokerServiceURLProperties;
    
    @Autowired
    private Resource resource;

    public Response callBrokerForRGetLangService(String senderMSISDN, String receiverMSISDN, String interopReferenceId) {
    	Map<String, String> request = new HashMap<>();
    	request.put(MobiquityConst.MSISDN.getValue(), senderMSISDN);
   	 	request.put(MobiquityConst.MSISDN2.getValue(), receiverMSISDN);
        String url = getGetLangRetailerUrl() + MobiquityConst.SEPARATOR.getValue() + MobiquityConst.MSISDN.getValue()+ MobiquityConst.EQUAL_OPERATOR.getValue() + receiverMSISDN
        		+MobiquityConst.AND_OPERATOR.getValue()
        		+ MobiquityConst.SESSION_ID.getValue()+ MobiquityConst.EQUAL_OPERATOR.getValue() + interopReferenceId;
        return thirdPartyCaller.postMobiquityServiceRequest(request, null, url, interopReferenceId, NonFinancialServiceTypes.GET_RETAILER_LANG.getServiceType());
    }

    private String getGetLangRetailerUrl() {
        Object[] urlArgs = { resource.getWalletBaseUrl(), brokerServiceURLProperties.getUrlAddonIdValue(),
                brokerServiceURLProperties.getUrlCountryIdValue(), brokerServiceURLProperties.getUrlCurrencyValue() };
        return StringUtils.msgFormat(brokerServiceURLProperties.getRetailerLangURL(), urlArgs);
    }
}
