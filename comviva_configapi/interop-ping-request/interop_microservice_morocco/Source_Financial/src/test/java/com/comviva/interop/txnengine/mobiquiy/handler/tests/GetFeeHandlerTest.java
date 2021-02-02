package com.comviva.interop.txnengine.mobiquiy.handler.tests;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestClientException;

import com.comviva.interop.txnengine.configuration.BrokerServiceURLProperties;
import com.comviva.interop.txnengine.configuration.BrokerServiceProperties;
import com.comviva.interop.txnengine.enums.InteropResponseCodes;
import com.comviva.interop.txnengine.enums.TestCaseConstants;
import com.comviva.interop.txnengine.mobiquity.service.handlers.GetFeeHandler;
import com.comviva.interop.txnengine.mobiquity.service.handlers.GetFeeService;
import com.comviva.interop.txnengine.model.BrokerResponse;
import com.comviva.interop.txnengine.model.MappingResponse;
import com.comviva.interop.txnengine.model.QuotationRequest;
import com.comviva.interop.txnengine.model.Response;
import com.comviva.interop.txnengine.request.validations.GetDescriptionForCode;
import com.comviva.interop.txnengine.util.DataPreparationUtil;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GetFeeHandlerTest {

    @MockBean
    private GetFeeService mobiquityGetFee;

    @MockBean
    private ApplicationEventPublisher applicationEventPublisher;
    
    @MockBean
    private BrokerServiceProperties thirdPartyProperties;

    @MockBean
    private GetDescriptionForCode getDescriptionForCode;
    
    @MockBean
    private BrokerServiceURLProperties brokerServiceURLProperties;

    @Test
    public void validateGetFeeHandleFailureCase() {
        GetFeeHandler getFeeHandler = new GetFeeHandler(mobiquityGetFee, applicationEventPublisher, getDescriptionForCode, brokerServiceURLProperties);
        QuotationRequest transactionRequest = DataPreparationUtil.prepareTransactionQuotationrequestObj();
        Response response = new Response();
        BrokerResponse brokerResponse = new BrokerResponse();
        brokerResponse.setBrokerCode("200");
        brokerResponse.setBrokerMsg("Success");
        MappingResponse mappingResponse = new MappingResponse();
        mappingResponse.setMappingCode(InteropResponseCodes.FAILURE_RESPONSE_FROM_BROKER.getStatusCode());
        response.setMappingResponse(mappingResponse);
        response.setBrokerResponse(brokerResponse);
        when(brokerServiceURLProperties.getUrlCountryIdValue()).thenReturn(TestCaseConstants.COUNTRY_ID_VALUE.getValue());
        when(mobiquityGetFee.execute(Boolean.FALSE, transactionRequest, TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue())).thenReturn(response);
        getFeeHandler.execute(Boolean.FALSE, transactionRequest, TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue());
        Mockito.verify(mobiquityGetFee, Mockito.times(1)).execute(Boolean.FALSE, transactionRequest, TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue());
    }
    
    @Test
    public void validateGetFeeHandleAmbiguousCase() {
        GetFeeHandler getFeeHandler = new GetFeeHandler(mobiquityGetFee, applicationEventPublisher, getDescriptionForCode, brokerServiceURLProperties);
        QuotationRequest transactionRequest = DataPreparationUtil.prepareTransactionQuotationrequestObj();
        Response response = new Response();
        BrokerResponse brokerResponse = new BrokerResponse();
        brokerResponse.setBrokerCode("200");
        brokerResponse.setBrokerMsg("Success");
        response.setBrokerResponse(brokerResponse);
        when(brokerServiceURLProperties.getUrlCountryIdValue()).thenReturn(TestCaseConstants.COUNTRY_ID_VALUE.getValue());
        when(mobiquityGetFee.execute(Boolean.FALSE, transactionRequest, TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue())).thenReturn(response);
        getFeeHandler.execute(Boolean.FALSE, transactionRequest, TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue());
        Mockito.verify(mobiquityGetFee, Mockito.times(1)).execute(Boolean.FALSE, transactionRequest, TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue());
    }
    
    @Test
    public void validateGetFeeHandleRestClientExceptionCase() {
        GetFeeHandler getFeeHandler = new GetFeeHandler(mobiquityGetFee, applicationEventPublisher, getDescriptionForCode, brokerServiceURLProperties);
        QuotationRequest transactionRequest = DataPreparationUtil.prepareTransactionQuotationrequestObj();
        Response response = new Response();
        BrokerResponse brokerResponse = new BrokerResponse();
        brokerResponse.setBrokerCode("200");
        brokerResponse.setBrokerMsg("Success");
        MappingResponse mappingResponse = new MappingResponse();
        mappingResponse.setMappingCode(InteropResponseCodes.FAILURE_RESPONSE_FROM_BROKER.getStatusCode());
        response.setMappingResponse(mappingResponse);
        response.setBrokerResponse(brokerResponse);
        when(brokerServiceURLProperties.getUrlCountryIdValue()).thenReturn(TestCaseConstants.COUNTRY_ID_VALUE.getValue());
        doThrow(new RestClientException(TestCaseConstants.ERRORDEFAULT_INTERNAL.getValue())).when(mobiquityGetFee).execute(Boolean.FALSE, transactionRequest, TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue());
        getFeeHandler.execute(Boolean.FALSE, transactionRequest, TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue());
        Mockito.verify(mobiquityGetFee, Mockito.times(1)).execute(Boolean.FALSE, transactionRequest, TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue());
    }
    
    @Test
    public void validateGetFeeHandleExceptionCase() {
        GetFeeHandler getFeeHandler = new GetFeeHandler(mobiquityGetFee, applicationEventPublisher, getDescriptionForCode, brokerServiceURLProperties);
        QuotationRequest transactionRequest = DataPreparationUtil.prepareTransactionQuotationrequestObj();
        Response response = new Response();
        BrokerResponse brokerResponse = new BrokerResponse();
        brokerResponse.setBrokerCode("200");
        brokerResponse.setBrokerMsg("Success");
        when(brokerServiceURLProperties.getUrlCountryIdValue()).thenReturn(TestCaseConstants.COUNTRY_ID_VALUE.getValue());
        MappingResponse mappingResponse = new MappingResponse();
        mappingResponse.setMappingCode(InteropResponseCodes.FAILURE_RESPONSE_FROM_BROKER.getStatusCode());
        response.setMappingResponse(mappingResponse);
        response.setBrokerResponse(brokerResponse);
        doThrow(new RuntimeException(TestCaseConstants.ERRORDEFAULT_INTERNAL.getValue())).when(mobiquityGetFee).execute(Boolean.FALSE, transactionRequest, TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue());
        getFeeHandler.execute(Boolean.FALSE, transactionRequest, TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue());
        Mockito.verify(mobiquityGetFee, Mockito.times(1)).execute(Boolean.FALSE, transactionRequest, TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue());
    }
}
