package com.comviva.interop.txnengine.service.test;

import static io.restassured.RestAssured.with;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.net.SocketTimeoutException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.comviva.interop.txnengine.enums.InteropResponseCodes;
import com.comviva.interop.txnengine.enums.TestCaseConstants;
import com.comviva.interop.txnengine.enums.ThirdPartyResponseCodes;
import com.comviva.interop.txnengine.exception.InteropException;
import com.comviva.interop.txnengine.mobiquity.service.handlers.CashOutInitHandler;
import com.comviva.interop.txnengine.mobiquity.service.handlers.P2PHandler;
import com.comviva.interop.txnengine.model.BrokerResponse;
import com.comviva.interop.txnengine.model.MappingResponse;
import com.comviva.interop.txnengine.model.Response;
import com.comviva.interop.txnengine.model.WalletResponse;
import com.comviva.interop.txnengine.services.GetDefaultWalletStatusHandler;
import com.comviva.interop.txnengine.services.OffUsTransactionService;
import com.comviva.interop.txnengine.services.P2POnUsTransactionService;
import com.comviva.interop.txnengine.util.DataPreparationUtil;
import com.comviva.interop.txnengine.util.ThirdPartyCaller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class P2PTransactionServiceMockTest {
	
	@LocalServerPort
	private int port;
	
	@MockBean
    private GetDefaultWalletStatusHandler getDefaultWalletStatusHandler;
	
	@MockBean
	private CashOutInitHandler cashOutInitHandler;
	
	 @MockBean
	 private P2PHandler mobiquityp2pHandler;

	 @InjectMocks
	 @Autowired
	 private P2POnUsTransactionService p2pOnUsTransactionService;
	 
	 
	@InjectMocks
	@Autowired
	private OffUsTransactionService offUsTransactionService;
	
	@MockBean
	RestTemplate restTemplate;

	@InjectMocks
	@Autowired
	private ThirdPartyCaller thirdPartyCaller;
	
	@Before
	public void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
	}
	
	
	
	@Test
    public void verifyP2POffUsInterOpException() {
		when(restTemplate.postForObject(ArgumentMatchers.anyString(),ArgumentMatchers.any(),ArgumentMatchers.<Class<String>>any())).thenReturn(getXmlResponseString());
		when(getDefaultWalletStatusHandler.isUserRegisteredWithSameMFS(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(Boolean.FALSE);
		doThrow(new InteropException(InteropResponseCodes.INTERNAL_ERROR.getStatusCode(),TestCaseConstants.ERRORDEFAULT_INTERNAL.getValue())).when(cashOutInitHandler).doCashOutInit(Mockito.any(), Mockito.any(), Mockito.any());
		with().contentType(ContentType.JSON).accept(ContentType.JSON).body(getTransactionRequest()).given().header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue()).when().post(TestCaseConstants.TRANSACTIONS_BASE_CONTEXT.getValue()).then().assertThat().statusCode(HttpStatus.OK.value())
		.and().assertThat()
		.body(TestCaseConstants.MAPPED_CODE_NAME.getValue(),
				equalTo(TestCaseConstants.ERRORDEFAULT_INTERNAL.getValue()));
	}
	
	@Test
    public void verifyP2POffUsRestClientException() {
		when(restTemplate.postForObject(ArgumentMatchers.anyString(),ArgumentMatchers.any(),ArgumentMatchers.<Class<String>>any())).thenReturn(getXmlResponseString());
		when(getDefaultWalletStatusHandler.isUserRegisteredWithSameMFS(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(Boolean.FALSE);
		doThrow(new RestClientException(TestCaseConstants.ERRORDEFAULT_INTERNAL.getValue())).when(cashOutInitHandler).doCashOutInit(Mockito.any(), Mockito.any(), Mockito.any());
		with().contentType(ContentType.JSON).accept(ContentType.JSON).body(getTransactionRequest()).given().header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue()).when().post(TestCaseConstants.TRANSACTIONS_BASE_CONTEXT.getValue()).then().assertThat().statusCode(HttpStatus.OK.value())
		.and().assertThat()
		.body(TestCaseConstants.MAPPED_CODE_NAME.getValue(),
				equalTo(TestCaseConstants.ERRORDEFAULT_INTERNAL.getValue()));
	}
	
	@Test
    public void verifyP2POffUsException() {
		when(restTemplate.postForObject(ArgumentMatchers.anyString(),ArgumentMatchers.any(),ArgumentMatchers.<Class<String>>any())).thenReturn(getXmlResponseString());
		when(getDefaultWalletStatusHandler.isUserRegisteredWithSameMFS(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(Boolean.FALSE);
		doThrow(new RuntimeException()).when(cashOutInitHandler).doCashOutInit(Mockito.any(), Mockito.any(), Mockito.any());
		with().contentType(ContentType.JSON).accept(ContentType.JSON).body(getTransactionRequest()).given().header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue()).when().post(TestCaseConstants.TRANSACTIONS_BASE_CONTEXT.getValue()).then().assertThat().statusCode(HttpStatus.OK.value())
		.and().assertThat()
		.body(TestCaseConstants.MAPPED_CODE_NAME.getValue(),
				equalTo(TestCaseConstants.ERRORDEFAULT_INTERNAL.getValue()));
	}
	
	@Test
    public void verifyP2POffUsSocketTimeOutException() {
        when(restTemplate.postForObject(ArgumentMatchers.anyString(),ArgumentMatchers.any(),ArgumentMatchers.<Class<String>>any())).thenReturn(getXmlResponseString());
        when(getDefaultWalletStatusHandler.isUserRegisteredWithSameMFS(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(Boolean.FALSE);
        doThrow(new RestClientException(TestCaseConstants.ERRORDEFAULT_INTERNAL.getValue()).initCause(new SocketTimeoutException())).when(cashOutInitHandler).doCashOutInit(Mockito.any(), Mockito.any(), Mockito.any());
        with().contentType(ContentType.JSON).accept(ContentType.JSON).body(getTransactionRequest()).given().header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue()).when().post(TestCaseConstants.TRANSACTIONS_BASE_CONTEXT.getValue()).then().assertThat().statusCode(HttpStatus.OK.value())
        .and().assertThat()
        .body(TestCaseConstants.MAPPED_CODE_NAME.getValue(),
                equalTo(TestCaseConstants.ERRORDEFAULT_INTERNAL.getValue()));
    }
	
	@Test
    public void verifyP2POnUsTransactionSuccess() {
		when(restTemplate.postForObject(ArgumentMatchers.anyString(),ArgumentMatchers.any(),ArgumentMatchers.<Class<String>>any())).thenReturn(getXmlResponseString());
		when(getDefaultWalletStatusHandler.isUserRegisteredWithSameMFS(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(Boolean.TRUE);
		Response response = new Response();
        MappingResponse mappingResponse = new MappingResponse();
        mappingResponse.setMappingCode(ThirdPartyResponseCodes.SUCCESS.getMappedCode());
        response.setMappingResponse(mappingResponse);
		when(mobiquityp2pHandler.execute(Mockito.any(), Mockito.anyString(),Mockito.anyString(),Mockito.anyString())).thenReturn(response);
		with().contentType(ContentType.JSON).accept(ContentType.JSON).body(getTransactionRequest()).given().header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue()).when().post(TestCaseConstants.TRANSACTIONS_BASE_CONTEXT.getValue()).then().assertThat().statusCode(HttpStatus.OK.value())
		.and().assertThat()
		.body(TestCaseConstants.MAPPED_CODE_NAME.getValue(),
				equalTo(TestCaseConstants.ERRORDEFAULT_INTERNAL.getValue()));
	}
	
	@Test
    public void verifyP2POnUsTransactionFailureCase() {
        when(restTemplate.postForObject(ArgumentMatchers.anyString(),ArgumentMatchers.any(),ArgumentMatchers.<Class<String>>any())).thenReturn(getXmlResponseString());
        when(getDefaultWalletStatusHandler.isUserRegisteredWithSameMFS(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(Boolean.TRUE);
        Response response = new Response();
        BrokerResponse brokerResponse = new BrokerResponse();
        brokerResponse.setBrokerCode("200");
        MappingResponse mappingResponse = new MappingResponse();
        mappingResponse.setMappingCode(TestCaseConstants.BROKER_FAILURE.getValue());
        WalletResponse walletResponse = new WalletResponse();
        walletResponse.setTxnid("123456");
        response.setWalletResponse(walletResponse);
        response.setMappingResponse(mappingResponse);
        response.setBrokerResponse(brokerResponse);
        when(mobiquityp2pHandler.execute(Mockito.any(), Mockito.anyString(),Mockito.anyString(),Mockito.anyString())).thenReturn(response);
        with().contentType(ContentType.JSON).accept(ContentType.JSON).body(getTransactionRequest()).given().header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue()).when().post(TestCaseConstants.TRANSACTIONS_BASE_CONTEXT.getValue()).then().assertThat().statusCode(HttpStatus.OK.value())
        .and().assertThat()
        .body(TestCaseConstants.MAPPED_CODE_NAME.getValue(),
                equalTo(TestCaseConstants.BROKER_FAILURE.getValue()));
    }
	
	@Test
    public void verifyP2POnUsTransactionFail() {
		when(restTemplate.postForObject(ArgumentMatchers.anyString(),ArgumentMatchers.any(),ArgumentMatchers.<Class<String>>any())).thenReturn(getXmlResponseString());
		when(getDefaultWalletStatusHandler.isUserRegisteredWithSameMFS(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(Boolean.TRUE);
		Response response = new Response();
        MappingResponse mappingResponse = new MappingResponse();
        mappingResponse.setMappingCode(ThirdPartyResponseCodes.EIG_GENERAL_ERROR.getMappedCode());
        response.setMappingResponse(mappingResponse);
		when(mobiquityp2pHandler.execute(Mockito.any(), Mockito.anyString(),Mockito.anyString(),Mockito.anyString())).thenReturn(response);
		with().contentType(ContentType.JSON).accept(ContentType.JSON).body(getTransactionRequest()).given().header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue()).when().post(TestCaseConstants.TRANSACTIONS_BASE_CONTEXT.getValue()).then().assertThat().statusCode(HttpStatus.OK.value())
		.and().assertThat()
		.body(TestCaseConstants.MAPPED_CODE_NAME.getValue(),
				equalTo(TestCaseConstants.ERRORDEFAULT_INTERNAL.getValue()));
	}
	
	@Test
    public void verifyP2POnUsTransactionAmbiguos() {
		when(restTemplate.postForObject(ArgumentMatchers.anyString(),ArgumentMatchers.any(),ArgumentMatchers.<Class<String>>any())).thenReturn(getXmlResponseString());
		when(getDefaultWalletStatusHandler.isUserRegisteredWithSameMFS(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(Boolean.TRUE);
		Response response = new Response();
       
		when(mobiquityp2pHandler.execute(Mockito.any(), Mockito.anyString(),Mockito.anyString(),Mockito.anyString())).thenReturn(response);
		with().contentType(ContentType.JSON).accept(ContentType.JSON).body(getTransactionRequest()).given().header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue()).when().post(TestCaseConstants.TRANSACTIONS_BASE_CONTEXT.getValue()).then().assertThat().statusCode(HttpStatus.OK.value())
		.and().assertThat()
		.body(TestCaseConstants.MAPPED_CODE_NAME.getValue(),
				equalTo(TestCaseConstants.ERRORDEFAULT_INTERNAL.getValue()));
	}
	

	@Test
    public void verifyP2POnUsInterOpException() {
		when(restTemplate.postForObject(ArgumentMatchers.anyString(),ArgumentMatchers.any(),ArgumentMatchers.<Class<String>>any())).thenReturn(getXmlResponseString());
		when(getDefaultWalletStatusHandler.isUserRegisteredWithSameMFS(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(Boolean.TRUE);
		doThrow(new InteropException(InteropResponseCodes.INTERNAL_ERROR.getStatusCode(),TestCaseConstants.ERRORDEFAULT_INTERNAL.getValue())).when(mobiquityp2pHandler).execute(Mockito.any(), Mockito.anyString(),Mockito.anyString(),Mockito.anyString());
		with().contentType(ContentType.JSON).accept(ContentType.JSON).body(getTransactionRequest()).given().header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue()).when().post(TestCaseConstants.TRANSACTIONS_BASE_CONTEXT.getValue()).then().assertThat().statusCode(HttpStatus.OK.value())
		.and().assertThat()
		.body(TestCaseConstants.MAPPED_CODE_NAME.getValue(),
				equalTo(TestCaseConstants.ERRORDEFAULT_INTERNAL.getValue()));
	}
	
	@Test
    public void verifyP2POnUsRestClientException() {
		when(restTemplate.postForObject(ArgumentMatchers.anyString(),ArgumentMatchers.any(),ArgumentMatchers.<Class<String>>any())).thenReturn(getXmlResponseString());
		when(getDefaultWalletStatusHandler.isUserRegisteredWithSameMFS(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(Boolean.TRUE);
		doThrow(new RestClientException(TestCaseConstants.ERRORDEFAULT_INTERNAL.getValue())).when(mobiquityp2pHandler).execute(Mockito.any(), Mockito.anyString(),Mockito.anyString(),Mockito.anyString());
		with().contentType(ContentType.JSON).accept(ContentType.JSON).body(getTransactionRequest()).given().header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue()).when().post(TestCaseConstants.TRANSACTIONS_BASE_CONTEXT.getValue()).then().assertThat().statusCode(HttpStatus.OK.value())
		.and().assertThat()
		.body(TestCaseConstants.MAPPED_CODE_NAME.getValue(),
				equalTo(TestCaseConstants.ERRORDEFAULT_INTERNAL.getValue()));
	}
	
	@Test
    public void verifyP2POnUsSocketTimeoutException() {
        when(restTemplate.postForObject(ArgumentMatchers.anyString(),ArgumentMatchers.any(),ArgumentMatchers.<Class<String>>any())).thenReturn(getXmlResponseString());
        when(getDefaultWalletStatusHandler.isUserRegisteredWithSameMFS(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(Boolean.TRUE);
        doThrow(new RestClientException(TestCaseConstants.ERRORDEFAULT_INTERNAL.getValue()).initCause(new SocketTimeoutException())).when(mobiquityp2pHandler).execute(Mockito.any(), Mockito.anyString(),Mockito.anyString(),Mockito.anyString());
        with().contentType(ContentType.JSON).accept(ContentType.JSON).body(getTransactionRequest()).given().header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue()).when().post(TestCaseConstants.TRANSACTIONS_BASE_CONTEXT.getValue()).then().assertThat().statusCode(HttpStatus.OK.value())
        .and().assertThat()
        .body(TestCaseConstants.MAPPED_CODE_NAME.getValue(),
                equalTo(TestCaseConstants.ERRORDEFAULT_INTERNAL.getValue()));
    }
	
	@Test
    public void verifyP2POnUsException() {
		when(restTemplate.postForObject(ArgumentMatchers.anyString(),ArgumentMatchers.any(),ArgumentMatchers.<Class<String>>any())).thenReturn(getXmlResponseString());
		when(getDefaultWalletStatusHandler.isUserRegisteredWithSameMFS(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(Boolean.TRUE);
		doThrow(new RuntimeException()).when(mobiquityp2pHandler).execute(Mockito.any(), Mockito.anyString(),Mockito.anyString(),Mockito.anyString());
		with().contentType(ContentType.JSON).accept(ContentType.JSON).body(getTransactionRequest()).given().header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue()).when().post(TestCaseConstants.TRANSACTIONS_BASE_CONTEXT.getValue()).then().assertThat().statusCode(HttpStatus.OK.value())
		.and().assertThat()
		.body(TestCaseConstants.MAPPED_CODE_NAME.getValue(),
				equalTo(TestCaseConstants.ERRORDEFAULT_INTERNAL.getValue()));
	}
	
	private String getTransactionRequest()
	{
		return DataPreparationUtil.prepareTransactionRequest(TestCaseConstants.DEFAULT_AMOUNT.getValue(),
				TestCaseConstants.DEFAULT_PAYEE_MSISDN.getValue(), TestCaseConstants.DEFAULT_CURRENCY.getValue(),
				TestCaseConstants.DEFAULT_PAYER_MSISDN.getValue(), TestCaseConstants.DEFAULT_PIN.getValue(),
				TestCaseConstants.EXT_ORG_REF_ID_VALUE.getValue(), TestCaseConstants.P2P_TXN_TYPE_VALUE.getValue());
	}
	
	private String getXmlResponseString() {
		return "<?xml version=\"\"1.0\"\" encoding=\"\"UTF-8\"\"?>" +
				"<response>" +
				"    <broker_response>" +
				"        <broker_code>200</broker_code>" +
				"        <broker_msg>ok</broker_msg>" +
				"        <call_wallet_id>2244</call_wallet_id>" +
				"        <session_id>BROKER-V3_SN_001_20190311121123772_257</session_id>" +
				"    </broker_response>" +
				"    <mapping_response>" +
				"        <mapping_code>SUCCESS</mapping_code>" +
				"    </mapping_response>" +
				"    <wallet_response>" +
				"        <type>GETFEESRESP</type>" +
				"        <service_type>CTMMREQ</service_type>" +
				"        <txnid>GF190311.1311.R00004</txnid>" +
				"        <txnstatus>200</txnstatus>" +
				"        <trid>201903111311R7300</trid>" +
				"        <fees_payer_paid>0.00</fees_payer_paid>" +
				"        <fees_payee_paid>0.00</fees_payee_paid>" +
				"        <comm_payer_paid>0.00</comm_payer_paid>" +
				"        <comm_payer_rec>0.00</comm_payer_rec>" +
				"        <comm_payee_paid>0.00</comm_payee_paid>" +
				"        <comm_payee_rec>0.00</comm_payee_rec>" +
				"        <tax_payer_paid>0.00</tax_payer_paid>" +
				"        <tax_payee_paid>0.00</tax_payee_paid>" +
				"        <message>Success</message>" +
				"    </wallet_response>" +
				"</response>";
	}
}
