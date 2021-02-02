package com.comviva.interop.txnengine.service.test;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestClientException;

import com.comviva.interop.txnengine.configuration.CurrencyLoader;
import com.comviva.interop.txnengine.configuration.EigTags;
import com.comviva.interop.txnengine.configuration.BrokerServiceProperties;
import com.comviva.interop.txnengine.configuration.BrokerServiceURLProperties;
import com.comviva.interop.txnengine.enums.TestCaseConstants;
import com.comviva.interop.txnengine.mobiquity.service.handlers.UserAuthenticationHandler;
import com.comviva.interop.txnengine.mobiquity.service.handlers.UserEnquiryHandler;
import com.comviva.interop.txnengine.model.Request;
import com.comviva.interop.txnengine.model.TransactionRequest;
import com.comviva.interop.txnengine.repositories.ChannelUserDetailsRepository;
import com.comviva.interop.txnengine.repositories.InteropTransactionDetailsRepository;
import com.comviva.interop.txnengine.repositories.InteropTransactionsRepository;
import com.comviva.interop.txnengine.request.validations.GetDescriptionForCode;
import com.comviva.interop.txnengine.services.OffUsTransactionHPSHandler;
import com.comviva.interop.txnengine.services.OffusHPS;
import com.comviva.interop.txnengine.util.DataPreparationUtil;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class P2POffUsTransactionHPSHandlerMockTest {

    @Mock
    private BrokerServiceProperties thirdPartyProperties;

    @Mock
    private OffusHPS p2pOffusHPS;

    @Mock
    private GetDescriptionForCode getDescriptionForCode;

    @Mock
    private InteropTransactionDetailsRepository interopTransactionDetailsRepository;

    @Mock
    private InteropTransactionsRepository interOpTransactionRepository;

    @Mock
    private ChannelUserDetailsRepository channelUserDetailsRepository;

    @Mock
    private CurrencyLoader currencyLoader;

    @Mock
    private EntityManager entityManager;

    @Mock
    private UserEnquiryHandler userEnquiryHandler;

    @Mock
    private UserAuthenticationHandler usAuthenticationHandler;
    
    @InjectMocks
    @Autowired
    private OffUsTransactionHPSHandler p2pOffUsTransactionHPSHandler;
    
    @Mock
    private EigTags eigTags;
    
    @Mock
    private BrokerServiceURLProperties brokerServiceURLProperties;
    

    @Test
    public void verifyP2POffUsTransactionHPSHandlerWithEmptyResponseCode() {
        TransactionRequest transactionRequest = DataPreparationUtil.prepareTransactionrequestObj();
        Request request = new Request(transactionRequest);
        request.setInteropReferenceId(TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue());
        when(thirdPartyProperties.getChannelUserMsisdn()).thenReturn(TestCaseConstants.CHANNEL_USER_MSISDN.getValue());
        Query mockedQuery = mock(Query.class);
        when(entityManager.createNativeQuery("select STAN_SEQID.nextval from dual")).thenReturn(mockedQuery);
        when(mockedQuery.getSingleResult()).thenReturn(new BigDecimal(1));
        when(channelUserDetailsRepository.findChannelUserDetailsByMsisdn(TestCaseConstants.CHANNEL_USER_MSISDN.getValue())).thenReturn( DataPreparationUtil.prepareChannelUserDetails());
        when(currencyLoader.getCurrencyByCode(transactionRequest.getCurrency())).thenReturn("INR");
        p2pOffUsTransactionHPSHandler.execute(request, TestCaseConstants.TXN_ID_VALUE.getValue(),TestCaseConstants.TXN_MODE_VAL_P2POFF.getValue(), DataPreparationUtil.prepareInteropTransaction(transactionRequest, request, null));
        Mockito.verify(channelUserDetailsRepository, Mockito.times(1)).findChannelUserDetailsByMsisdn(TestCaseConstants.CHANNEL_USER_MSISDN.getValue());
    }
    
    
    @Test
    public void verifyP2POffUsTransactionHPSHandlerWithSuccessResponseCode() {
        TransactionRequest transactionRequest = DataPreparationUtil.prepareTransactionrequestObj();
        Request request = new Request(transactionRequest);
        request.setInteropReferenceId(TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue());
        when(thirdPartyProperties.getChannelUserMsisdn()).thenReturn(TestCaseConstants.CHANNEL_USER_MSISDN.getValue());
        when(channelUserDetailsRepository.findChannelUserDetailsByMsisdn(TestCaseConstants.CHANNEL_USER_MSISDN.getValue())).thenReturn( DataPreparationUtil.prepareChannelUserDetails());
        Query mockedQuery = mock(Query.class);
        when(entityManager.createNativeQuery("select STAN_SEQID.nextval from dual")).thenReturn(mockedQuery);
        when(mockedQuery.getSingleResult()).thenReturn(new BigDecimal(1));
        when(currencyLoader.getCurrencyByCode(transactionRequest.getCurrency())).thenReturn("INR");
        when(eigTags.getStatusCodeTag()).thenReturn("statusCode");
        when(eigTags.getActionCodeTag()).thenReturn("actionCode");
        when(getDescriptionForCode.getMappingCode("00000")).thenReturn("SUCCESS");
        when(p2pOffusHPS.execute(ArgumentMatchers.any(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any())).thenReturn(DataPreparationUtil.getHPSTransactionResponse("00000"));
        p2pOffUsTransactionHPSHandler.execute(request, TestCaseConstants.TXN_ID_VALUE.getValue(),TestCaseConstants.TXN_MODE_VAL_P2POFF.getValue(), DataPreparationUtil.prepareInteropTransaction(transactionRequest, request, null));
        Mockito.verify(channelUserDetailsRepository, Mockito.times(1)).findChannelUserDetailsByMsisdn(TestCaseConstants.CHANNEL_USER_MSISDN.getValue());
    }
    
    @Test
    public void verifyP2POffUsTransactionHPSHandlerWithEIGTimeOutResponseCode() {
        TransactionRequest transactionRequest = DataPreparationUtil.prepareTransactionrequestObj();
        Request request = new Request(transactionRequest);
        request.setInteropReferenceId(TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue());
        when(thirdPartyProperties.getChannelUserMsisdn()).thenReturn(TestCaseConstants.CHANNEL_USER_MSISDN.getValue());
        when(channelUserDetailsRepository.findChannelUserDetailsByMsisdn(TestCaseConstants.CHANNEL_USER_MSISDN.getValue())).thenReturn( DataPreparationUtil.prepareChannelUserDetails());
        p2pOffUsTransactionHPSHandler.execute(request, TestCaseConstants.TXN_ID_VALUE.getValue(),TestCaseConstants.TXN_MODE_VAL_P2POFF.getValue(), DataPreparationUtil.prepareInteropTransaction(transactionRequest, request, null));
        Mockito.verify(channelUserDetailsRepository, Mockito.times(1)).findChannelUserDetailsByMsisdn(TestCaseConstants.CHANNEL_USER_MSISDN.getValue());
    }
    
    @Test
    public void verifyP2POffUsTransactionHPSHandlerWithEIGErrorResponseCode() {
        TransactionRequest transactionRequest = DataPreparationUtil.prepareTransactionrequestObj();
        Request request = new Request(transactionRequest);
        request.setInteropReferenceId(TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue());
        when(thirdPartyProperties.getChannelUserMsisdn()).thenReturn(TestCaseConstants.CHANNEL_USER_MSISDN.getValue());
        when(channelUserDetailsRepository.findChannelUserDetailsByMsisdn(TestCaseConstants.CHANNEL_USER_MSISDN.getValue())).thenReturn( DataPreparationUtil.prepareChannelUserDetails());
        when(currencyLoader.getCurrencyByCode(transactionRequest.getCurrency())).thenReturn("INR");
        Query mockedQuery = mock(Query.class);
        when(entityManager.createNativeQuery("select STAN_SEQID.nextval from dual")).thenReturn(mockedQuery);
        when(mockedQuery.getSingleResult()).thenReturn(new BigDecimal(1));
        when(eigTags.getStatusCodeTag()).thenReturn("statusCode");
        when(eigTags.getActionCodeTag()).thenReturn("actionCode");
        when(getDescriptionForCode.getMappingCode("000")).thenReturn("ERRORDEFAULT_INTERNAL");
        when(p2pOffusHPS.execute(ArgumentMatchers.any(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any())).thenReturn(DataPreparationUtil.getHPSTransactionResponse("000"));
        p2pOffUsTransactionHPSHandler.execute(request, TestCaseConstants.TXN_ID_VALUE.getValue(),TestCaseConstants.TXN_MODE_VAL_P2POFF.getValue(), DataPreparationUtil.prepareInteropTransaction(transactionRequest, request, null));
        Mockito.verify(channelUserDetailsRepository, Mockito.times(1)).findChannelUserDetailsByMsisdn(TestCaseConstants.CHANNEL_USER_MSISDN.getValue());
    }
    
    @Test
    public void verifyP2POffUsTransactionHPSHandlerWithRestClientException() {
        TransactionRequest transactionRequest = DataPreparationUtil.prepareTransactionrequestObj();
        Request request = new Request(transactionRequest);
        request.setInteropReferenceId(TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue());
        when(thirdPartyProperties.getChannelUserMsisdn()).thenReturn(TestCaseConstants.CHANNEL_USER_MSISDN.getValue());
        when(channelUserDetailsRepository.findChannelUserDetailsByMsisdn(TestCaseConstants.CHANNEL_USER_MSISDN.getValue())).thenReturn( DataPreparationUtil.prepareChannelUserDetails());
        when(currencyLoader.getCurrencyByCode(transactionRequest.getCurrency())).thenReturn("INR");
        Query mockedQuery = mock(Query.class);
        when(entityManager.createNativeQuery("select STAN_SEQID.nextval from dual")).thenReturn(mockedQuery);
        when(mockedQuery.getSingleResult()).thenReturn(new BigDecimal(1));
        doThrow(new RestClientException(TestCaseConstants.ERRORDEFAULT_INTERNAL.getValue())).when(p2pOffusHPS).execute(ArgumentMatchers.any(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any());
        p2pOffUsTransactionHPSHandler.execute(request, TestCaseConstants.TXN_ID_VALUE.getValue(),TestCaseConstants.TXN_MODE_VAL_P2POFF.getValue(), DataPreparationUtil.prepareInteropTransaction(transactionRequest, request, null));
        Mockito.verify(channelUserDetailsRepository, Mockito.times(1)).findChannelUserDetailsByMsisdn(TestCaseConstants.CHANNEL_USER_MSISDN.getValue());
    }
}
