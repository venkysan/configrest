package com.comviva.interop.txnengine.service.test;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.client.RestClientException;

import com.comviva.interop.txnengine.configuration.BrokerServiceProperties;
import com.comviva.interop.txnengine.configuration.BrokerServiceURLProperties;
import com.comviva.interop.txnengine.configuration.Resource;
import com.comviva.interop.txnengine.entities.InteropTransactions;
import com.comviva.interop.txnengine.enums.InteropResponseCodes;
import com.comviva.interop.txnengine.enums.SuccessStatus;
import com.comviva.interop.txnengine.enums.TestCaseConstants;
import com.comviva.interop.txnengine.enums.ThirdPartyResponseCodes;
import com.comviva.interop.txnengine.mobiquity.service.handlers.SubscriberGetLangHandler;
import com.comviva.interop.txnengine.model.BrokerResponse;
import com.comviva.interop.txnengine.model.MappingResponse;
import com.comviva.interop.txnengine.model.Request;
import com.comviva.interop.txnengine.model.RequestValidationResponse;
import com.comviva.interop.txnengine.model.Response;
import com.comviva.interop.txnengine.model.TransactionRequest;
import com.comviva.interop.txnengine.repositories.InteropTransactionsRepository;
import com.comviva.interop.txnengine.request.validations.GetDescriptionForCode;
import com.comviva.interop.txnengine.request.validations.ValidateCreateTransactionRequestBody;
import com.comviva.interop.txnengine.services.CreateTransactionService;
import com.comviva.interop.txnengine.services.GetDefaultWalletStatusHandler;
import com.comviva.interop.txnengine.services.OffUsTransactionService;
import com.comviva.interop.txnengine.services.P2POnUsTransactionService;
import com.comviva.interop.txnengine.util.DataPreparationUtil;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CreateTransactionServiceMockTests {

    @Mock
    private ValidateCreateTransactionRequestBody requestValidations;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private GetDescriptionForCode getDescriptionForCode;

    @Mock
    private GetDefaultWalletStatusHandler getDefaultWalletStatusHandler;

    @Mock
    private SubscriberGetLangHandler mobiquityGetLangHandler;

    @Mock
    private P2POnUsTransactionService p2pOnUsTransactionService;

    @Mock
    private OffUsTransactionService offUsTransactionService;
    
    @Mock
    private BrokerServiceProperties thirdPartyProperties;
    
    @Mock
    private BrokerServiceURLProperties brokerServiceURLProperties;
    
    @InjectMocks
    private CreateTransactionService createTransactionService;
    
    @Mock
    private InteropTransactionsRepository interopTransactionsRepository;
    
    @Autowired
	private Resource resource; 
    
    @Mock
    private ExecutorService executorService;

    @Test
    public void validateCreateTransactionAPIWhenGetLangAPIResponseIsOtherThanSuccess() {
        TransactionRequest transactionRequest = DataPreparationUtil.prepareTransactionrequestObj();
        RequestValidationResponse requestValidationResponse = new RequestValidationResponse(
                SuccessStatus.SUCCESSFUL_REQUEST_VALIDATION.getStatusCode(),
                SuccessStatus.SUCCESSFUL_REQUEST_VALIDATION.getEntity().toString());
        Request request = new Request(transactionRequest);
        request.setInteropReferenceId(TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue());
        when(requestValidations.validate(transactionRequest)).thenReturn(requestValidationResponse);
        InteropTransactions interopTransactions = DataPreparationUtil.prepareInteropTransaction(transactionRequest, request, null);
        when(interopTransactionsRepository.save(ArgumentMatchers.any())).thenReturn(interopTransactions);
        Response response = new Response();
        MappingResponse mappingResponse = new MappingResponse();
        mappingResponse.setMappingCode(InteropResponseCodes.FAILURE_RESPONSE_FROM_BROKER.getStatusCode());
        response.setMappingResponse(mappingResponse);
        createTransactionService.execute(request);
        Mockito.verify(offUsTransactionService, Mockito.times(1)).doOffUsTransaction(request, interopTransactions);
    }

    @Test
    public void validateCreateTransactionAPIWhenExceptionCase() {
        
        TransactionRequest transactionRequest = DataPreparationUtil.prepareTransactionrequestObj();
        RequestValidationResponse requestValidationResponse = new RequestValidationResponse(
                SuccessStatus.SUCCESSFUL_REQUEST_VALIDATION.getStatusCode(),
                SuccessStatus.SUCCESSFUL_REQUEST_VALIDATION.getEntity().toString());
        Request request = new Request(transactionRequest);
        request.setInteropReferenceId(TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue());
        when(requestValidations.validate(transactionRequest)).thenReturn(requestValidationResponse);
        when(interopTransactionsRepository.save(ArgumentMatchers.any())).thenThrow(DataIntegrityViolationException.class);
        createTransactionService.execute(request);
        Mockito.verify(interopTransactionsRepository, Mockito.times(1)).save(ArgumentMatchers.any());
    }
    
    @Test(expected=DataIntegrityViolationException.class)
    public void validateCreateTransactionAPIWhenUpdatingExceptionCase() {
        
        TransactionRequest transactionRequest = DataPreparationUtil.prepareTransactionrequestObj();
        RequestValidationResponse requestValidationResponse = new RequestValidationResponse(
                SuccessStatus.SUCCESSFUL_REQUEST_VALIDATION.getStatusCode(),
                SuccessStatus.SUCCESSFUL_REQUEST_VALIDATION.getEntity().toString());
        Request request = new Request(transactionRequest);
        request.setInteropReferenceId(TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue());
        when(requestValidations.validate(transactionRequest)).thenReturn(requestValidationResponse);
        when(interopTransactionsRepository.save(ArgumentMatchers.any())).thenThrow(DataIntegrityViolationException.class);
        when(interopTransactionsRepository.findInteropTransactionsByTxnId(ArgumentMatchers.any())).thenReturn(new InteropTransactions());
        createTransactionService.execute(request);
        Mockito.verify(interopTransactionsRepository, Mockito.times(1)).save(ArgumentMatchers.any());
    }
    
    @Test
    public void validateCreateTransactionAPIWhenRestClientExceptionCase() {
       
        TransactionRequest transactionRequest = DataPreparationUtil.prepareTransactionrequestObj();
        RequestValidationResponse requestValidationResponse = new RequestValidationResponse(
                SuccessStatus.SUCCESSFUL_REQUEST_VALIDATION.getStatusCode(),
                SuccessStatus.SUCCESSFUL_REQUEST_VALIDATION.getEntity().toString());
        Request request = new Request(transactionRequest);
        request.setInteropReferenceId(TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue());
        when(requestValidations.validate(transactionRequest)).thenReturn(requestValidationResponse);
       doThrow(new RestClientException("RestClientException")).when(offUsTransactionService).doOffUsTransaction(ArgumentMatchers.any(Request.class), ArgumentMatchers.any());
       InteropTransactions interopTransactions = DataPreparationUtil.prepareInteropTransaction(transactionRequest, request, null);
       when(interopTransactionsRepository.save(ArgumentMatchers.any())).thenReturn(interopTransactions);
        createTransactionService.execute(request);
        Mockito.verify(offUsTransactionService, Mockito.times(1)).doOffUsTransaction(request, interopTransactions);
    }
    
    @Test
    public void validateCreateTransactionAPIWhenSocketTimeoutExceptionCase() {
       
        TransactionRequest transactionRequest = DataPreparationUtil.prepareTransactionrequestObj();
        RequestValidationResponse requestValidationResponse = new RequestValidationResponse(
                SuccessStatus.SUCCESSFUL_REQUEST_VALIDATION.getStatusCode(),
                SuccessStatus.SUCCESSFUL_REQUEST_VALIDATION.getEntity().toString());
        Request request = new Request(transactionRequest);
        request.setInteropReferenceId(TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue());
        when(requestValidations.validate(transactionRequest)).thenReturn(requestValidationResponse);
       doThrow(new RestClientException("RestClientException").initCause(new SocketTimeoutException())).when(offUsTransactionService).doOffUsTransaction(ArgumentMatchers.any(Request.class), ArgumentMatchers.any());
       InteropTransactions interopTransactions = DataPreparationUtil.prepareInteropTransaction(transactionRequest, request, null);
       when(interopTransactionsRepository.save(ArgumentMatchers.any())).thenReturn(interopTransactions);
        createTransactionService.execute(request);
        Mockito.verify(offUsTransactionService, Mockito.times(1)).doOffUsTransaction(request, interopTransactions);
    }
    
    @Test
    public void validateCreateTransactionAPIWhenMappingCodeFailureCase() {
       
        TransactionRequest transactionRequest = DataPreparationUtil.prepareTransactionrequestObj();
        RequestValidationResponse requestValidationResponse = new RequestValidationResponse(
                SuccessStatus.SUCCESSFUL_REQUEST_VALIDATION.getStatusCode(),
                SuccessStatus.SUCCESSFUL_REQUEST_VALIDATION.getEntity().toString());
        when(requestValidations.validate(transactionRequest)).thenReturn(requestValidationResponse);
        Response response = new Response();
        MappingResponse mappingResponse = new MappingResponse();
        mappingResponse.setMappingCode(ThirdPartyResponseCodes.EIG_GENERAL_ERROR.getMappedCode());
        response.setMappingResponse(mappingResponse);
        BrokerResponse brokerResponse = new BrokerResponse();
        brokerResponse.setBrokerCode(ThirdPartyResponseCodes.EIG_GENERAL_ERROR.getMappedCode());
        response.setBrokerResponse(brokerResponse);
        Request request = new Request(transactionRequest);
        request.setInteropReferenceId(TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue());
        InteropTransactions interopTransactions = DataPreparationUtil.prepareInteropTransaction(transactionRequest, request, null);
        when(interopTransactionsRepository.save(ArgumentMatchers.any())).thenReturn(interopTransactions);
        createTransactionService.execute(request);
        Mockito.verify(offUsTransactionService, Mockito.times(1)).doOffUsTransaction(request, interopTransactions);
    }
    
   
    
    public String getPath(String interopReferenceId) {
		return resource.getNonFinancialServerUrlValue() + TestCaseConstants.BASE_CONTEXT.getValue()
				+ interopReferenceId + TestCaseConstants.BASE_CONTEXT_END.getValue();
	}
}
