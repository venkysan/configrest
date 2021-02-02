package com.comviva.interop.txnengine.mobiquiy.handler.tests;

import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.comviva.interop.txnengine.configuration.BrokerServiceURLProperties;
import com.comviva.interop.txnengine.configuration.BrokerServiceProperties;
import com.comviva.interop.txnengine.entities.ChannelUserDetails;
import com.comviva.interop.txnengine.entities.InteropTransactions;
import com.comviva.interop.txnengine.enums.TestCaseConstants;
import com.comviva.interop.txnengine.mobiquity.service.handlers.TxnCorrectionInitHandler;
import com.comviva.interop.txnengine.mobiquity.service.handlers.UserAuthenticationHandler;
import com.comviva.interop.txnengine.model.Request;
import com.comviva.interop.txnengine.model.TransactionRequest;
import com.comviva.interop.txnengine.repositories.InteropTransactionDetailsRepository;
import com.comviva.interop.txnengine.repositories.InteropTransactionsRepository;
import com.comviva.interop.txnengine.util.DataPreparationUtil;
import com.comviva.interop.txnengine.util.StringUtils;
import com.comviva.interop.txnengine.util.ThirdPartyCaller;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserAuthenticationHandlerTest {

    @MockBean
    private BrokerServiceProperties thirdPartyProperties;

    @MockBean
    private ThirdPartyCaller thirdPartyCaller;

    @MockBean
    private InteropTransactionDetailsRepository interopTransactionDetailsRepository;

    @MockBean
    private InteropTransactionsRepository interOpTransactionRepository;

    @MockBean
    private TxnCorrectionInitHandler txnCorrectionInitHandler;
    
    @MockBean
    RestTemplate restTemplate;
    
    @InjectMocks
    @Autowired
    private UserAuthenticationHandler userAuthenticationHandler;
    
    @MockBean
    private BrokerServiceURLProperties brokerServiceURLProperties;

    @Test
    public void validateUserAuthenticationHandlerSuccessCase() {
        TransactionRequest transactionRequest = DataPreparationUtil.prepareTransactionrequestObj();
        Request request = new Request(transactionRequest);
        InteropTransactions interopTransactions = DataPreparationUtil.prepareInteropTransaction(transactionRequest,
                request, TestCaseConstants.P2POFF_US_VALUE.getValue());
        ChannelUserDetails channelUserDetails = DataPreparationUtil.prepareChannelUserDetails();
        request.setInteropReferenceId(TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue());
        when(brokerServiceURLProperties.getUserAuthUrl()).thenReturn(TestCaseConstants.USER_AUTH_URL_VALUE.getValue());
        when(thirdPartyCaller.postMobiquityServiceRequest(ArgumentMatchers.anyMap(),ArgumentMatchers.any(), ArgumentMatchers.anyString()
                , ArgumentMatchers.anyString()
                , ArgumentMatchers.anyString())).thenReturn(StringUtils.xmlToModel(getUserAuthenticationSuccessResponseXmlString()));        
        userAuthenticationHandler.doTxnCorrection(interopTransactions, TestCaseConstants.EMPTY.getValue(),TestCaseConstants.DEFAULT_LANGUAGE_CODE.getValue(), TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue(),
                TestCaseConstants.TXN_ID_VALUE.getValue(),channelUserDetails,TestCaseConstants.TXN_MODE_VAL_P2POFF.getValue(), "3456");
         Mockito.verify(thirdPartyCaller, Mockito.times(1)).postMobiquityServiceRequest(ArgumentMatchers.anyMap(),ArgumentMatchers.any(), ArgumentMatchers.anyString()
                 , ArgumentMatchers.anyString()
                 , ArgumentMatchers.anyString());
    }
    
    @Test
    public void validateUserAuthenticationHandlerFailureCase() {
        TransactionRequest transactionRequest = DataPreparationUtil.prepareTransactionrequestObj();
        Request request = new Request(transactionRequest);
        InteropTransactions interopTransactions = DataPreparationUtil.prepareInteropTransaction(transactionRequest,
                request, TestCaseConstants.P2POFF_US_VALUE.getValue());
        ChannelUserDetails channelUserDetails = DataPreparationUtil.prepareChannelUserDetails();
        request.setInteropReferenceId(TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue());
        when(brokerServiceURLProperties.getUserAuthUrl()).thenReturn(TestCaseConstants.USER_AUTH_URL_VALUE.getValue());
        when(thirdPartyCaller.postMobiquityServiceRequest(ArgumentMatchers.anyMap(),ArgumentMatchers.any(), ArgumentMatchers.anyString()
                , ArgumentMatchers.anyString()
                , ArgumentMatchers.anyString())).thenReturn(StringUtils.xmlToModel(getUserAuthenticationFailureResponseXmlString()));        
        userAuthenticationHandler.doTxnCorrection(interopTransactions, TestCaseConstants.EMPTY.getValue(),TestCaseConstants.DEFAULT_LANGUAGE_CODE.getValue(), TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue(),
                TestCaseConstants.TXN_ID_VALUE.getValue(),channelUserDetails,TestCaseConstants.TXN_MODE_VAL_P2POFF.getValue(), "5678");
         Mockito.verify(thirdPartyCaller, Mockito.times(1)).postMobiquityServiceRequest(ArgumentMatchers.anyMap(),ArgumentMatchers.any(), ArgumentMatchers.anyString()
                 , ArgumentMatchers.anyString()
                 , ArgumentMatchers.anyString());
    }
    
    @Test
    public void validateUserAuthenticationHandlerAmbiguousCase() {
        TransactionRequest transactionRequest = DataPreparationUtil.prepareTransactionrequestObj();
        Request request = new Request(transactionRequest);
        InteropTransactions interopTransactions = DataPreparationUtil.prepareInteropTransaction(transactionRequest,
                request, TestCaseConstants.P2POFF_US_VALUE.getValue());
        ChannelUserDetails channelUserDetails = DataPreparationUtil.prepareChannelUserDetails();
        request.setInteropReferenceId(TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue());
        when(brokerServiceURLProperties.getUserAuthUrl()).thenReturn(TestCaseConstants.USER_AUTH_URL_VALUE.getValue());
        when(thirdPartyCaller.postMobiquityServiceRequest(ArgumentMatchers.anyMap(),ArgumentMatchers.any(), ArgumentMatchers.anyString()
                , ArgumentMatchers.anyString()
                , ArgumentMatchers.anyString())).thenReturn(StringUtils.xmlToModel(getUserAuthenticationAmbiguousResponseXmlString()));        
        userAuthenticationHandler.doTxnCorrection(interopTransactions, TestCaseConstants.EMPTY.getValue(),TestCaseConstants.DEFAULT_LANGUAGE_CODE.getValue(), TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue(),
                TestCaseConstants.TXN_ID_VALUE.getValue(),channelUserDetails,TestCaseConstants.TXN_MODE_VAL_P2POFF.getValue(), "56789");
         Mockito.verify(thirdPartyCaller, Mockito.times(1)).postMobiquityServiceRequest(ArgumentMatchers.anyMap(),ArgumentMatchers.any(), ArgumentMatchers.anyString()
                 , ArgumentMatchers.anyString()
                 , ArgumentMatchers.anyString());
    }

    private String getUserAuthenticationSuccessResponseXmlString() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                +   "<response>"
                +       "<broker_response>"
                +           "<broker_code>200</broker_code>"
                +           "<broker_msg>ok</broker_msg>"
                +           "<call_wallet_id>2202</call_wallet_id>"
                +           "<session_id>BROKER-V3_SN_001_20190311113136138_216</session_id>"
                +       "</broker_response>"
                +       "<mapping_response>"
                +           "<mapping_code>SUCCESS</mapping_code>"
                +           "</mapping_response>"
                +       "<wallet_response>"
                +           "<type>RESUSERAUTH</type>"
                +            "<txnstatus>200</txnstatus>"
                +            "<trid>201903111231R7225</trid>"
                +            "<message>Success</message>"
                +            "<msisdn>784444444</msisdn>"
                +            "<reqstatus>200</reqstatus>"
                +            "<userid>PT171213.1203.210139</userid>"
                +            "<userstatus>ACTIVE</userstatus>"
                +             "<barredtype></barredtype>"
                +             "<token>eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJvbXRvb2xib3giLCJzdWIiOiJ6ZWVuZXRhZG1pbiIsImlhdCI6MTU1MjMwMzg5Niw"
                +                       "iZXhwIjoxNTUyMzA0Nzk2LCJuYmYiOjE1NTIzMDM4OTZ9.eXxrUg0LO9uMC2EYnydoN13riTtQqTo_yumGH0o7xe4</token>"
                +       "</wallet_response>"
                + "</response>";
    }
    
    private String getUserAuthenticationFailureResponseXmlString() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                +   "<response>"
                +       "<broker_response>"
                +           "<broker_code>200</broker_code>"
                +           "<broker_msg>ok</broker_msg>"
                +           "<call_wallet_id>2202</call_wallet_id>"
                +           "<session_id>BROKER-V3_SN_001_20190311113136138_216</session_id>"
                +       "</broker_response>"
                +       "<mapping_response>"
                +           "<mapping_code>Failure</mapping_code>"
                +           "</mapping_response>"
                +       "<wallet_response>"
                +           "<type>RESUSERAUTH</type>"
                +            "<txnstatus>400</txnstatus>"
                +            "<trid>201903111231R7225</trid>"
                +            "<message>Fail</message>"
                +            "<msisdn>784444444</msisdn>"
                +            "<reqstatus>200</reqstatus>"
                +            "<userid>PT171213.1203.210139</userid>"
                +            "<userstatus>ACTIVE</userstatus>"
                +             "<barredtype></barredtype>"
                +             "<token>eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJvbXRvb2xib3giLCJzdWIiOiJ6ZWVuZXRhZG1pbiIsImlhdCI6MTU1MjMwMzg5Niw"
                +                       "iZXhwIjoxNTUyMzA0Nzk2LCJuYmYiOjE1NTIzMDM4OTZ9.eXxrUg0LO9uMC2EYnydoN13riTtQqTo_yumGH0o7xe4</token>"
                +       "</wallet_response>"
                + "</response>";
    }
    
    private String getUserAuthenticationAmbiguousResponseXmlString() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<response>"
                +   "<broker_response>"
                +       "<broker_code>200</broker_code>"
                +       "<broker_msg>ok</broker_msg>"
                +       "<call_wallet_id>2237</call_wallet_id>"
                +       "<session_id>BROKER-V3_SN_001_20190311115411657_358</session_id>"
                +   "</broker_response>"
                + "</response>"
                ;
    }

}
