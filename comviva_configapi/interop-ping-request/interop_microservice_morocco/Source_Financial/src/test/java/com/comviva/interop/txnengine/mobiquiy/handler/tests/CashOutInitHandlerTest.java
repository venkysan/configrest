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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit4.SpringRunner;

import com.comviva.interop.txnengine.configuration.BrokerServiceURLProperties;
import com.comviva.interop.txnengine.configuration.ChannelUserDetailsLoader;
import com.comviva.interop.txnengine.configuration.BrokerServiceProperties;
import com.comviva.interop.txnengine.entities.InteropTransactions;
import com.comviva.interop.txnengine.enums.TestCaseConstants;
import com.comviva.interop.txnengine.mobiquity.service.handlers.CashOutConfirmHandler;
import com.comviva.interop.txnengine.mobiquity.service.handlers.CashOutInitHandler;
import com.comviva.interop.txnengine.model.Request;
import com.comviva.interop.txnengine.model.TransactionRequest;
import com.comviva.interop.txnengine.repositories.InteropTransactionDetailsRepository;
import com.comviva.interop.txnengine.repositories.InteropTransactionsRepository;
import com.comviva.interop.txnengine.request.validations.GetDescriptionForCode;
import com.comviva.interop.txnengine.util.DataPreparationUtil;
import com.comviva.interop.txnengine.util.StringUtils;
import com.comviva.interop.txnengine.util.ThirdPartyCaller;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CashOutInitHandlerTest {

    @MockBean
    private BrokerServiceProperties thirdPartyProperties;
    
    @MockBean
    private ThirdPartyCaller thirdPartyCaller;

    @MockBean
    private InteropTransactionDetailsRepository interopTransactionDetailsRepository;

    @MockBean
    private ChannelUserDetailsLoader channelUserDetailsLoader;

    @MockBean
    private GetDescriptionForCode getDescriptionForCode;

    @MockBean
    private InteropTransactionsRepository interOpTransactionRepository;

    @MockBean
    private CashOutConfirmHandler cashOutConfirmHandler;

    @MockBean
    private ApplicationEventPublisher applicationEventPublisher;
    
    @InjectMocks
    @Autowired
    private CashOutInitHandler cashOutInitHandler;
    
    @MockBean
    private BrokerServiceURLProperties brokerServiceURLProperties;

    
    @Test
    public void validateCashOutInitSuccessCase() {
        TransactionRequest transactionRequest = DataPreparationUtil.prepareTransactionrequestObj();
        Request request = new Request(transactionRequest);
        InteropTransactions interopTransactions = DataPreparationUtil.prepareInteropTransaction(transactionRequest,
                request, TestCaseConstants.P2POFF_US_VALUE.getValue());
        request.setInteropReferenceId(TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue());
        when(brokerServiceURLProperties.getCashOutInitUrl()).thenReturn(TestCaseConstants.CASH_OUT_INIT_URL_VALUE.getValue());
        when(thirdPartyProperties.getChannelUserMsisdn()).thenReturn(TestCaseConstants.CHANNEL_USER_MSISDN.getValue());
        when(thirdPartyCaller.postMobiquityServiceRequest(ArgumentMatchers.anyMap(),ArgumentMatchers.any(), ArgumentMatchers.anyString(),ArgumentMatchers.anyString(),ArgumentMatchers.anyString())).thenReturn(StringUtils.xmlToModel(getCashOutInitSuccessResponseXmlString()));    
        when(channelUserDetailsLoader.channelUserDetailsByMsisdn(TestCaseConstants.CHANNEL_USER_MSISDN.getValue())).thenReturn( DataPreparationUtil.prepareChannelUserDetails());
        cashOutInitHandler.doCashOutInit(request, interopTransactions, transactionRequest);
         Mockito.verify(thirdPartyCaller, Mockito.times(1)).postMobiquityServiceRequest(ArgumentMatchers.anyMap(),ArgumentMatchers.any(), ArgumentMatchers.anyString(),
                 ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
    }
    
    @Test
    public void validateCashOutInitFailureCase() {
        TransactionRequest transactionRequest = DataPreparationUtil.prepareTransactionrequestObj();
        Request request = new Request(transactionRequest);
        InteropTransactions interopTransactions = DataPreparationUtil.prepareInteropTransaction(transactionRequest,
                request, TestCaseConstants.P2POFF_US_VALUE.getValue());
        request.setInteropReferenceId(TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue());
        when(brokerServiceURLProperties.getCashOutInitUrl()).thenReturn(TestCaseConstants.CASH_OUT_INIT_URL_VALUE.getValue());
        when(thirdPartyProperties.getChannelUserMsisdn()).thenReturn(TestCaseConstants.CHANNEL_USER_MSISDN.getValue());
        when(thirdPartyCaller.postMobiquityServiceRequest(ArgumentMatchers.anyMap(),ArgumentMatchers.any(), ArgumentMatchers.anyString(),ArgumentMatchers.anyString(),ArgumentMatchers.anyString())).thenReturn(StringUtils.xmlToModel(getCashOutInitFailureResponseXmlString()));    
        when(channelUserDetailsLoader.channelUserDetailsByMsisdn(TestCaseConstants.CHANNEL_USER_MSISDN.getValue())).thenReturn( DataPreparationUtil.prepareChannelUserDetails());
        cashOutInitHandler.doCashOutInit(request, interopTransactions, transactionRequest);
         Mockito.verify(thirdPartyCaller, Mockito.times(1)).postMobiquityServiceRequest(ArgumentMatchers.anyMap(),ArgumentMatchers.any(), 
                 ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
    }
    
    @Test
    public void validateCashOutInitAmbiguousCase() {
        TransactionRequest transactionRequest = DataPreparationUtil.prepareTransactionrequestObj();
        Request request = new Request(transactionRequest);
        InteropTransactions interopTransactions = DataPreparationUtil.prepareInteropTransaction(transactionRequest,
                request, TestCaseConstants.P2POFF_US_VALUE.getValue());
        request.setInteropReferenceId(TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue());
        when(brokerServiceURLProperties.getCashOutInitUrl()).thenReturn(TestCaseConstants.CASH_OUT_INIT_URL_VALUE.getValue());
        when(thirdPartyProperties.getChannelUserMsisdn()).thenReturn(TestCaseConstants.CHANNEL_USER_MSISDN.getValue());
        when(thirdPartyCaller.postMobiquityServiceRequest(ArgumentMatchers.anyMap(),ArgumentMatchers.any(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(StringUtils.xmlToModel(getCashOutInitAmbiguousResponseXmlString()));    
        when(channelUserDetailsLoader.channelUserDetailsByMsisdn(TestCaseConstants.CHANNEL_USER_MSISDN.getValue())).thenReturn( DataPreparationUtil.prepareChannelUserDetails());
        cashOutInitHandler.doCashOutInit(request, interopTransactions, transactionRequest);
         Mockito.verify(thirdPartyCaller, Mockito.times(1)).postMobiquityServiceRequest(ArgumentMatchers.anyMap(),ArgumentMatchers.any(), ArgumentMatchers.anyString(),
                 ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
    }

    private String getCashOutInitSuccessResponseXmlString() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                +   "<response>"
                +       "<broker_response>"
                +           "<broker_code>200</broker_code>"
                +           "<broker_msg>ok</broker_msg>"
                +           "<call_wallet_id>2171</call_wallet_id>"
                +           "<session_id>BROKER-V3_SN_001_20190311103309908_178</session_id>"
                +       "</broker_response>"
                +       "<mapping_response>"
                +           "<mapping_code>SUCCESS</mapping_code>"
                +       "</mapping_response>"
                +       "<wallet_response>"
                +           "<type>RCORESP</type>"
                +           "<txnid>CO190311.1133.R00023</txnid>"
                +           "<txnstatus>200</txnstatus>"
                +           "<trid>700110033201903111133R7148</trid>"
                +           "<message>Cash Out transaction is initiated succeesfully. Confirmation is sent to receiver.</message>"
                +           "<txnmode></txnmode>"
                +      "</wallet_response>"
                + "</response>";
    }
    
    private String getCashOutInitFailureResponseXmlString() {
        return"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                +   "<response>"
                +       "<broker_response>"
                +           "<broker_code>200</broker_code>"
                +           "<broker_msg>ok</broker_msg>"
                +           "<call_wallet_id>2171</call_wallet_id>"
                +           "<session_id>BROKER-V3_SN_001_20190311103309908_178</session_id>"
                +       "</broker_response>"
                +       "<mapping_response>"
                +           "<mapping_code>FAILURE</mapping_code>"
                +       "</mapping_response>"
                +       "<wallet_response>"
                +           "<type>RCORESP</type>"
                +           "<txnid>CO190311.1133.R00023</txnid>"
                +           "<txnstatus>400</txnstatus>"
                +           "<trid>700110033201903111133R7148</trid>"
                +           "<txnmode></txnmode>"
                +      "</wallet_response>"
                + "</response>";
    }
    
    private String getCashOutInitAmbiguousResponseXmlString() {
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
