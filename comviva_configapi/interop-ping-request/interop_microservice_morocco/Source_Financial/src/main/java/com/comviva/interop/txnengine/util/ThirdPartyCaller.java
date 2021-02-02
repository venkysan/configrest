package com.comviva.interop.txnengine.util;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.comviva.interop.txnengine.configuration.BrokerServiceURLProperties;
import com.comviva.interop.txnengine.configuration.EigTags;
import com.comviva.interop.txnengine.configuration.NonFinancialServerProperties;
import com.comviva.interop.txnengine.configuration.Resource;
import com.comviva.interop.txnengine.entities.BrokerNonFinServiceDetails;
import com.comviva.interop.txnengine.entities.NonFinancialServiceDetails;
import com.comviva.interop.txnengine.enums.InteropResponseCodes;
import com.comviva.interop.txnengine.enums.LogConstants;
import com.comviva.interop.txnengine.enums.NonFinancialServiceTypes;
import com.comviva.interop.txnengine.enums.ValidationErrors;
import com.comviva.interop.txnengine.exception.InteropException;
import com.comviva.interop.txnengine.model.Response;
import com.comviva.interop.txnengine.repositories.BrokerNonFinServiceDetailsRepository;
import com.comviva.interop.txnengine.repositories.NonFinancialServiceDetailsRepository;

