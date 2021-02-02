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
import com.comviva.interop.txnengine.entities.SmsTemplates;
import com.comviva.interop.txnengine.enums.SMSNotificationCodes;
import com.comviva.interop.txnengine.enums.TestCaseConstants;
import com.comviva.interop.txnengine.mobiquity.service.handlers.CashOutConfirmHandler;
import com.comviva.interop.txnengine.model.Request;
import com.comviva.interop.txnengine.model.TransactionRequest;
import com.comviva.interop.txnengine.repositories.InteropTransactionDetailsRepository;
import com.comviva.interop.txnengine.repositories.InteropTransactionsRepository;
import com.comviva.interop.txnengine.repositories.SmsDeliveryRepository;
import com.comviva.interop.txnengine.repositories.SmsTemplatesRepository;
import com.comviva.interop.txnengine.request.validations.GetDescriptionForCode;
import com.comviva.interop.txnengine.services.OffUsTransactionHPSHandler;
import com.comviva.interop.txnengine.services.OffusHPS;
import com.comviva.interop.txnengine.util.DataPreparationUtil;
import com.comviva.interop.txnengine.util.StringUtils;
import com.comviva.interop.txnengine.util.ThirdPartyCaller;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CashOutConfirmHandlerTest {

    @MockBean
    private BrokerServiceProperties thirdPartyProperties;

    @MockBean
    private ThirdPartyCaller thirdPartyCaller;

    @MockBean
    private ChannelUserDetailsLoader channelUserDetailsLoader;

    @MockBean
    private GetDescriptionForCode getDescriptionForCode;

    @MockBean
    private InteropTransactionDetailsRepository interopTransactionDetailsRepository;

    @MockBean
    private InteropTransactionsRepository interOpTransactionRepository;

    @MockBean
    private SmsTemplatesRepository smsTemplatesRepository;

    @MockBean
    private SmsDeliveryRepository smsDeliveryRepository;

    @MockBean
    private OffUsTransactionHPSHandler transactionRequestHPSHandler;

    @MockBean
    private ApplicationEventPublisher applicationEventPublisher;
    
    @MockBean
    private BrokerServiceURLProperties brokerServiceURLProperties;

    
    @InjectMocks
    @Autowired
    private CashOutConfirmHandler cashOutConfirmHandler;
    @InjectMocks
    @Autowired
    private OffusHPS offusHPS;

    @Test
    public void validateCashOutConfirmSuccessCase() {
        TransactionRequest transactionRequest = DataPreparationUtil.prepareTransactionrequestObj();
        Request request = new Request(transactionRequest);
        InteropTransactions interopTransactions = DataPreparationUtil.prepareInteropTransaction(transactionRequest,
                request, TestCaseConstants.P2POFF_US_VALUE.getValue());
        request.setInteropReferenceId(TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue());
        when(brokerServiceURLProperties.getCashOutConfirmUrl())
                .thenReturn(TestCaseConstants.CASH_OUT_CONFIRM_URL_VALUE.getValue());
        when(thirdPartyProperties.getChannelUserMsisdn()).thenReturn(TestCaseConstants.CHANNEL_USER_MSISDN.getValue());
        when(thirdPartyCaller.postMobiquityServiceRequest(ArgumentMatchers.anyMap(), ArgumentMatchers.any(),
                ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                        .thenReturn(StringUtils.xmlToModel(getCashOutConfirmSuccessResponseXmlString()));
        when(channelUserDetailsLoader.channelUserDetailsByMsisdn(TestCaseConstants.CHANNEL_USER_MSISDN.getValue()))
                .thenReturn(DataPreparationUtil.prepareChannelUserDetails());
        offusHPS.execute(transactionRequest, "123456", "234567", "USD");
        cashOutConfirmHandler.doCashOutConfirm(interopTransactions, transactionRequest, request,TestCaseConstants.TXN_ID_VALUE.getValue(),TestCaseConstants.TXN_MODE_VAL_P2POFF.getValue());
        
        Mockito.verify(thirdPartyCaller, Mockito.times(1)).postMobiquityServiceRequest(ArgumentMatchers.anyMap(),
                ArgumentMatchers.any(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
    }

    @Test
    public void validateCashOutConfirmFailureCase() {
        TransactionRequest transactionRequest = DataPreparationUtil.prepareTransactionrequestObj();
        Request request = new Request(transactionRequest);
        InteropTransactions interopTransactions = DataPreparationUtil.prepareInteropTransaction(transactionRequest,
                request, TestCaseConstants.P2POFF_US_VALUE.getValue());
        request.setInteropReferenceId(TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue());
        when(brokerServiceURLProperties.getCashOutConfirmUrl())
                .thenReturn(TestCaseConstants.CASH_OUT_CONFIRM_URL_VALUE.getValue());
        when(thirdPartyProperties.getChannelUserMsisdn()).thenReturn(TestCaseConstants.CHANNEL_USER_MSISDN.getValue());
        when(thirdPartyCaller.postMobiquityServiceRequest(ArgumentMatchers.anyMap(), ArgumentMatchers.any(),
                ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                        .thenReturn(StringUtils.xmlToModel(getCashOutConfirmFailureResponseXmlString()));
        when(channelUserDetailsLoader.channelUserDetailsByMsisdn(TestCaseConstants.CHANNEL_USER_MSISDN.getValue()))
                .thenReturn(DataPreparationUtil.prepareChannelUserDetails());
        when(smsTemplatesRepository.findSmsTemplateByTypeAndLang( SMSNotificationCodes.OFF_US_CASH_OUT_CONFIRM_FAIL.toString(), TestCaseConstants.LANGUAGE_VALUE.getValue())).thenReturn(new SmsTemplates())
        ;
        cashOutConfirmHandler.doCashOutConfirm(interopTransactions, transactionRequest, request,TestCaseConstants.TXN_ID_VALUE.getValue(),TestCaseConstants.TXN_MODE_VAL_P2POFF.getValue());
        Mockito.verify(thirdPartyCaller, Mockito.times(1)).postMobiquityServiceRequest(ArgumentMatchers.anyMap(),
                ArgumentMatchers.any(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
    }

    @Test
    public void validateCashOutConfirmAmbiguousCase() {
        TransactionRequest transactionRequest = DataPreparationUtil.prepareTransactionrequestObj();
        Request request = new Request(transactionRequest);
        InteropTransactions interopTransactions = DataPreparationUtil.prepareInteropTransaction(transactionRequest,
                request, TestCaseConstants.P2POFF_US_VALUE.getValue());
        request.setInteropReferenceId(TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue());
        when(brokerServiceURLProperties.getCashOutConfirmUrl())
                .thenReturn(TestCaseConstants.CASH_OUT_CONFIRM_URL_VALUE.getValue());
        when(thirdPartyProperties.getChannelUserMsisdn()).thenReturn(TestCaseConstants.CHANNEL_USER_MSISDN.getValue());
        when(thirdPartyCaller.postMobiquityServiceRequest(ArgumentMatchers.anyMap(), ArgumentMatchers.any(),
                ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                        .thenReturn(StringUtils.xmlToModel(getCashOutConfirmAmbiguousResponseXmlString()));
        when(channelUserDetailsLoader.channelUserDetailsByMsisdn(TestCaseConstants.CHANNEL_USER_MSISDN.getValue()))
                .thenReturn(DataPreparationUtil.prepareChannelUserDetails());

        cashOutConfirmHandler.doCashOutConfirm(interopTransactions, transactionRequest, request, TestCaseConstants.TXN_ID_VALUE.getValue(),TestCaseConstants.TXN_MODE_VAL_P2POFF.getValue());
        Mockito.verify(thirdPartyCaller, Mockito.times(1)).postMobiquityServiceRequest(ArgumentMatchers.anyMap(),
                ArgumentMatchers.any(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
    }

    private String getCashOutConfirmSuccessResponseXmlString() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                +   "<response>"
                +       "<broker_response>"
                +           "<broker_code>200</broker_code>"
                +           "<broker_msg>ok</broker_msg>"
                +           "<call_wallet_id>2173</call_wallet_id>"
                +           "<session_id>BROKER-V3_SN_001_20190311103656641_146</session_id>"
                +       "</broker_response>"
                +       "<mapping_response>"
                +           "<mapping_code>SUCCESS</mapping_code>"
                +           "</mapping_response>"
                +       "<wallet_response>"
                +           "<type>RCORESP</type>"
                +           "<txnid>CO190311.1133.R00023</txnid>"
                +           "<txnstatus>200</txnstatus>"
                +           "<trid>768910909201903111136R7153</trid>"
                +           "<message>\"\"\"\"CashOut success by 700110033 IchRet from 768910909 test1. "
                +               " The details are as follows: transaction amount: 101.00 FCFA, transaction Id: CO190311.1133.R00023,"
                +               " net debit amount 1.00FCFA, new balance: 990028412.87 FCFA.\"\"\"\"</message>"
                +           "<txnmode></txnmode>"
                +       "</wallet_response>"
                + "</response>";
    }

    private String getCashOutConfirmFailureResponseXmlString() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                +   "<response>"
                +       "<broker_response>"
                +           "<broker_code>200</broker_code>"
                +           "<broker_msg>ok</broker_msg>"
                +           "<call_wallet_id>2173</call_wallet_id>"
                +           "<session_id>BROKER-V3_SN_001_20190311103656641_146</session_id>"
                +       "</broker_response>"
                +       "<mapping_response>"
                +           "<mapping_code>FAILURE</mapping_code>"
                +           "</mapping_response>"
                +       "<wallet_response>"
                +           "<type>RCORESP</type>"
                +           "<txnid>CO190311.1133.R00023</txnid>"
                +           "<txnstatus>400</txnstatus>"
                +           "<trid>768910909201903111136R7153</trid>"
                +           "<txnmode></txnmode>"
                +       "</wallet_response>"
                + "</response>";
    }

    private String getCashOutConfirmAmbiguousResponseXmlString() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + "<response>" + "<broker_response>"
                + "<broker_code>200</broker_code>" + "<broker_msg>ok</broker_msg>"
                + "<call_wallet_id>2237</call_wallet_id>"
                + "<session_id>BROKER-V3_SN_001_20190311115411657_358</session_id>" + "</broker_response>"
                + "</response>";
    }

}
