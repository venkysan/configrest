package com.comviva.interop.txnengine.service.test;

import static io.restassured.RestAssured.with;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.comviva.interop.txnengine.enums.InteropResponseCodes;
import com.comviva.interop.txnengine.enums.TestCaseConstants;
import com.comviva.interop.txnengine.enums.ThirdPartyResponseCodes;
import com.comviva.interop.txnengine.util.DataPreparationUtil;
import com.comviva.interop.txnengine.util.ThirdPartyCaller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SuppressWarnings("rawtypes")
public class TransactionQuotationServiceTest {
	
	@LocalServerPort
	private int port;

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
    public void verifyTransactionQuotationServiceWithAmountEmpty() {
		with().contentType(ContentType.JSON).accept(ContentType.JSON).body(getRequestAmountEmpty()).given().header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue()).when().post(TestCaseConstants.TRANSACTION_QUOTATION_URL.getValue()).then().assertThat().statusCode(HttpStatus.OK.value())
		.and().assertThat()
		.body(TestCaseConstants.MAPPED_CODE_NAME.getValue(),
				equalTo(TestCaseConstants.INVALIDREQUEST_INVALID_AMOUNT.getValue()));
	}
	
	@Test
    public void verifyTransactionQuotationServiceWithValidData() {
		when(restTemplate.exchange(ArgumentMatchers.anyString(),ArgumentMatchers.any(HttpMethod.class),ArgumentMatchers.any(),ArgumentMatchers.<Class<Map>>any())).thenReturn(DataPreparationUtil.getResponseEntityP2POn());
		when(restTemplate.postForObject(ArgumentMatchers.anyString(),ArgumentMatchers.any(),ArgumentMatchers.<Class<String>>any())).thenReturn(getFeeXmlString());
		with().contentType(ContentType.JSON).accept(ContentType.JSON).body(getRequest()).given().header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue()).when().post(TestCaseConstants.TRANSACTION_QUOTATION_URL.getValue()).then().assertThat().statusCode(HttpStatus.OK.value())
		.and().assertThat()
		.body(TestCaseConstants.MAPPED_CODE_NAME.getValue(),
				equalTo(ThirdPartyResponseCodes.SUCCESS.getMappedCode()));
	}
	
	@Test
    public void transactionQuotationServiceP2POffFlowTest() {
		when(restTemplate.exchange(ArgumentMatchers.anyString(),ArgumentMatchers.any(HttpMethod.class),ArgumentMatchers.any(),ArgumentMatchers.<Class<Map>>any())).thenReturn(getResponseEntityP2POff());
		when(restTemplate.postForObject(ArgumentMatchers.anyString(),ArgumentMatchers.any(),ArgumentMatchers.<Class<String>>any())).thenReturn(getFeeXmlString());
		with().contentType(ContentType.JSON).accept(ContentType.JSON).body(getRequest()).given().header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue()).when().post(TestCaseConstants.TRANSACTION_QUOTATION_URL.getValue()).then().assertThat().statusCode(HttpStatus.OK.value())
		.and().assertThat()
		.body(TestCaseConstants.CODE_NAME.getValue(),
				equalTo(InteropResponseCodes.INTERNAL_ERROR.getEntity().toString() +"_"+InteropResponseCodes.INTERNAL_ERROR.getStatusCode()));
    }
	
	private ResponseEntity<Map> getResponseEntityP2POff(){
		Map<String,String> map= DataPreparationUtil.getUserHandlerResponse(TestCaseConstants.ENROLMENT_REG_SUCCESS_STATUS.getValue());
		map.put(TestCaseConstants.CODE.getValue(), InteropResponseCodes.INTERNAL_ERROR.getStatusCode());
		return new ResponseEntity<Map>(map, HttpStatus.OK);
	}	
	
	
	
	private String getRequest() {
	 return DataPreparationUtil.prepareTransactionRequest(TestCaseConstants.DEFAULT_AMOUNT.getValue(),
			TestCaseConstants.DEFAULT_MSISDN.getValue(), TestCaseConstants.DEFAULT_CURRENCY.getValue(),
			TestCaseConstants.DEFAULT_PAYER_MSISDN.getValue(), TestCaseConstants.DEFAULT_PIN.getValue(),
			TestCaseConstants.EXT_ORG_REF_ID_VALUE.getValue(), TestCaseConstants.P2P_TXN_TYPE_VALUE.getValue());
	}
	
	private String getRequestAmountEmpty() {
		 return DataPreparationUtil.prepareTransactionRequest(TestCaseConstants.EMPTY.getValue(),
				TestCaseConstants.DEFAULT_MSISDN.getValue(), TestCaseConstants.DEFAULT_CURRENCY.getValue(),
				TestCaseConstants.DEFAULT_PAYER_MSISDN.getValue(), TestCaseConstants.DEFAULT_PIN.getValue(),
				TestCaseConstants.EXT_ORG_REF_ID_VALUE.getValue(), TestCaseConstants.P2P_TXN_TYPE_VALUE.getValue());
		}
	

	private String getFeeXmlString() {
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