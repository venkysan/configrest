package com.comviva.interop.txnengine.services;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comviva.interop.txnengine.configuration.NonFinancialServerProperties;
import com.comviva.interop.txnengine.configuration.Resource;
import com.comviva.interop.txnengine.enums.DefaultWalletStatusCodes;
import com.comviva.interop.txnengine.enums.Sources;
import com.comviva.interop.txnengine.enums.SuccessStatus;
import com.comviva.interop.txnengine.enums.TransactionTypes;
import com.comviva.interop.txnengine.exception.InteropException;
import com.comviva.interop.txnengine.util.ThirdPartyCaller;

@Service
public class GetDefaultWalletStatusHandler {

    @Autowired
    private NonFinancialServerProperties nonFinancialServerProperties;

    @Autowired
    private ThirdPartyCaller thirdPartyCaller;
    
    @Autowired
	private Resource resource;

    @Autowired
    public GetDefaultWalletStatusHandler(ThirdPartyCaller thirdPartyCaller, NonFinancialServerProperties nonFinancialServerProperties, Resource resource) {
        this.thirdPartyCaller = thirdPartyCaller;
        this.nonFinancialServerProperties = nonFinancialServerProperties;
        this.resource = resource;
    }

    public boolean isUserRegisteredWithSameMFS(String senderMSISDN, String recevierMSISDN, String interopRefId, String transactionType) {
        Map<String, String> getDefaultWalletStatusResponse = execute(senderMSISDN, recevierMSISDN, interopRefId, transactionType);
        String statusCode = getDefaultWalletStatusResponse.get(nonFinancialServerProperties.getCodeTag()).split("_")[1];

        if (SuccessStatus.SUCCEEDED.getStatusCode().equals(statusCode)) {
            String defaultWalletStatus = getDefaultWalletStatusResponse
                    .get(nonFinancialServerProperties.getDefaultWalletStatusTag());
            if (DefaultWalletStatusCodes.ENROLMENT_REG_SUCCESS_STATUS.getCode().equals(defaultWalletStatus)
                    || DefaultWalletStatusCodes.NOT_REGISTERED.getCode().equals(defaultWalletStatus)) {
                return true;
            } else if (DefaultWalletStatusCodes.REGISTERED_WITH_OTHER_MFS.getCode().equals(defaultWalletStatus)) {
                return false;
            }
        } else {
            throw new InteropException(statusCode, Sources.HPS.toString());
        }
        return false;
    }

    private Map<String, String> execute(String senderMSISDN, String recevierMSISDN, String interopRefId, String transactionType) {
    	String url = null; 
    	if(TransactionTypes.MERCHPAY_PULL.getTransactionType().equals(transactionType)) {
    		url = resource.getNonFinGetUserUrl() + senderMSISDN;
    	}
    	else {
    		url = resource.getNonFinGetUserUrl() + recevierMSISDN;
    	}
        return thirdPartyCaller.getDefaultWalletStatusFromNonFinancialService(
        		url, resource.getNonFinApiKeyName(),
        		resource.getNonFinApiKeyValue(), senderMSISDN, recevierMSISDN, interopRefId);
    }

}
