package com.comviva.interop.txnengine.service.test;

import static io.restassured.RestAssured.with;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.when;

import java.net.SocketTimeoutException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.comviva.interop.txnengine.enums.Constants;
import com.comviva.interop.txnengine.enums.InteropResponseCodes;
import com.comviva.interop.txnengine.enums.TestCaseConstants;
import com.comviva.interop.txnengine.model.ReceiveTransactionRequest;
import com.comviva.interop.txnengine.util.DataPreparationUtil;
import com.comviva.interop.txnengine.util.ThirdPartyCaller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReceiveTransactionServiceTest {
	
	@LocalServerPort
	private int port;

	@MockBean
	private RestTemplate restTemplate;
	
	@InjectMocks
	@Autowired
	private ThirdPartyCaller thirdPartyCaller;
	
	@Before
	public void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
	}
	
	@Test
    public void verifyReceiveTransactionServiceWithAmountEmpty() {
		with().contentType(ContentType.JSON).accept(ContentType.JSON).body(getRequestAmountEmpty()).given().header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue()).when().post(TestCaseConstants.RECEIVE_TRANSACTION_URL.getValue()).then().assertThat().statusCode(HttpStatus.OK.value())
		.and().assertThat()
		.body(TestCaseConstants.ACTION_CODE_NAME.getValue(),
                equalTo(InteropResponseCodes.HPS_TXN_FAILED_ACTION_CODE.getStatusCode()));
	}
	
	@Test
    public void verifyReceiveTransactionServiceWithProcessingCodeEmpty() {
		with().contentType(ContentType.JSON).accept(ContentType.JSON).body(getRequestProcessingCodeEmpty()).given().header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue()).when().post(TestCaseConstants.RECEIVE_TRANSACTION_URL.getValue()).then().assertThat().statusCode(HttpStatus.OK.value())
		.and().assertThat()
		.body(TestCaseConstants.ACTION_CODE_NAME.getValue(),
                equalTo(InteropResponseCodes.HPS_TXN_FAILED_ACTION_CODE.getStatusCode()));
	}
	
	@Test
    public void verifyReceiveTransactionServiceWithPayerMsisdnEmpty() {
		with().contentType(ContentType.JSON).accept(ContentType.JSON).body(getRequestPayerMsisdnEmpty()).given().header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue()).when().post(TestCaseConstants.RECEIVE_TRANSACTION_URL.getValue()).then().assertThat().statusCode(HttpStatus.OK.value())
		.and().assertThat()
		.body(TestCaseConstants.ACTION_CODE_NAME.getValue(),
                equalTo(InteropResponseCodes.HPS_TXN_FAILED_ACTION_CODE.getStatusCode()));
	}
	
	@Test
    public void verifyReceiveTransactionServiceWithPayeeEmpty() {
		with().contentType(ContentType.JSON).accept(ContentType.JSON).body(getRequestPayeeMsisdnEmpty()).given().header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue()).when().post(TestCaseConstants.RECEIVE_TRANSACTION_URL.getValue()).then().assertThat().statusCode(HttpStatus.OK.value())
		.and().assertThat()
		.body(TestCaseConstants.ACTION_CODE_NAME.getValue(),
                equalTo(InteropResponseCodes.HPS_TXN_FAILED_ACTION_CODE.getStatusCode()));
	}
	
	@Test
    public void verifyReceiveTransactionServiceWithCurrencyEmpty() {
		with().contentType(ContentType.JSON).accept(ContentType.JSON).body(getRequestCurrencyEmpty()).given().header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue()).when().post(TestCaseConstants.RECEIVE_TRANSACTION_URL.getValue()).then().assertThat().statusCode(HttpStatus.OK.value())
		.and().assertThat()
		.body(TestCaseConstants.ACTION_CODE_NAME.getValue(),
                equalTo(InteropResponseCodes.HPS_TXN_FAILED_ACTION_CODE.getStatusCode()));
	}
	
	@Test
    public void verifyReceiveTransactionServiceWithSystemAuditNumberEmpty() {
		with().contentType(ContentType.JSON).accept(ContentType.JSON).body(getRequestSystemAuditNumberEmpty()).given().header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue()).when().post(TestCaseConstants.RECEIVE_TRANSACTION_URL.getValue()).then().assertThat().statusCode(HttpStatus.OK.value())
		.and().assertThat()
		.body(TestCaseConstants.ACTION_CODE_NAME.getValue(),
                equalTo(InteropResponseCodes.HPS_TXN_FAILED_ACTION_CODE.getStatusCode()));
	}
	
	@Test
    public void verifyReceiveTransactionServiceWithReferenceNumberEmpty() {
		with().contentType(ContentType.JSON).accept(ContentType.JSON).body(getRequestReferenceEmpty()).given().header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue()).when().post(TestCaseConstants.RECEIVE_TRANSACTION_URL.getValue()).then().assertThat().statusCode(HttpStatus.OK.value())
		.and().assertThat()
		.body(TestCaseConstants.ACTION_CODE_NAME.getValue(),
                equalTo(InteropResponseCodes.HPS_TXN_FAILED_ACTION_CODE.getStatusCode()));
	}
	
	@Test
    public void verifyReceiveTransactionServiceWithCountryCodeEmpty() {
		with().contentType(ContentType.JSON).accept(ContentType.JSON).body(getRequestCountryCodeEmpty()).given().header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue()).when().post(TestCaseConstants.RECEIVE_TRANSACTION_URL.getValue()).then().assertThat().statusCode(HttpStatus.OK.value())
		.and().assertThat()
		.body(TestCaseConstants.ACTION_CODE_NAME.getValue(),
				equalTo(InteropResponseCodes.HPS_TXN_FAILED_ACTION_CODE.getStatusCode()));
	}
	
	@Test
    public void verifyReceiveTransactionServiceWithValidData() {
		when(restTemplate.postForObject(ArgumentMatchers.anyString(),ArgumentMatchers.any(),ArgumentMatchers.<Class<String>>any())).thenReturn(getResponseXmlString());
		with().contentType(ContentType.JSON).accept(ContentType.JSON).body(getRequest()).given().header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue()).when().post(TestCaseConstants.RECEIVE_TRANSACTION_URL.getValue()).then().assertThat().statusCode(HttpStatus.OK.value())
		.and().assertThat()
		.body(TestCaseConstants.ACTION_CODE_NAME.getValue(),
				equalTo(InteropResponseCodes.HPS_TXN_SUCCESS_ACTION_CODE.getStatusCode()));
	}
	
	@Test
    public void verifyReceiveTransactionServiceWithValidDataC2CFlow() {
		when(restTemplate.postForObject(ArgumentMatchers.anyString(),ArgumentMatchers.any(),ArgumentMatchers.<Class<String>>any())).thenReturn(getResponseXmlString());
		with().contentType(ContentType.JSON).accept(ContentType.JSON).body(getC2CRequest()).given().header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue()).when().post(TestCaseConstants.RECEIVE_TRANSACTION_URL.getValue()).then().assertThat().statusCode(HttpStatus.OK.value())
		.and().assertThat()
		.body(TestCaseConstants.ACTION_CODE_NAME.getValue(),
                equalTo(InteropResponseCodes.HPS_TXN_SUCCESS_ACTION_CODE.getStatusCode()));
	}
	
	@Test
    public void receiveTransactionServiceBrokerFailureTest() {
		when(restTemplate.postForObject(ArgumentMatchers.anyString(),ArgumentMatchers.any(),ArgumentMatchers.<Class<String>>any())).thenReturn(getFailResponseXmlString());
		with().contentType(ContentType.JSON).accept(ContentType.JSON).body(getRequest()).given().header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue()).when().post(TestCaseConstants.RECEIVE_TRANSACTION_URL.getValue()).then().assertThat().statusCode(HttpStatus.OK.value())
		.and().assertThat()
		.body(TestCaseConstants.ACTION_CODE_NAME.getValue(),
                equalTo(InteropResponseCodes.HPS_TXN_FAILED_ACTION_CODE.getStatusCode()));
    }
	
	@Test
	public void receiveTransactionServiceException(){
		when(restTemplate.postForObject(ArgumentMatchers.anyString(),ArgumentMatchers.any(),ArgumentMatchers.<Class<String>>any())).thenReturn(getWithOutMappingCodeTagResponseXmlString());
		with().contentType(ContentType.JSON).accept(ContentType.JSON).body(getRequest()).given().header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue()).when().post(TestCaseConstants.RECEIVE_TRANSACTION_URL.getValue()).then().assertThat().statusCode(HttpStatus.OK.value())
		.and().assertThat()
		.body(TestCaseConstants.ACTION_CODE_NAME.getValue(),
                equalTo(InteropResponseCodes.HPS_TXN_FAILED_ACTION_CODE.getStatusCode()));
	}	
	
	@Test
	public void receiveTransactionRestClientException(){
		when(restTemplate.postForObject(ArgumentMatchers.anyString(),ArgumentMatchers.any(),ArgumentMatchers.<Class<String>>any())).thenThrow(RestClientException.class);
		with().contentType(ContentType.JSON).accept(ContentType.JSON).body(getRequest()).given().header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue()).when().post(TestCaseConstants.RECEIVE_TRANSACTION_URL.getValue()).then().assertThat().statusCode(HttpStatus.OK.value())
		.and().assertThat()
		.body(TestCaseConstants.ACTION_CODE_NAME.getValue(),
                equalTo(InteropResponseCodes.HPS_TXN_FAILED_ACTION_CODE.getStatusCode()));
	}	
	
	@Test
	public void receiveTransactionSocketTimeOutClientException(){
		when(restTemplate.postForObject(ArgumentMatchers.anyString(),ArgumentMatchers.any(),ArgumentMatchers.<Class<String>>any())).thenThrow(new RestClientException(TestCaseConstants.ERRORDEFAULT_INTERNAL.getValue()).initCause(new SocketTimeoutException()));
		with().contentType(ContentType.JSON).accept(ContentType.JSON).body(getRequest()).given().header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue()).when().post(TestCaseConstants.RECEIVE_TRANSACTION_URL.getValue()).then().assertThat().statusCode(HttpStatus.OK.value())
		.and().assertThat()
		.body(TestCaseConstants.ACTION_CODE_NAME.getValue(),
                equalTo(InteropResponseCodes.HPS_TXN_FAILED_ACTION_CODE.getStatusCode()));
	}
	
	private String getRequest() {
		return jsonConvertion(DataPreparationUtil.prepareReceiveTransactionRequest());
	}
	
	private String getC2CRequest() {
		ReceiveTransactionRequest request =DataPreparationUtil.prepareReceiveTransactionRequest();
		request.setProcessingCode(Constants.MP_PROCESSING_CODE.getValue());;
		return jsonConvertion(request);
		
	}
	
	private String getRequestAmountEmpty() {
		ReceiveTransactionRequest request =DataPreparationUtil.prepareReceiveTransactionRequest();
		request.setTransactionAmount("");
		return jsonConvertion(request);
	}
	
	private String getRequestProcessingCodeEmpty() {
		ReceiveTransactionRequest request =DataPreparationUtil.prepareReceiveTransactionRequest();
		request.setProcessingCode("");
		return jsonConvertion(request);
	}
	
	private String getRequestPayerMsisdnEmpty() {
		ReceiveTransactionRequest request =DataPreparationUtil.prepareReceiveTransactionRequest();
		request.setSourceAccountNumber("");
		return jsonConvertion(request);
	}
	
	private String getRequestPayeeMsisdnEmpty() {
		ReceiveTransactionRequest request =DataPreparationUtil.prepareReceiveTransactionRequest();
		request.setPan("");
		return jsonConvertion(request);
	}
	
	private String getRequestSystemAuditNumberEmpty() {
		ReceiveTransactionRequest request =DataPreparationUtil.prepareReceiveTransactionRequest();
		request.setSystemAuditNumber("");
		return jsonConvertion(request);
	}
	
	private String getRequestCurrencyEmpty() {
		ReceiveTransactionRequest request =DataPreparationUtil.prepareReceiveTransactionRequest();
		request.setCurrencyCodeOfTheTransaction("");
		return jsonConvertion(request);
	}
	
	private String getRequestReferenceEmpty() {
		ReceiveTransactionRequest request =DataPreparationUtil.prepareReceiveTransactionRequest();
		request.setReferenceNumberOfTheRecovery("");
		return jsonConvertion(request);
	}
	
	private String getRequestCountryCodeEmpty() {
		ReceiveTransactionRequest request =DataPreparationUtil.prepareReceiveTransactionRequest();
		request.setCountryCodeOftheSenderOrganization("");
		return jsonConvertion(request);
	}
	
	private String getResponseXmlString() {
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
	
	private String getFailResponseXmlString() {
		return "<?xml version=\"\"1.0\"\" encoding=\"\"UTF-8\"\"?>" +
				"<response>" +
				"    <broker_response>" +
				"        <broker_code>500</broker_code>" +
				"        <broker_msg>ok</broker_msg>" +
				"        <call_wallet_id>2244</call_wallet_id>" +
				"        <session_id>BROKER-V3_SN_001_20190311121123772_257</session_id>" +
				"    </broker_response>" +
				"    <mapping_response>" +
				"        <mapping_code>FAILURE</mapping_code>" +
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
	
	private String getWithOutMappingCodeTagResponseXmlString() {
		return "<?xml version=\"\"1.0\"\" encoding=\"\"UTF-8\"\"?>" +
				"<response>" +
				"    <broker_response>" +
				"        <broker_code>500</broker_code>" +
				"        <broker_msg>ok</broker_msg>" +
				"        <call_wallet_id>2244</call_wallet_id>" +
				"        <session_id>BROKER-V3_SN_001_20190311121123772_257</session_id>" +
				"    </broker_response>" +				
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
	
	private String jsonConvertion(ReceiveTransactionRequest request) {
		try {
			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			return ow.writeValueAsString(request);
			}catch(Exception e) {
				e.printStackTrace();
			}
		return "";
	}
}