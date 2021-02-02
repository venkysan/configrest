package com.comviva.interop.txnengine.services;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comviva.interop.txnengine.configuration.EigTags;
import com.comviva.interop.txnengine.configuration.Resource;
import com.comviva.interop.txnengine.configuration.ServiceTemplateNames;
import com.comviva.interop.txnengine.enums.Constants;
import com.comviva.interop.txnengine.enums.ISOFields;
import com.comviva.interop.txnengine.enums.LogConstants;
import com.comviva.interop.txnengine.model.NetworkMessageRequest;
import com.comviva.interop.txnengine.util.ThirdPartyCaller;

@Service
public class NetworkMessageHPS {

    @Autowired
    private Resource resource;
    
    @Autowired
    private ThirdPartyCaller thirdPartyCaller;
    
    @Autowired
    private ServiceTemplateNames serviceTemplateNames;
    
    @Autowired
    private EigTags eigTags;


    public Map<String, String> execute(NetworkMessageRequest networkMessageRequest, String stan, String rrn,
            String lang) {

        Map<String, String> eigRequest = new HashMap<>();
        eigRequest.put(eigTags.getInterfaceIdTag(), eigTags.getNetworkMessageInterfaceId());
        eigRequest.put(eigTags.getUserLanguageTag(),
        		lang.toLowerCase() + "_" + lang.toUpperCase());
        eigRequest.put(eigTags.getRequestDateTag(),
                new SimpleDateFormat(eigTags.getEigDateFormat()).format(new Date()));
        eigRequest.put(eigTags.getServiceTypeTag(), ISOFields.NETWORK_MESSAGE_SERVICE_TYPE.getValue());
        
        eigRequest.put(ISOFields.DATE_AND_TIME_OF_TRANSMISSION.getValue(), new SimpleDateFormat("yyMMddHHmm").format(new Date()));
        eigRequest.put(ISOFields.SYSTEM_AUDIT_NUMBER.getValue(), stan);
        eigRequest.put(ISOFields.DATE_AND_TIME_OF_TXN.getValue(), new SimpleDateFormat("yyMMddHHmmss").format(new Date()));
        eigRequest.put(ISOFields.FUNCTION_CODE.getValue(), Constants.NETWORK_FUNCTION_CODE.getValue());
        eigRequest.put(ISOFields.MESSAGE_REASON_CODE.getValue(), Constants.MESSAGE_REASON_CODE.getValue());
        eigRequest.put(ISOFields.REFERENCE_NUMBER_OF_THE_RECOVERY.getValue(), rrn);
        eigRequest.put(ISOFields.NETWORK_ACTION.getValue(), networkMessageRequest.getNetworkAction());

        return thirdPartyCaller.postRequestMapResponse(eigRequest, serviceTemplateNames.getNetworkMessageRequestTemplate(),
                resource.getEigUrl(), serviceTemplateNames.getNetworkMessageResponseTemplate(), null, LogConstants.HPS_NETWORK_MESSAGE.getValue());
    }

}
