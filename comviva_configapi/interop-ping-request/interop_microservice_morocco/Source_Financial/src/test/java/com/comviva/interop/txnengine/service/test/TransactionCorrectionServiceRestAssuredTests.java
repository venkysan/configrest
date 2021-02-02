package com.comviva.interop.txnengine.service.test;

import static io.restassured.RestAssured.with;
import static org.hamcrest.core.IsEqual.equalTo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import com.comviva.interop.txnengine.enums.InteropResponseCodes;
import com.comviva.interop.txnengine.enums.TestCaseConstants;
import com.comviva.interop.txnengine.enums.ThirdPartyResponseCodes;
import com.comviva.interop.txnengine.enums.ValidationErrors;
import com.comviva.interop.txnengine.model.TransactionData;
import com.comviva.interop.txnengine.util.DataPreparationUtil;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TransactionCorrectionServiceRestAssuredTests {

	@LocalServerPort
	private int port;

	@Before
	public void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
	}

	@Test
	public void validateTransactionCorrectionAPIWithUnAuthorizedAccess() {
		with().contentType(ContentType.JSON).accept(ContentType.JSON).when()
				.get(TestCaseConstants.TRANSACTION_CORRECTION_CONTEXT.getValue()).then().assertThat()
				.statusCode(HttpStatus.UNAUTHORIZED.value());
	}

	@Test
	public void validateTransactionCorrectionAPIWithValidData() {

		long recevierMSISDN = DataPreparationUtil.generateRandomMSISDN();

		// create transaction with enrolled msisdn
		String interopTxnId = with().contentType(ContentType.JSON).accept(ContentType.JSON)
				.body(DataPreparationUtil.prepareTransactionRequest(TestCaseConstants.DEFAULT_AMOUNT.getValue(),
						Long.toString(recevierMSISDN), TestCaseConstants.DEFAULT_CURRENCY.getValue(),
						TestCaseConstants.DEFAULT_PAYER_MSISDN.getValue(), TestCaseConstants.DEFAULT_PIN.getValue(),
						TestCaseConstants.EXT_ORG_REF_ID_VALUE.getValue(),
						TestCaseConstants.P2P_TXN_TYPE_VALUE.getValue()))
				.given()
				.header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue())
				.when().post(TestCaseConstants.TRANSACTIONS_BASE_CONTEXT.getValue()).then().assertThat()
				.statusCode(HttpStatus.OK.value()).and().assertThat()
				.body(TestCaseConstants.MAPPED_CODE_NAME.getValue(),
						equalTo(ThirdPartyResponseCodes.SUCCESS.getMappedCode()))
				.extract().path(TestCaseConstants.INTEROP_REFERENCE_ID_NAME.getValue());

		// Get the transaction by interop txn id
		Response response = with().contentType(ContentType.JSON).accept(ContentType.JSON).given()
				.header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue())
				.when()
				.get(TestCaseConstants.TRANSACTIONS_BASE_CONTEXT.getValue() + interopTxnId + "/"
						+ TestCaseConstants.LANGUAGE_CODE.getValue())
				.then().assertThat().statusCode(HttpStatus.OK.value()).and().assertThat()
				.body(TestCaseConstants.MAPPED_CODE_NAME.getValue(),
						equalTo(ThirdPartyResponseCodes.SUCCESS.getMappedCode()))
				.extract().response();

		with().contentType(ContentType.JSON).accept(ContentType.JSON)

				.given()
				.header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue())
				.when()
				.post(TestCaseConstants.TRANSACTION_CORRECTION_CONTEXT.getValue() + "?transactionId="
						+ response.jsonPath().getList("data", TransactionData.class).get(1).getExtOrgRefId())
				.then().assertThat().statusCode(HttpStatus.OK.value()).and().assertThat()
				.body(TestCaseConstants.CODE_NAME.getValue(),
						equalTo(InteropResponseCodes.SUCCESS.getEntity().toString() + "_"
								+ InteropResponseCodes.SUCCESS.getStatusCode()));
	}

	@Test
	public void validateTransactionCorrectionAPIWhenInvalidTransactionId() {
		with().contentType(ContentType.JSON).accept(ContentType.JSON).given()
				.header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue())
				.when().post(TestCaseConstants.TRANSACTION_CORRECTION_CONTEXT.getValue() + "?transactionId=" + "")
				.then().assertThat().statusCode(HttpStatus.OK.value()).and().assertThat()
				.body(TestCaseConstants.CODE_NAME.getValue(),
						equalTo(ValidationErrors.INVALID_TRANSACTION_ID.getEntity().toString() + "_"
								+ ValidationErrors.INVALID_TRANSACTION_ID.getStatusCode()));
	}

	@Test
	public void validateTransactionCorrectionAPIWhenInvalidRRN() {
		with().contentType(ContentType.JSON).accept(ContentType.JSON).given()
				.header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue())
				.when()
				.post(TestCaseConstants.TRANSACTION_CORRECTION_CONTEXT.getValue() + "?retrievalReferenceNumber=" + "")
				.then().assertThat().statusCode(HttpStatus.OK.value()).and().assertThat()
				.body(TestCaseConstants.CODE_NAME.getValue(),
						equalTo(ValidationErrors.INVALID_TRANSACTION_ID.getEntity().toString() + "_"
								+ ValidationErrors.INVALID_TRANSACTION_ID.getStatusCode()));
	}

	@Test
	public void validateTransactionCorrectionAPIWhenBothTxnIdAndRRNInvalid() {
		with().contentType(ContentType.JSON).accept(ContentType.JSON).given()
				.header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue())
				.when()
				.post(TestCaseConstants.TRANSACTION_CORRECTION_CONTEXT.getValue() + "?transactionId=" + ""
						+ "&retrievalReferenceNumber=" + "")
				.then().assertThat().statusCode(HttpStatus.OK.value()).and().assertThat()
				.body(TestCaseConstants.CODE_NAME.getValue(),
						equalTo(ValidationErrors.INVALID_TRANSACTION_ID.getEntity().toString() + "_"
								+ ValidationErrors.INVALID_TRANSACTION_ID.getStatusCode()));
	}
}
