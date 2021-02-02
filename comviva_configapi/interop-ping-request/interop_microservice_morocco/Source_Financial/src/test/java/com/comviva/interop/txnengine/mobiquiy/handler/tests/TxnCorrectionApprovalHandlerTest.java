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
import com.comviva.interop.txnengine.configuration.ServiceTemplateNames;
import com.comviva.interop.txnengine.configuration.BrokerServiceProperties;
import com.comviva.interop.txnengine.entities.InteropTransactions;
import com.comviva.interop.txnengine.enums.TestCaseConstants;
import com.comviva.interop.txnengine.mobiquity.service.handlers.TxnCorrectionApprovalHandler;
import com.comviva.interop.txnengine.mobiquity.service.handlers.UserEnquiryHandler;
import com.comviva.interop.txnengine.model.Request;
import com.comviva.interop.txnengine.model.TransactionRequest;
import com.comviva.interop.txnengine.repositories.InteropTransactionDetailsRepository;
import com.comviva.interop.txnengine.repositories.InteropTransactionsRepository;
import com.comviva.interop.txnengine.util.DataPreparationUtil;
import com.comviva.interop.txnengine.util.StringUtils;
import com.comviva.interop.txnengine.util.ThirdPartyCaller;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TxnCorrectionApprovalHandlerTest {

    @MockBean
    private BrokerServiceProperties thirdPartyProperties;

    @MockBean
    private ThirdPartyCaller thirdPartyCaller;

    @MockBean
    private InteropTransactionDetailsRepository interopTransactionDetailsRepository;

    @MockBean
    private InteropTransactionsRepository interOpTransactionRepository;

    @MockBean
    private UserEnquiryHandler userEnquiryHandler;
    
    @MockBean
    RestTemplate restTemplate;
    
    @MockBean
    private ServiceTemplateNames serviceTemplateNames;
    
    @InjectMocks
    @Autowired
    private TxnCorrectionApprovalHandler txnCorrectionApprovalHandler;
    
    @MockBean
    private BrokerServiceURLProperties brokerServiceURLProperties;

    @Test
    public void validateTxnCorrectionApproveHandlerSuccessCase() {
        TransactionRequest transactionRequest = DataPreparationUtil.prepareTransactionrequestObj();
        Request request = new Request(transactionRequest);
        request.setInteropReferenceId(TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue());
        when(brokerServiceURLProperties.getTxnCorrectionApproveUrl()).thenReturn(TestCaseConstants.TXN_CORRECTION_APPROVE_URL_VALUE.getValue());
        when(thirdPartyCaller.postMobiquityServiceRequest(ArgumentMatchers.anyMap(),ArgumentMatchers.any(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(StringUtils.xmlToModel(getTxnCorrectionApproveSuccessResponseXmlString()));
        
        InteropTransactions interopTransactions = DataPreparationUtil.prepareInteropTransaction(transactionRequest,
                request, TestCaseConstants.P2POFF_US_VALUE.getValue());
        txnCorrectionApprovalHandler.doTxnCorrectionApprove(interopTransactions, TestCaseConstants.EMPTY.getValue(), TestCaseConstants.DEFAULT_LANGUAGE_CODE.getValue(),
                TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue(), TestCaseConstants.TXN_ID_VALUE.getValue(),
                DataPreparationUtil.prepareChannelUserDetails(),TestCaseConstants.TXN_MODE_VAL_P2POFF.getValue(), "2345");
         Mockito.verify(thirdPartyCaller, Mockito.times(1)).postMobiquityServiceRequest(ArgumentMatchers.anyMap(),ArgumentMatchers.any(), ArgumentMatchers.anyString()
                 , ArgumentMatchers.anyString()
                 , ArgumentMatchers.anyString());
    }
    
    @Test
    public void validateTxnCorrectionApproveHandlerFailureCase() {
        TransactionRequest transactionRequest = DataPreparationUtil.prepareTransactionrequestObj();
        Request request = new Request(transactionRequest);
        request.setInteropReferenceId(TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue());
        when(brokerServiceURLProperties.getTxnCorrectionApproveUrl()).thenReturn(TestCaseConstants.TXN_CORRECTION_APPROVE_URL_VALUE.getValue());
        when(thirdPartyCaller.postMobiquityServiceRequest(ArgumentMatchers.anyMap(),ArgumentMatchers.any(), 
                ArgumentMatchers.anyString()
                , ArgumentMatchers.anyString()
                , ArgumentMatchers.anyString())).thenReturn(StringUtils.xmlToModel(getTxnCorrectionApproveFailureResponseXmlString()));
        
        InteropTransactions interopTransactions = DataPreparationUtil.prepareInteropTransaction(transactionRequest,
                request, TestCaseConstants.P2POFF_US_VALUE.getValue());
        txnCorrectionApprovalHandler.doTxnCorrectionApprove(interopTransactions, TestCaseConstants.EMPTY.getValue(), TestCaseConstants.DEFAULT_LANGUAGE_CODE.getValue(),
                TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue(), TestCaseConstants.TXN_ID_VALUE.getValue(),
                DataPreparationUtil.prepareChannelUserDetails(),TestCaseConstants.TXN_MODE_VAL_P2POFF.getValue(), "4567");
         Mockito.verify(thirdPartyCaller, Mockito.times(1)).postMobiquityServiceRequest(ArgumentMatchers.anyMap(),ArgumentMatchers.any(), ArgumentMatchers.anyString()
                 , ArgumentMatchers.anyString()
                 , ArgumentMatchers.anyString());
    }
    
    @Test
    public void validateTxnCorrectionApproveHandlerAmbiguousCase() {
        TransactionRequest transactionRequest = DataPreparationUtil.prepareTransactionrequestObj();
        Request request = new Request(transactionRequest);
        request.setInteropReferenceId(TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue());
        when(brokerServiceURLProperties.getTxnCorrectionApproveUrl()).thenReturn(TestCaseConstants.TXN_CORRECTION_APPROVE_URL_VALUE.getValue());
        when(thirdPartyCaller.postMobiquityServiceRequest(ArgumentMatchers.anyMap(),ArgumentMatchers.any(), ArgumentMatchers.anyString()
                , ArgumentMatchers.anyString()
                , ArgumentMatchers.anyString())).thenReturn(StringUtils.xmlToModel(getTxnCorrectionApproveAmbiguousResponseXmlString()));
        
        InteropTransactions interopTransactions = DataPreparationUtil.prepareInteropTransaction(transactionRequest,
                request, TestCaseConstants.P2POFF_US_VALUE.getValue());
        txnCorrectionApprovalHandler.doTxnCorrectionApprove(interopTransactions, TestCaseConstants.EMPTY.getValue(), TestCaseConstants.DEFAULT_LANGUAGE_CODE.getValue(),
                TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue(), TestCaseConstants.TXN_ID_VALUE.getValue(),
                DataPreparationUtil.prepareChannelUserDetails(),TestCaseConstants.TXN_MODE_VAL_P2POFF.getValue(), "56787");
         Mockito.verify(thirdPartyCaller, Mockito.times(1)).postMobiquityServiceRequest(ArgumentMatchers.anyMap(),ArgumentMatchers.any(), ArgumentMatchers.anyString()
                 , ArgumentMatchers.anyString()
                 , ArgumentMatchers.anyString());
    }

    private String getTxnCorrectionApproveSuccessResponseXmlString() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<response>"
                +   "<broker_response>"
                +       "<broker_code>200</broker_code>"
                +       "<broker_msg>ok</broker_msg>"
                +       "<call_wallet_id>22367</call_wallet_id>"
                +       "<session_id>BROKER-V3_SN_001_201930311115411657_358</session_id>"
                +   "</broker_response>"
                +   "<mapping_response>"
                +       "<mapping_code>SUCCESS</mapping_code>"
                +   "</mapping_response>"
                +   "<wallet_response>"
                +       "<type>TRCORCFRESP</type>"
                +       "<txnid>TC190311.1254.R00306</txnid>"
                +       "<txnstatus>200</txnstatus>"
                +       "<trid>763189890201903111254R7272</trid>"
                +       "<txnmode></txnmode>"
                +   "</wallet_response>"
                + "</response>";
    }
    
    private String getTxnCorrectionApproveFailureResponseXmlString() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<response>"
                +   "<broker_response>"
                +       "<broker_code>200</broker_code>"
                +       "<broker_msg>ok</broker_msg>"
                +       "<call_wallet_id>2237</call_wallet_id>"
                +       "<session_id>BROKER-V3_SN_001_20190311115411657_358</session_id>"
                +   "</broker_response>"
                +   "<mapping_response>"
                +       "<mapping_code>FAIL</mapping_code>"
                +   "</mapping_response>"
                +   "<wallet_response>"
                +       "<type>TRCORCFRESP</type>"
                +       "<txnid>TC190311.1254.R00306</txnid>"
                +       "<txnstatus>200</txnstatus>"
                +       "<trid>763189890201903111254R7272</trid>"
                +       "<txnmode></txnmode>"
                +   "</wallet_response>"
                + "</response>";
    }
    
    private String getTxnCorrectionApproveAmbiguousResponseXmlString() {
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
