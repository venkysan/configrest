package com.comviva.interop.txnengine.service.test;

import static io.restassured.RestAssured.with;
import static org.hamcrest.core.IsEqual.equalTo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import com.comviva.interop.txnengine.configuration.Resource;
import com.comviva.interop.txnengine.enums.InteropResponseCodes;
import com.comviva.interop.txnengine.enums.TestCaseConstants;
import com.comviva.interop.txnengine.enums.ThirdPartyResponseCodes;
import com.comviva.interop.txnengine.enums.ValidationErrors;
import com.comviva.interop.txnengine.util.DataPreparationUtil;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TransactionStatusServiceTests {
	
	@LocalServerPort
	private int port;
	
	@Autowired
	private Resource resource;

	@Before
	public void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
	}

	@Test
	public void validateGetTransactionStatusAPIWithUnAuthorizedAccess() {
		with().contentType(ContentType.JSON).accept(ContentType.JSON).when().get(TestCaseConstants.TRANSACTIONS_BASE_CONTEXT.getValue()).then().assertThat()
				.statusCode(HttpStatus.UNAUTHORIZED.value());
	}

	@Test
	public void validateTransactionStatusAPIWithValidData() {
		
		long recevierMSISDN = DataPreparationUtil.generateRandomMSISDN();

		// initiate enrollment with receiver MSISDN
		String interopRefId = with().contentType(ContentType.JSON).accept(ContentType.JSON)
				.body(DataPreparationUtil.prepareInitiateEnrollmentRequest(Long.toString(recevierMSISDN),
						TestCaseConstants.DEFAULT_LANGUAGE_CODE.getValue()))
				.given()
				.header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue())
				.when()
				.post(resource.getNonFinancialServerUrlValue() + TestCaseConstants.BASE_CONTEXT.getValue())
				.then().assertThat().contentType(ContentType.JSON).and().assertThat().statusCode(HttpStatus.OK.value())
				.and().assertThat()
				.body(TestCaseConstants.MAPPED_CODE_NAME.getValue(),
						equalTo(ThirdPartyResponseCodes.SUCCESS.getMappedCode()))
				.extract().path(TestCaseConstants.INTEROP_REFERENCE_ID_NAME.getValue());

		// confirm enrollment
		with().contentType(ContentType.JSON).accept(ContentType.JSON)
				.body(DataPreparationUtil.prepareConfirmEnrollmentRequest(TestCaseConstants.OTP_VALUE.getValue(),
						TestCaseConstants.DEFAULT_LANGUAGE_CODE.getValue()))
				.given()
				.header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue())
				.when().post(getPath(interopRefId)).then().assertThat().contentType(ContentType.JSON).and().assertThat()
				.statusCode(HttpStatus.OK.value()).and().assertThat()
				.body(TestCaseConstants.MAPPED_CODE_NAME.getValue(),
						equalTo(ThirdPartyResponseCodes.SUCCESS.getMappedCode()));

		// create transaction with enrolled msisdn
	String interopTxnId = 	with().contentType(ContentType.JSON).accept(ContentType.JSON)
				.body(DataPreparationUtil.prepareTransactionRequest(TestCaseConstants.DEFAULT_AMOUNT.getValue(),
						Long.toString(recevierMSISDN), TestCaseConstants.DEFAULT_CURRENCY.getValue(),
						TestCaseConstants.DEFAULT_PAYER_MSISDN.getValue(), TestCaseConstants.DEFAULT_PIN.getValue(),
						TestCaseConstants.EXT_ORG_REF_ID_VALUE.getValue(), TestCaseConstants.P2P_TXN_TYPE_VALUE.getValue()))
				.given()
				.header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue())
				.when().post(TestCaseConstants.TRANSACTIONS_BASE_CONTEXT.getValue()).then().assertThat()
				.statusCode(HttpStatus.OK.value()).and().assertThat()
				.body(TestCaseConstants.MAPPED_CODE_NAME.getValue(),
						equalTo(ThirdPartyResponseCodes.SUCCESS.getMappedCode())).extract().path(TestCaseConstants.INTEROP_REFERENCE_ID_NAME.getValue());
		
	//Get the transaction by interop txn id
		with().contentType(ContentType.JSON).accept(ContentType.JSON).given()
				.header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue()).when().get(TestCaseConstants.TRANSACTIONS_BASE_CONTEXT.getValue() + interopTxnId + "/" + TestCaseConstants.LANGUAGE_CODE.getValue()).then()
				.assertThat().statusCode(HttpStatus.OK.value())
				.and().assertThat()
				.body(TestCaseConstants.MAPPED_CODE_NAME.getValue(),
						equalTo(ThirdPartyResponseCodes.SUCCESS.getMappedCode()));
	}
	
	@Test
	public void validateTransactionStatusAPIWithEmptyInterOpId() {
		with().contentType(ContentType.JSON).accept(ContentType.JSON).given()
		.header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue()).when().get(TestCaseConstants.TRANSACTIONS_BASE_CONTEXT.getValue() + "" + "/" + TestCaseConstants.LANGUAGE_CODE.getValue()).then()
		.assertThat().statusCode(HttpStatus.NOT_FOUND.value());
	}
	
	@Test
	public void validateTransactionStatusAPIWithInvalidInterOpId() {
		with().contentType(ContentType.JSON).accept(ContentType.JSON).given()
		.header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue()).when().get(TestCaseConstants.TRANSACTIONS_BASE_CONTEXT.getValue() + "12365" + "/" + TestCaseConstants.LANGUAGE_CODE.getValue()).then()
		.assertThat().statusCode(HttpStatus.OK.value())
		.body(TestCaseConstants.CODE_NAME.getValue(),
				equalTo(InteropResponseCodes.NO_RECORDS_FOUND.getEntity() + "_"
						+ InteropResponseCodes.NO_RECORDS_FOUND.getStatusCode()));
	}
	
	
	@Test
	public void validateTransactionStatusAPIWithInvalidLanguageCode() {
		with().contentType(ContentType.JSON).accept(ContentType.JSON).given()
		.header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue()).when().get(TestCaseConstants.TRANSACTIONS_BASE_CONTEXT.getValue() + TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue() + "/" + TestCaseConstants.LANGUAGE_VALUE.getValue()).then()
		.assertThat().statusCode(HttpStatus.OK.value())
		.body(TestCaseConstants.CODE_NAME.getValue(),
				equalTo(ValidationErrors.LANGUAGE_NOT_SUPPORTED.getEntity() + "_"
						+ ValidationErrors.LANGUAGE_NOT_SUPPORTED.getStatusCode()));
	}
	
	@Test
	public void validateTransactionStatusExceptionCase() {
		with().contentType(ContentType.JSON).accept(ContentType.JSON).given()
		.header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue()).when().get(TestCaseConstants.TRANSACTIONS_BASE_CONTEXT.getValue() + TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue() + "/" + TestCaseConstants.LANGUAGE_CODE.getValue()).then()
		.assertThat().statusCode(HttpStatus.OK.value())
		.and().assertThat()
		.body(TestCaseConstants.MAPPED_CODE_NAME.getValue(),
				equalTo(ThirdPartyResponseCodes.SUCCESS.getMappedCode()));
	}
	
	
	
	public String getPath(String interopReferenceId) {
		return resource.getNonFinancialServerUrlValue() + TestCaseConstants.BASE_CONTEXT.getValue()
				+ interopReferenceId + TestCaseConstants.BASE_CONTEXT_END.getValue();
	}
	
}
