
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
import com.comviva.interop.txnengine.entities.ChannelUserDetails;
import com.comviva.interop.txnengine.entities.InteropTransactions;
import com.comviva.interop.txnengine.enums.TestCaseConstants;
import com.comviva.interop.txnengine.mobiquity.service.handlers.TxnCorrectionApprovalHandler;
import com.comviva.interop.txnengine.mobiquity.service.handlers.TxnCorrectionInitHandler;
import com.comviva.interop.txnengine.model.Request;
import com.comviva.interop.txnengine.model.TransactionRequest;
import com.comviva.interop.txnengine.model.TxnCorrection;
import com.comviva.interop.txnengine.repositories.InteropTransactionDetailsRepository;
import com.comviva.interop.txnengine.repositories.InteropTransactionsRepository;
import com.comviva.interop.txnengine.util.DataPreparationUtil;
import com.comviva.interop.txnengine.util.StringUtils;
import com.comviva.interop.txnengine.util.ThirdPartyCaller;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TxnCorrectionInitHandlerTest {

    @MockBean
    private BrokerServiceProperties thirdPartyProperties;

    @MockBean
    private ThirdPartyCaller thirdPartyCaller;

    @MockBean
    private InteropTransactionDetailsRepository interopTransactionDetailsRepository;

    @MockBean
    private InteropTransactionsRepository interOpTransactionRepository;

    @MockBean
    private TxnCorrectionApprovalHandler txnCorrectionApprovalHandler;
    
    @MockBean
    RestTemplate restTemplate;
    
    @MockBean
    private ServiceTemplateNames serviceTemplateNames;
    
    @InjectMocks
    @Autowired
    private TxnCorrectionInitHandler txnCorrectionInitHandler;
    
    @MockBean
    private BrokerServiceURLProperties brokerServiceURLProperties;

    @Test
    public void validateTxnCorrectionInitHandlerSuccessCase() {
        TransactionRequest transactionRequest = DataPreparationUtil.prepareTransactionrequestObj();
        Request request = new Request(transactionRequest);
        InteropTransactions interopTransactions = DataPreparationUtil.prepareInteropTransaction(transactionRequest,
                request, TestCaseConstants.P2POFF_US_VALUE.getValue());
        ChannelUserDetails channelUserDetails = DataPreparationUtil.prepareChannelUserDetails();
        request.setInteropReferenceId(TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue());
        when(brokerServiceURLProperties.getTxnCorrectionInitUrl()).thenReturn(TestCaseConstants.TXN_CORRECTION_INIT_URL_VALUE.getValue());
        when(thirdPartyProperties.getTxnCorrectionRemarks()).thenReturn(TestCaseConstants.REMARKS.getValue());
        when(thirdPartyProperties.getTxnCorrectionActionType()).thenReturn(TestCaseConstants.ACTION.getValue());
        when(thirdPartyProperties.getTxnScreversal()).thenReturn(TestCaseConstants.SCREVERSAL.getValue());
        when(txnCorrectionApprovalHandler.prepareTxnCorrection(interopTransactions, TestCaseConstants.TXN_ID_VALUE.getValue(), channelUserDetails, TestCaseConstants.ACTION.getValue())).thenReturn(new TxnCorrection());
        when(thirdPartyCaller.postMobiquityServiceRequest(ArgumentMatchers.anyMap(),ArgumentMatchers.any(), ArgumentMatchers.anyString()
                , ArgumentMatchers.anyString()
                , ArgumentMatchers.anyString())).thenReturn(StringUtils.xmlToModel(getTxnCorrectionInitSuccessResponseXmlString()));
        
        
        txnCorrectionInitHandler.doTxnCorrectionInitiation(interopTransactions, TestCaseConstants.EMPTY.getValue(),TestCaseConstants.DEFAULT_LANGUAGE_CODE.getValue(),
        		TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue(),
                TestCaseConstants.TXN_ID_VALUE.getValue(),channelUserDetails,TestCaseConstants.TXN_MODE_VAL_P2POFF.getValue(), "34556");
         Mockito.verify(thirdPartyCaller, Mockito.times(1)).postMobiquityServiceRequest(ArgumentMatchers.anyMap(),ArgumentMatchers.any(), ArgumentMatchers.anyString()
                 , ArgumentMatchers.anyString()
                 , ArgumentMatchers.anyString());
    }
    
    @Test
    public void validateTxnCorrectionInitHandlerFailureCase() {
        TransactionRequest transactionRequest = DataPreparationUtil.prepareTransactionrequestObj();
        Request request = new Request(transactionRequest);
        InteropTransactions interopTransactions = DataPreparationUtil.prepareInteropTransaction(transactionRequest,
                request, TestCaseConstants.P2POFF_US_VALUE.getValue());
        ChannelUserDetails channelUserDetails = DataPreparationUtil.prepareChannelUserDetails();
        request.setInteropReferenceId(TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue());
        when(brokerServiceURLProperties.getTxnCorrectionInitUrl()).thenReturn(TestCaseConstants.TXN_CORRECTION_INIT_URL_VALUE.getValue());
        when(thirdPartyProperties.getTxnCorrectionRemarks()).thenReturn(TestCaseConstants.REMARKS.getValue());
        when(thirdPartyProperties.getTxnCorrectionActionType()).thenReturn(TestCaseConstants.ACTION.getValue());
        when(thirdPartyProperties.getTxnScreversal()).thenReturn(TestCaseConstants.SCREVERSAL.getValue());
        when(txnCorrectionApprovalHandler.prepareTxnCorrection(interopTransactions, TestCaseConstants.TXN_ID_VALUE.getValue(), channelUserDetails, TestCaseConstants.ACTION.getValue())).thenReturn(new TxnCorrection());
        when(thirdPartyCaller.postMobiquityServiceRequest(ArgumentMatchers.anyMap(),ArgumentMatchers.any(), ArgumentMatchers.anyString()
                , ArgumentMatchers.anyString()
                , ArgumentMatchers.anyString())).thenReturn(StringUtils.xmlToModel(getTxnCorrectionInitFailureResponseXmlString()));
        
        
        txnCorrectionInitHandler.doTxnCorrectionInitiation(interopTransactions, TestCaseConstants.EMPTY.getValue(),TestCaseConstants.DEFAULT_LANGUAGE_CODE.getValue(), TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue(),
                TestCaseConstants.TXN_ID_VALUE.getValue(),channelUserDetails,TestCaseConstants.TXN_MODE_VAL_P2POFF.getValue(),"4567");
         Mockito.verify(thirdPartyCaller, Mockito.times(1)).postMobiquityServiceRequest(ArgumentMatchers.anyMap(),ArgumentMatchers.any(), ArgumentMatchers.anyString()
                 , ArgumentMatchers.anyString()
                 , ArgumentMatchers.anyString());
    }
    
    @Test
    public void validateTxnCorrectionInitHandlerAmbiguousCase() {
        TransactionRequest transactionRequest = DataPreparationUtil.prepareTransactionrequestObj();
        Request request = new Request(transactionRequest);
        InteropTransactions interopTransactions = DataPreparationUtil.prepareInteropTransaction(transactionRequest,
                request, TestCaseConstants.P2POFF_US_VALUE.getValue());
        ChannelUserDetails channelUserDetails = DataPreparationUtil.prepareChannelUserDetails();
        request.setInteropReferenceId(TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue());
        when(brokerServiceURLProperties.getTxnCorrectionInitUrl()).thenReturn(TestCaseConstants.TXN_CORRECTION_INIT_URL_VALUE.getValue());
        when(thirdPartyProperties.getTxnCorrectionRemarks()).thenReturn(TestCaseConstants.REMARKS.getValue());
        when(thirdPartyProperties.getTxnCorrectionActionType()).thenReturn(TestCaseConstants.ACTION.getValue());
        when(thirdPartyProperties.getTxnScreversal()).thenReturn(TestCaseConstants.SCREVERSAL.getValue());
        when(txnCorrectionApprovalHandler.prepareTxnCorrection(interopTransactions, TestCaseConstants.TXN_ID_VALUE.getValue(), channelUserDetails, TestCaseConstants.ACTION.getValue())).thenReturn(new TxnCorrection());
        when(thirdPartyCaller.postMobiquityServiceRequest(ArgumentMatchers.anyMap(),ArgumentMatchers.any(), ArgumentMatchers.anyString()
                , ArgumentMatchers.anyString()
                , ArgumentMatchers.anyString())).thenReturn(StringUtils.xmlToModel(getTxnCorrectionInitAmbiguousResponseXmlString()));
        
        
        txnCorrectionInitHandler.doTxnCorrectionInitiation(interopTransactions, TestCaseConstants.EMPTY.getValue(),TestCaseConstants.DEFAULT_LANGUAGE_CODE.getValue(), TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue(),
                TestCaseConstants.TXN_ID_VALUE.getValue(),channelUserDetails,TestCaseConstants.TXN_MODE_VAL_P2POFF.getValue(), "4567");
         Mockito.verify(thirdPartyCaller, Mockito.times(1)).postMobiquityServiceRequest(ArgumentMatchers.anyMap(),ArgumentMatchers.any(), ArgumentMatchers.anyString()
                 , ArgumentMatchers.anyString()
                 , ArgumentMatchers.anyString());
    }

    private String getTxnCorrectionInitSuccessResponseXmlString() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                +   "<response>"
                +       "<broker_response>"
                +           "<broker_code>200</broker_code>"
                +           "<broker_msg>ok</broker_msg>"
                +           "<call_wallet_id>2237</call_wallet_id>"
                +           "<session_id>BROKER-V3_SN_001_201903111154116577_358</session_id>"
                +           "</broker_response>"
                +       "<mapping_response>"
                +           "<mapping_code>SUCCESS</mapping_code>"
                +       "</mapping_response>"
                +       "<wallet_response>"
                +           "<type>TRCORINRESP</type>"
                +           "<txnid>TC190311.1254.R00306</txnid>"
                +           "<txnstatus>200</txnstatus>"
                +           "<trid>763189890201903111254R7272</trid>"
                +           "<txnmode></txnmode>"
                +           "<frozenbal>120</frozenbal>"
                +       "</wallet_response>"
                + "</response>";
    }
    
    private String getTxnCorrectionInitFailureResponseXmlString() {
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
                +       "<type>TRCORINRESP</type>"
                +       "<txnid>TC190311.1254.R00306</txnid>"
                +       "<txnstatus>200</txnstatus>"
                +       "<trid>763189890201903111254R7272</trid>"
                +       "<txnmode></txnmode>"
                +   "</wallet_response>"
                + "</response>";
    }
    
    private String getTxnCorrectionInitAmbiguousResponseXmlString() {
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
