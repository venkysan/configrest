package com.comviva.interop.txnengine.service.test;

import static io.restassured.RestAssured.with;
import static org.hamcrest.core.IsEqual.equalTo;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import com.comviva.interop.txnengine.configuration.Resource;
import com.comviva.interop.txnengine.entities.InteropTransactions;
import com.comviva.interop.txnengine.enums.TestCaseConstants;
import com.comviva.interop.txnengine.enums.ThirdPartyResponseCodes;
import com.comviva.interop.txnengine.enums.ValidationErrors;
import com.comviva.interop.txnengine.util.DataPreparationUtil;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ConfirmTransactionServiceRestAssuredTests {
	
	@LocalServerPort
	private int port;
	
	@Autowired
	private Resource resource;

	
	@Mock
	private EntityManager entityManager;

	@Mock TypedQuery<InteropTransactions> typedQuery;
	
	@Before
	public void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
	}

	@Test
	public void validateCreateTransactionAPIWithUnAuthorizedAccess() {
		with().contentType(ContentType.JSON).accept(ContentType.JSON).when()
				.get(TestCaseConstants.CONFIRM_TRANSACTIONS_BASE_CONTEXT.getValue()).then().assertThat()
				.statusCode(HttpStatus.UNAUTHORIZED.value());
	}

	@Test
	public void validateConfirmTransactionAPIWithValidDataForAcceptCase() {
		
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
						TestCaseConstants.EXT_ORG_REF_ID_VALUE.getValue(), TestCaseConstants.MP_PULL_TXN_TYPE_VALUE.getValue()))
				.given()
				.header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue())
				.when().post(TestCaseConstants.TRANSACTIONS_BASE_CONTEXT.getValue()).then().assertThat()
				.statusCode(HttpStatus.OK.value()).and().assertThat()
				.body(TestCaseConstants.MAPPED_CODE_NAME.getValue(),
						equalTo(ThirdPartyResponseCodes.SUCCESS.getMappedCode())).extract().path(TestCaseConstants.INTEROP_REFERENCE_ID_NAME.getValue());
		
		 with().contentType(ContentType.JSON).accept(ContentType.JSON)
				.body(DataPreparationUtil.prepareConfirmTransactionRequest(interopTxnId, TestCaseConstants.PIN.getValue(),
						TestCaseConstants.LANGUAGE_CODE.getValue(), "accept"))
				.given()
				.header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue())
				.when()
				.post(TestCaseConstants.CONFIRM_TRANSACTIONS_BASE_CONTEXT.getValue())
				.then().assertThat().contentType(ContentType.JSON).and().assertThat().statusCode(HttpStatus.OK.value())
				.and().assertThat()
				.body(TestCaseConstants.MAPPED_CODE_NAME.getValue(),
						equalTo(ThirdPartyResponseCodes.SUCCESS.getMappedCode()));
				
	}
	
	@Test
	public void validateConfirmTransactionAPIWithValidDataForRejectCase() {
		
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
						TestCaseConstants.EXT_ORG_REF_ID_VALUE.getValue(), TestCaseConstants.MP_PULL_TXN_TYPE_VALUE.getValue()))
				.given()
				.header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue())
				.when().post(TestCaseConstants.TRANSACTIONS_BASE_CONTEXT.getValue()).then().assertThat()
				.statusCode(HttpStatus.OK.value()).and().assertThat()
				.body(TestCaseConstants.MAPPED_CODE_NAME.getValue(),
						equalTo(ThirdPartyResponseCodes.SUCCESS.getMappedCode())).extract().path(TestCaseConstants.INTEROP_REFERENCE_ID_NAME.getValue());
		
		 with().contentType(ContentType.JSON).accept(ContentType.JSON)
				.body(DataPreparationUtil.prepareConfirmTransactionRequest(interopTxnId, TestCaseConstants.PIN.getValue(),
						TestCaseConstants.LANGUAGE_CODE.getValue(), "reject"))
				.given()
				.header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue())
				.when()
				.post(TestCaseConstants.CONFIRM_TRANSACTIONS_BASE_CONTEXT.getValue())
				.then().assertThat().contentType(ContentType.JSON).and().assertThat().statusCode(HttpStatus.OK.value())
				.and().assertThat()
				.body(TestCaseConstants.MAPPED_CODE_NAME.getValue(),
						equalTo(ThirdPartyResponseCodes.SUCCESS.getMappedCode()))
				.extract().path(TestCaseConstants.INTEROP_REFERENCE_ID_NAME.getValue());
	}
	
	
	
	@Test
	public void validateConfirmTransactionAPIWithInvalidInteropTxnID() {
		 with().contentType(ContentType.JSON).accept(ContentType.JSON)
				.body(DataPreparationUtil.prepareConfirmTransactionRequest(TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue(), TestCaseConstants.PIN.getValue(),
						TestCaseConstants.LANGUAGE_CODE.getValue(), "accept"))
				.given()
				.header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue())
				.when()
				.post(TestCaseConstants.CONFIRM_TRANSACTIONS_BASE_CONTEXT.getValue())
				.then().assertThat().contentType(ContentType.JSON).and().assertThat().statusCode(HttpStatus.OK.value())
				.and().assertThat()
				.body(TestCaseConstants.CODE_NAME.getValue(),
						equalTo(ValidationErrors.INVALID_TRANSACTION_ID.getEntity().toString() + "_" +ValidationErrors.INVALID_TRANSACTION_ID.getStatusCode()));
	}
	
	@Test
	public void validateConfirmTransactionAPIWithNULLInteropTxnID() {
		 with().contentType(ContentType.JSON).accept(ContentType.JSON)
				.body(DataPreparationUtil.prepareConfirmTransactionRequest(null, TestCaseConstants.PIN.getValue(),
						TestCaseConstants.LANGUAGE_CODE.getValue(), "accept"))
				.given()
				.header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue())
				.when()
				.post(TestCaseConstants.CONFIRM_TRANSACTIONS_BASE_CONTEXT.getValue())
				.then().assertThat().contentType(ContentType.JSON).and().assertThat().statusCode(HttpStatus.OK.value())
				.and().assertThat()
				.body(TestCaseConstants.CODE_NAME.getValue(),
						equalTo(ValidationErrors.INTEROP_REFERENCE_ID_MISSING.getEntity().toString() + "_" +ValidationErrors.INTEROP_REFERENCE_ID_MISSING.getStatusCode()));
	}
	
	@Test
	public void validateConfirmTransactionAPIWithEmptyPin() {
		 with().contentType(ContentType.JSON).accept(ContentType.JSON)
				.body(DataPreparationUtil.prepareConfirmTransactionRequest(TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue(), TestCaseConstants.EMPTY.getValue(),
						TestCaseConstants.LANGUAGE_CODE.getValue(), "accept"))
				.given()
				.header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue())
				.when()
				.post(TestCaseConstants.CONFIRM_TRANSACTIONS_BASE_CONTEXT.getValue())
				.then().assertThat().contentType(ContentType.JSON).and().assertThat().statusCode(HttpStatus.OK.value())
				.and().assertThat()
				.body(TestCaseConstants.CODE_NAME.getValue(),
						equalTo(ValidationErrors.INVALID_PIN_LENGTH.getEntity().toString() + "_" +ValidationErrors.INVALID_PIN_LENGTH.getStatusCode()));
				
	}
	
	@Test
	public void validateConfirmTransactionAPIWithEmptyLangugae() {
		 with().contentType(ContentType.JSON).accept(ContentType.JSON)
				.body(DataPreparationUtil.prepareConfirmTransactionRequest(TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue(), TestCaseConstants.PIN.getValue(),
						TestCaseConstants.EMPTY.getValue(), "accept"))
				.given()
				.header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue())
				.when()
				.post(TestCaseConstants.CONFIRM_TRANSACTIONS_BASE_CONTEXT.getValue())
				.then().assertThat().contentType(ContentType.JSON).and().assertThat().statusCode(HttpStatus.OK.value())
				.and().assertThat()
				.body(TestCaseConstants.CODE_NAME.getValue(),
						equalTo(ValidationErrors.LANGUAGE_NOT_SUPPORTED.getEntity().toString() + "_" +ValidationErrors.LANGUAGE_NOT_SUPPORTED.getStatusCode()));
				
	}
	
	@Test
	public void validateConfirmTransactionAPIWithEmptyActionode() {
		 with().contentType(ContentType.JSON).accept(ContentType.JSON)
				.body(DataPreparationUtil.prepareConfirmTransactionRequest(TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue(), TestCaseConstants.PIN.getValue(),
						TestCaseConstants.LANGUAGE_CODE.getValue(), ""))
				.given()
				.header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue())
				.when()
				.post(TestCaseConstants.CONFIRM_TRANSACTIONS_BASE_CONTEXT.getValue())
				.then().assertThat().contentType(ContentType.JSON).and().assertThat().statusCode(HttpStatus.OK.value())
				.and().assertThat()
				.body(TestCaseConstants.CODE_NAME.getValue(),
						equalTo(ValidationErrors.INVALID_ACTION_TYPE.getEntity().toString() + "_" +ValidationErrors.INVALID_ACTION_TYPE.getStatusCode()));
				
	}
	
	@Test
	public void validateConfirmTransactionAPIWithInvalidActionode() {
		 with().contentType(ContentType.JSON).accept(ContentType.JSON)
				.body(DataPreparationUtil.prepareConfirmTransactionRequest(TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue(), TestCaseConstants.PIN.getValue(),
						TestCaseConstants.LANGUAGE_CODE.getValue(), "do not accept"))
				.given()
				.header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue())
				.when()
				.post(TestCaseConstants.CONFIRM_TRANSACTIONS_BASE_CONTEXT.getValue())
				.then().assertThat().contentType(ContentType.JSON).and().assertThat().statusCode(HttpStatus.OK.value())
				.and().assertThat()
				.body(TestCaseConstants.CODE_NAME.getValue(),
						equalTo(ValidationErrors.INVALID_ACTION_TYPE.getEntity().toString() + "_" +ValidationErrors.INVALID_ACTION_TYPE.getStatusCode()));
	}
	
	public String getPath(String interopReferenceId) {
		return resource.getNonFinancialServerUrlValue() + TestCaseConstants.BASE_CONTEXT.getValue()
				+ interopReferenceId + TestCaseConstants.BASE_CONTEXT_END.getValue();
	}

	}
