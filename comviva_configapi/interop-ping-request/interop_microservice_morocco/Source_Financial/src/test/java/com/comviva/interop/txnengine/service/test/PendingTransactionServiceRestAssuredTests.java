package com.comviva.interop.txnengine.service.test;

import static io.restassured.RestAssured.with;
import static org.hamcrest.core.IsEqual.equalTo;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import com.comviva.interop.txnengine.entities.InteropTransactions;
import com.comviva.interop.txnengine.enums.InteropResponseCodes;
import com.comviva.interop.txnengine.enums.TestCaseConstants;
import com.comviva.interop.txnengine.enums.ValidationErrors;
import com.comviva.interop.txnengine.util.DataPreparationUtil;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PendingTransactionServiceRestAssuredTests {
	
	@LocalServerPort
	private int port;

	
	@Mock
	private EntityManager entityManager;

	@Mock TypedQuery<InteropTransactions> typedQuery;
	
	@Before
	public void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
	}

	@Test
	public void validatePendingTransactionAPIWithUnAuthorizedAccess() {
		with().contentType(ContentType.JSON).accept(ContentType.JSON).when()
				.get(TestCaseConstants.PENDING_TRANSACTIONS_BASE_CONTEXT.getValue()).then().assertThat()
				.statusCode(HttpStatus.UNAUTHORIZED.value());
	}

	@Test
	public void validatePendingTransactionAPIWithEmptyData() {
		long recevierMSISDN = DataPreparationUtil.generateRandomMSISDN();
		
       
		with().contentType(ContentType.JSON).accept(ContentType.JSON)
				.given()
				.header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue())
				.when().get(TestCaseConstants.PENDING_TRANSACTIONS_BASE_CONTEXT.getValue()+Long.toString(recevierMSISDN)+"?numberOfTransactions=4&lang=en").then().assertThat()
				.statusCode(HttpStatus.OK.value()).and().assertThat().body(TestCaseConstants.CODE_NAME.getValue(),
						equalTo(InteropResponseCodes.NO_RECORDS_FOUND.getEntity() + "_"
								+ InteropResponseCodes.NO_RECORDS_FOUND.getStatusCode()));
	}
	
	@Test
	public void validatePendingTransactionAPIWithInvalidLenghtOfMSISDN() {
		with().contentType(ContentType.JSON).accept(ContentType.JSON)
				.given()
				.header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue())
				.when().get(TestCaseConstants.PENDING_TRANSACTIONS_BASE_CONTEXT.getValue()+"12345"+"?numberOfTransactions=4&lang=en").then().assertThat()
				.statusCode(HttpStatus.OK.value()).and().assertThat().body(TestCaseConstants.CODE_NAME.getValue(),
						equalTo(ValidationErrors.INVALID_SENDER_MSISDN_LENGTH.getEntity() + "_"
								+ ValidationErrors.INVALID_SENDER_MSISDN_LENGTH.getStatusCode()));
	}
	
	@Test
	public void validatePendingTransactionAPIWithEmptyMSISDN() {
		with().contentType(ContentType.JSON).accept(ContentType.JSON)
				.given()
				.header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue())
				.when().get(TestCaseConstants.PENDING_TRANSACTIONS_BASE_CONTEXT.getValue()+""+"?numberOfTransactions=4&lang=en").then().assertThat()
				.statusCode(HttpStatus.NOT_FOUND.value());
	}
	
	@Test
	public void validatePendingTransactionAPIWithEmptyNoOfTransactions() {
		long recevierMSISDN = DataPreparationUtil.generateRandomMSISDN();
		with().contentType(ContentType.JSON).accept(ContentType.JSON)
				.given()
				.header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue())
				.when().get(TestCaseConstants.PENDING_TRANSACTIONS_BASE_CONTEXT.getValue()+Long.toString(recevierMSISDN)+"?numberOfTransactions=&lang=en").then().assertThat()
				.statusCode(HttpStatus.OK.value()).and().assertThat().body(TestCaseConstants.CODE_NAME.getValue(),
						equalTo(ValidationErrors.NO_OF_TXNS_SHOULD_BE_NUMERIC.getEntity() + "_"
								+ ValidationErrors.NO_OF_TXNS_SHOULD_BE_NUMERIC.getStatusCode()));
	}
	
	@Test
	public void validatePendingTransactionAPIWithEmptyLangugae() {
		long recevierMSISDN = DataPreparationUtil.generateRandomMSISDN();
		with().contentType(ContentType.JSON).accept(ContentType.JSON)
				.given()
				.header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue())
				.when().get(TestCaseConstants.PENDING_TRANSACTIONS_BASE_CONTEXT.getValue()+Long.toString(recevierMSISDN)+"?numberOfTransactions=&lang=").then().assertThat()
				.statusCode(HttpStatus.OK.value()).and().assertThat().body(TestCaseConstants.CODE_NAME.getValue(),
						equalTo(ValidationErrors.LANGUAGE_MISSING.getEntity() + "_"
								+ ValidationErrors.LANGUAGE_MISSING.getStatusCode()));
	}
	
	@Test
	public void validatePendingTransactionAPIWithInvalidLangugae() {
		long recevierMSISDN = DataPreparationUtil.generateRandomMSISDN();
		with().contentType(ContentType.JSON).accept(ContentType.JSON)
				.given()
				.header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue())
				.when().get(TestCaseConstants.PENDING_TRANSACTIONS_BASE_CONTEXT.getValue()+Long.toString(recevierMSISDN)+"?numberOfTransactions=&lang=english").then().assertThat()
				.statusCode(HttpStatus.OK.value()).and().assertThat().body(TestCaseConstants.CODE_NAME.getValue(),
						equalTo(ValidationErrors.LANGUAGE_NOT_SUPPORTED.getEntity() + "_"
								+ ValidationErrors.LANGUAGE_NOT_SUPPORTED.getStatusCode()));
	}
	
	@Test
	public void validatePendingTransactionAPIWithInvalidNumberOfTxns() {
		long recevierMSISDN = DataPreparationUtil.generateRandomMSISDN();
		with().contentType(ContentType.JSON).accept(ContentType.JSON)
				.given()
				.header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue())
				.when().get(TestCaseConstants.PENDING_TRANSACTIONS_BASE_CONTEXT.getValue()+Long.toString(recevierMSISDN)+"?numberOfTransactions=sd&lang=en").then().assertThat()
				.statusCode(HttpStatus.OK.value()).and().assertThat().body(TestCaseConstants.CODE_NAME.getValue(),
						equalTo(ValidationErrors.NO_OF_TXNS_SHOULD_BE_NUMERIC.getEntity() + "_"
								+ ValidationErrors.NO_OF_TXNS_SHOULD_BE_NUMERIC.getStatusCode()));
	}

	}