@Service
public class ThirdPartyCaller {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThirdPartyCaller.class);

    private RestTemplate restTemplate;
    
    private VelocityTemplateMaker velocityTemplateMaker;
    
    @Autowired
    private BrokerServiceURLProperties brokerServiceURLProperties;
    
    @Autowired
    private EigTags eigTags;
    
    @Autowired
    private Resource resource;
    
    @Autowired
    private NonFinancialServiceDetailsRepository nonFinancialServiceDetailsRepository;

    @Autowired
    private BrokerNonFinServiceDetailsRepository brokerNonFinServiceDetailsRepository;
    
    @Autowired
    private NonFinancialServerProperties nonFinancialServerProperties;
        
    @Autowired
    public ThirdPartyCaller(RestTemplate restTemplate, VelocityTemplateMaker velocityTemplateMaker) {
        this.restTemplate = restTemplate;
        this.velocityTemplateMaker = velocityTemplateMaker;
    }

    public Response postMobiquityServiceRequest(Map<String, String> request, String requestTemplateName, String url, String interOpRefId, String useCase) {
        Response brokerResponse = null;
        if (request != null && null != requestTemplateName) {
            String payload = velocityTemplateMaker.getTemplatefromMapInput(requestTemplateName, request);
            url += MobiquityConst.SEPARATOR.getValue() + payload;
        }
        url = url.replaceAll("\\s", "");
        BrokerNonFinServiceDetails brokerNonFinServiceDetails = saveNonfinacialService(useCase,  request, interOpRefId);
        
        String message = LoggerUtil.prepareLogDetailForBrokerRequest(brokerServiceURLProperties.getUrlCountryIdValue(), brokerServiceURLProperties.getUrlCurrencyValue(), interOpRefId, 
                LogConstants.OUTGOING_REQUEST_EVENT_TYPE.getValue(), request, useCase, resource.getPinLength());
        LOGGER.info("Broker request: {}", message);
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_XML));
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String response = restTemplate.postForObject(url, entity, String.class);
        if (response == null || "".equals(response)) {
            throw new InteropException(InteropResponseCodes.FAILURE_RESPONSE_FROM_BROKER.getStatusCode(),
                    InteropResponseCodes.FAILURE_RESPONSE_FROM_BROKER.getEntity().toString());
        }
        brokerResponse = StringUtils.xmlToModel(response);
        if(isNonFinancialService(useCase) && null != brokerNonFinServiceDetails) {
        	updateBrokerNonFinServiceDetails(brokerNonFinServiceDetails, brokerResponse.getMappingResponse() != null ? brokerResponse.getMappingResponse().getMappingCode() : "",
        			brokerResponse.getWalletResponse() != null ? brokerResponse.getWalletResponse().getTxnstatus() : "", brokerResponse.getWalletResponse() != null ? brokerResponse.getWalletResponse().getMessage() : "");
        }
        String responseMessage = LoggerUtil.prepareLogDetailForBrokerResponse(request, brokerServiceURLProperties.getUrlCountryIdValue(), interOpRefId, LogConstants.INCOMING_RESPONSE_EVENT_TYPE.getValue(), brokerResponse, useCase);
        LOGGER.info("Broker response: {}", responseMessage);
        return brokerResponse;
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> getDefaultWalletStatusFromNonFinancialService(String url, String apiKeyName,
            String apiKeyValue, String payerMSISDN, String payeeMSISDN, String interopRefId) {
    	NonFinancialServiceDetails nonFinancialDetails = prepareNonFinancialDetails(payerMSISDN, payeeMSISDN, NonFinancialServiceTypes.GET_DEFAULT_WALLET_STATUS.toString(), interopRefId);
    	nonFinancialServiceDetailsRepository.save(nonFinancialDetails);
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        if (apiKeyName != null && !"".equals(apiKeyName) && apiKeyValue != null && !"".equals(apiKeyValue)) {
            headers.add(apiKeyName, apiKeyValue);
        }
        HttpEntity<String> entity = new HttpEntity<>(headers);
        Map<String, String> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class).getBody();
        if(null != response && !response.isEmpty()) {
        	updateNonFinDetails(nonFinancialDetails, response.get(nonFinancialServerProperties.getMappedCodeTag()), 
        			response.get(nonFinancialServerProperties.getCodeTag()), "", response.get(nonFinancialServerProperties.getDefaultWalletStatusTag()));
        }
        return response; 
    }

    public Map<String, String> postRequestMapResponse(Map<String, String> request, String requestTemplateName,
            String url, String responseTemplateName, String interopReferenceId, String useCase) {

        String payload = velocityTemplateMaker.getTemplatefromMapInput(requestTemplateName, request);
        String message = LoggerUtil.prepareLogDetailForHPSRequest(request.get(eigTags.getMsisdnTag()),
                brokerServiceURLProperties.getUrlCountryIdValue(), interopReferenceId,
                LogConstants.OUTGOING_REQUEST_EVENT_TYPE.getValue(), payload, request.get(eigTags.getUserLanguageTag()));
        LOGGER.info("HPS request: {}", message);
        HttpEntity<String> eigRequestPost = new HttpEntity<>(payload);
        String response = restTemplate.postForObject(url, eigRequestPost, String.class);

        if ("".equals(response)) {
            throw new InteropException(ValidationErrors.NO_RESPONSE_FROM_EIG.getStatusCode(),
                    ValidationErrors.NO_RESPONSE_FROM_EIG.getEntity().toString());
        }

        String templateResponse = velocityTemplateMaker.getTemplatefromStringInput(responseTemplateName, response);
        String responseMessage = LoggerUtil.prepareLogDetailForHPSResponse(request.get(eigTags.getUserLanguageTag()), brokerServiceURLProperties.getUrlCountryIdValue(),
                interopReferenceId, LogConstants.INCOMING_RESPONSE_EVENT_TYPE.getValue(), templateResponse, useCase);
       LOGGER.info("HPS response: {}", responseMessage);
        return CastUtils.stringToMap(templateResponse);
    }
    
    private NonFinancialServiceDetails prepareNonFinancialDetails(String payerMsisdn, String payeeMsisdn, String serviceType, String interopTxnId) {
    	NonFinancialServiceDetails nonFinancialDetails = new NonFinancialServiceDetails();
    	nonFinancialDetails.setCreatedDate(new Date());
    	nonFinancialDetails.setPayeeMsisdn(payeeMsisdn);
    	nonFinancialDetails.setPayerMsisdn(payerMsisdn);
    	nonFinancialDetails.setServiceType(serviceType);
    	nonFinancialDetails.setUpdatedDate(new Date());
    	nonFinancialDetails.setInteropTxnId(interopTxnId);
    	return nonFinancialDetails;
    }
    
    private NonFinancialServiceDetails updateNonFinDetails(NonFinancialServiceDetails nonFinancialDetails, String mappingCode, String responseCode, String responseMessage, String defaultWalletStatus) {
    	nonFinancialDetails.setMappingCode(mappingCode);
    	nonFinancialDetails.setResponseCode(responseCode);
    	nonFinancialDetails.setResponseMessage(responseMessage);
    	nonFinancialDetails.setUpdatedDate(new Date());
    	nonFinancialDetails.setOptional(defaultWalletStatus);
    	nonFinancialServiceDetailsRepository.save(nonFinancialDetails);
    	return nonFinancialDetails;
    }
    
    private BrokerNonFinServiceDetails prepareBrokerNonFinServiceDetails(String payerMsisdn, String payeeMsisdn, String serviceType, String interopTxnId) {
    	BrokerNonFinServiceDetails nonFinancialDetails = new BrokerNonFinServiceDetails();
    	nonFinancialDetails.setCreatedDate(new Date());
    	nonFinancialDetails.setPayeeMsisdn(payeeMsisdn);
    	nonFinancialDetails.setPayerMsisdn(payerMsisdn);
    	nonFinancialDetails.setServiceType(serviceType);
    	nonFinancialDetails.setUpdatedDate(new Date());
    	nonFinancialDetails.setInteropTxnId(interopTxnId);
    	return nonFinancialDetails;
    }
    
    private BrokerNonFinServiceDetails updateBrokerNonFinServiceDetails(BrokerNonFinServiceDetails nonFinancialDetails, String mappingCode, String responseCode, String responseMessage) {
    	nonFinancialDetails.setMappingCode(mappingCode);
    	nonFinancialDetails.setResponseCode(responseCode);
    	nonFinancialDetails.setResponseMessage(responseMessage);
    	nonFinancialDetails.setUpdatedDate(new Date());
    	brokerNonFinServiceDetailsRepository.save(nonFinancialDetails);
    	return nonFinancialDetails;
    }
    
    private boolean isNonFinancialService(String serviceName) {
    	 for (NonFinancialServiceTypes type : NonFinancialServiceTypes.values()) {
             if (type.getServiceType().equals(serviceName)) {
                 return true;
             }
         }

         return false;
    }
    
    private BrokerNonFinServiceDetails saveNonfinacialService(String useCase, Map<String, String> request, String interOpRefId) {
    	if(null != request && isNonFinancialService(useCase)) {
        	String payerMSISDN = request.get(MobiquityConst.MSISDN.getValue());
        	String payeeMSISDN = request.get(MobiquityConst.MSISDN2.getValue());
        	if(null == payerMSISDN) {
        		payerMSISDN = request.get(MobiquityConst.PAYER_ACCOUNT_ID.getValue());
        	}
        	if(null == payeeMSISDN) {
        		payeeMSISDN = request.get(MobiquityConst.PAYEE_ACCOUNT_ID.getValue());
        	}
        	return brokerNonFinServiceDetailsRepository.save(prepareBrokerNonFinServiceDetails(payerMSISDN , payeeMSISDN , useCase, interOpRefId));
        }
    	return null;
    }
}
