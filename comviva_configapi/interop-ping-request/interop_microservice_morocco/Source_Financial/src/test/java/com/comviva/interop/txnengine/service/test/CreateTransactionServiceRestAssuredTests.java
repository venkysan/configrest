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
import com.comviva.interop.txnengine.util.CastUtils;
import com.comviva.interop.txnengine.util.DataPreparationUtil;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CreateTransactionServiceRestAssuredTests {
	
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
	public void validateCreateTransactionAPIWithUnAuthorizedAccess() {
		with().contentType(ContentType.JSON).accept(ContentType.JSON).when()
				.get(TestCaseConstants.TRANSACTIONS_BASE_CONTEXT.getValue()).then().assertThat()
				.statusCode(HttpStatus.UNAUTHORIZED.value());
	}

	@Test
	public void validateCreateTransactionAPIWithValidData() {

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
		with().contentType(ContentType.JSON).accept(ContentType.JSON)
				.body(DataPreparationUtil.prepareTransactionRequest(TestCaseConstants.DEFAULT_AMOUNT.getValue(),
						Long.toString(recevierMSISDN), TestCaseConstants.DEFAULT_CURRENCY.getValue(),
						TestCaseConstants.DEFAULT_PAYER_MSISDN.getValue(), TestCaseConstants.DEFAULT_PIN.getValue(),
						TestCaseConstants.EXT_ORG_REF_ID_VALUE.getValue(), TestCaseConstants.P2P_TXN_TYPE_VALUE.getValue()))
				.given()
				.header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue())
				.when().post(TestCaseConstants.TRANSACTIONS_BASE_CONTEXT.getValue()).then().assertThat()
				.statusCode(HttpStatus.OK.value()).and().assertThat()
				.body(TestCaseConstants.MAPPED_CODE_NAME.getValue(),
						equalTo(ThirdPartyResponseCodes.SUCCESS.getMappedCode()));
	}

	@Test
	public void validateCreateTransactionAPIWithUnRegisteredReceviedMSISDN() {
		with().contentType(ContentType.JSON).accept(ContentType.JSON)
				.body(DataPreparationUtil.prepareTransactionRequest(TestCaseConstants.DEFAULT_AMOUNT.getValue(),
						TestCaseConstants.DEFAULT_MSISDN.getValue(), TestCaseConstants.DEFAULT_CURRENCY.getValue(),
						TestCaseConstants.DEFAULT_PAYER_MSISDN.getValue(), TestCaseConstants.DEFAULT_PIN.getValue(),
						TestCaseConstants.EXT_ORG_REF_ID_VALUE.getValue(), TestCaseConstants.P2P_TXN_TYPE_VALUE.getValue()))
				.given()
				.header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue())
				.when().post(TestCaseConstants.TRANSACTIONS_BASE_CONTEXT.getValue()).then().assertThat()
				.statusCode(HttpStatus.OK.value()).and().assertThat().body(TestCaseConstants.CODE_NAME.getValue(),
						equalTo(CastUtils.joinStatusCode(InteropResponseCodes.TXN_INITIATED_SUCCESSFULLY.getEntity().toString(), InteropResponseCodes.TXN_INITIATED_SUCCESSFULLY.getStatusCode())));
	}
	
	@Test
    public void validateCreateTransactionAPIForMPOffUsWithUnRegisteredReceviedMSISDN() {
        with().contentType(ContentType.JSON).accept(ContentType.JSON)
                .body(DataPreparationUtil.prepareTransactionRequest(TestCaseConstants.DEFAULT_AMOUNT.getValue(),
                        TestCaseConstants.DEFAULT_MSISDN.getValue(), TestCaseConstants.DEFAULT_CURRENCY.getValue(),
                        TestCaseConstants.DEFAULT_PAYER_MSISDN.getValue(), TestCaseConstants.DEFAULT_PIN.getValue(),
                        TestCaseConstants.EXT_ORG_REF_ID_VALUE.getValue(), TestCaseConstants.MP_TXN_TYPE_VALUE.getValue()))
                .given()
                .header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue())
                .when().post(TestCaseConstants.TRANSACTIONS_BASE_CONTEXT.getValue()).then().assertThat()
                .statusCode(HttpStatus.OK.value()).and().assertThat().body(TestCaseConstants.CODE_NAME.getValue(),
                        equalTo(CastUtils.joinStatusCode(InteropResponseCodes.TXN_INITIATED_SUCCESSFULLY.getEntity().toString(), InteropResponseCodes.TXN_INITIATED_SUCCESSFULLY.getStatusCode())));
    }
	
	@Test
    public void validateCreateTransactionAPIForMPPullOffUsWithUnRegisteredPayerMSISDN() {
        with().contentType(ContentType.JSON).accept(ContentType.JSON)
                .body(DataPreparationUtil.prepareTransactionRequest(TestCaseConstants.DEFAULT_AMOUNT.getValue(),
                        TestCaseConstants.DEFAULT_MSISDN.getValue(), TestCaseConstants.DEFAULT_CURRENCY.getValue(),
                        TestCaseConstants.DEFAULT_PAYER_MSISDN.getValue(), TestCaseConstants.DEFAULT_PIN.getValue(),
                        TestCaseConstants.EXT_ORG_REF_ID_VALUE.getValue(), TestCaseConstants.MP_PULL_TXN_TYPE_VALUE.getValue()))
                .given()
                .header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue())
                .when().post(TestCaseConstants.TRANSACTIONS_BASE_CONTEXT.getValue()).then().assertThat()
                .statusCode(HttpStatus.OK.value()).and().assertThat().body(TestCaseConstants.CODE_NAME.getValue(),
                        equalTo(InteropResponseCodes.TXN_INITIATED_SUCCESSFULLY.getEntity() + "_" +InteropResponseCodes.TXN_INITIATED_SUCCESSFULLY.getStatusCode()));
    }
	
	@Test
	public void validateCreateTransactionAPIWithEmptyAmountValue() {
		with().contentType(ContentType.JSON).accept(ContentType.JSON)
				.body(DataPreparationUtil.prepareTransactionRequest(TestCaseConstants.EMPTY.getValue(),
						TestCaseConstants.DEFAULT_MSISDN.getValue(), TestCaseConstants.DEFAULT_CURRENCY.getValue(),
						TestCaseConstants.DEFAULT_PAYER_MSISDN.getValue(), TestCaseConstants.DEFAULT_PIN.getValue(),
						TestCaseConstants.EXT_ORG_REF_ID_VALUE.getValue(), TestCaseConstants.P2P_TXN_TYPE_VALUE.getValue()))
				.given()
				.header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue())
				.when().post(TestCaseConstants.TRANSACTIONS_BASE_CONTEXT.getValue()).then().assertThat()
				.statusCode(HttpStatus.OK.value()).and().assertThat().body(TestCaseConstants.CODE_NAME.getValue(),
						equalTo(ValidationErrors.INCORRECT_AMOUNT_FORMAT.getEntity() + "_"
								+ ValidationErrors.INCORRECT_AMOUNT_FORMAT.getStatusCode()));
	}

	@Test
	public void validateCreateTransactionAPIWithInvalidAmountValue() {
		with().contentType(ContentType.JSON).accept(ContentType.JSON)
				.body(DataPreparationUtil.prepareTransactionRequest(TestCaseConstants.INVALID_AMOUNT_VALUE.getValue(),
						TestCaseConstants.DEFAULT_MSISDN.getValue(), TestCaseConstants.DEFAULT_CURRENCY.getValue(),
						TestCaseConstants.DEFAULT_PAYER_MSISDN.getValue(), TestCaseConstants.DEFAULT_PIN.getValue(),
						TestCaseConstants.EXT_ORG_REF_ID_VALUE.getValue(), TestCaseConstants.P2P_TXN_TYPE_VALUE.getValue()))
				.given()
				.header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue())
				.when().post(TestCaseConstants.TRANSACTIONS_BASE_CONTEXT.getValue()).then().assertThat()
				.statusCode(HttpStatus.OK.value()).and().assertThat().body(TestCaseConstants.CODE_NAME.getValue(),
						equalTo(ValidationErrors.INCORRECT_AMOUNT_FORMAT.getEntity() + "_"
								+ ValidationErrors.INCORRECT_AMOUNT_FORMAT.getStatusCode()));
	}
	
	@Test
	public void validateCreateTransactionAPIWithoutAmount() {
		with().contentType(ContentType.JSON).accept(ContentType.JSON)
				.body(DataPreparationUtil.prepareTransactionRequestWithOutAmount(
						TestCaseConstants.DEFAULT_MSISDN.getValue(), TestCaseConstants.DEFAULT_CURRENCY.getValue(),
						TestCaseConstants.DEFAULT_PAYER_MSISDN.getValue(), TestCaseConstants.DEFAULT_PIN.getValue(),
						TestCaseConstants.EXT_ORG_REF_ID_VALUE.getValue(), TestCaseConstants.P2P_TXN_TYPE_VALUE.getValue()))
				.given()
				.header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue())
				.when().post(TestCaseConstants.TRANSACTIONS_BASE_CONTEXT.getValue()).then().assertThat()
				.statusCode(HttpStatus.BAD_REQUEST.value());
	}

	@Test
	public void validateCreateTransactionAPIWithEmptyReceiverMSISDN() {
		with().contentType(ContentType.JSON).accept(ContentType.JSON)
				.body(DataPreparationUtil.prepareTransactionRequest(TestCaseConstants.DEFAULT_AMOUNT.getValue(),
						TestCaseConstants.EMPTY.getValue(), TestCaseConstants.DEFAULT_CURRENCY.getValue(),
						TestCaseConstants.DEFAULT_PAYER_MSISDN.getValue(), TestCaseConstants.DEFAULT_PIN.getValue(),
						TestCaseConstants.EXT_ORG_REF_ID_VALUE.getValue(), TestCaseConstants.P2P_TXN_TYPE_VALUE.getValue()))
				.given()
				.header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue())
				.when().post(TestCaseConstants.TRANSACTIONS_BASE_CONTEXT.getValue()).then().assertThat()
				.statusCode(HttpStatus.OK.value()).and().assertThat().body(TestCaseConstants.CODE_NAME.getValue(),
						equalTo(ValidationErrors.RECEVIER_MSISDN_MISSING.getEntity() + "_"
								+ ValidationErrors.RECEVIER_MSISDN_MISSING.getStatusCode()));
	}

	@Test
	public void validateCreateTransactionAPIWithInvalidLengthOfReceiverMSISDN() {
		with().contentType(ContentType.JSON).accept(ContentType.JSON)
				.body(DataPreparationUtil.prepareTransactionRequest(TestCaseConstants.DEFAULT_AMOUNT.getValue(),
						TestCaseConstants.INVALID_LENGTH_MSISDN.getValue(),
						TestCaseConstants.DEFAULT_CURRENCY.getValue(),
						TestCaseConstants.DEFAULT_PAYER_MSISDN.getValue(), TestCaseConstants.DEFAULT_PIN.getValue(),
						TestCaseConstants.EXT_ORG_REF_ID_VALUE.getValue(), TestCaseConstants.P2P_TXN_TYPE_VALUE.getValue()))
				.given()
				.header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue())
				.when().post(TestCaseConstants.TRANSACTIONS_BASE_CONTEXT.getValue()).then().assertThat()
				.statusCode(HttpStatus.OK.value()).and().assertThat().body(TestCaseConstants.CODE_NAME.getValue(),
						equalTo(ValidationErrors.INVALID_RECEVIER_MSISDN_LENGTH.getEntity() + "_"
								+ ValidationErrors.INVALID_RECEVIER_MSISDN_LENGTH.getStatusCode()));
	}

	@Test
	public void validateCreateTransactionAPIWithNonNumericReceiverMSISDN() {
		with().contentType(ContentType.JSON).accept(ContentType.JSON)
				.body(DataPreparationUtil.prepareTransactionRequest(TestCaseConstants.DEFAULT_AMOUNT.getValue(),
						TestCaseConstants.INVALID_MSISDN.getValue(), TestCaseConstants.DEFAULT_CURRENCY.getValue(),
						TestCaseConstants.DEFAULT_PAYER_MSISDN.getValue(), TestCaseConstants.DEFAULT_PIN.getValue(),
						TestCaseConstants.EXT_ORG_REF_ID_VALUE.getValue(), TestCaseConstants.P2P_TXN_TYPE_VALUE.getValue()))
				.given()
				.header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue())
				.when().post(TestCaseConstants.TRANSACTIONS_BASE_CONTEXT.getValue()).then().assertThat()
				.statusCode(HttpStatus.OK.value()).and().assertThat().body(TestCaseConstants.CODE_NAME.getValue(),
						equalTo(ValidationErrors.RECEVIER_MSISDN_SHOULD_BE_NUMERIC.getEntity() + "_"
								+ ValidationErrors.RECEVIER_MSISDN_SHOULD_BE_NUMERIC.getStatusCode()));
	}
	
	@Test
	public void validateCreateTransactionAPIWithoutCreditParty() {
		with().contentType(ContentType.JSON).accept(ContentType.JSON)
				.body(DataPreparationUtil.prepareTransactionRequestWithoutCreditParty(
						TestCaseConstants.DEFAULT_AMOUNT.getValue(), TestCaseConstants.DEFAULT_CURRENCY.getValue(),
						TestCaseConstants.DEFAULT_PAYER_MSISDN.getValue(), TestCaseConstants.DEFAULT_PIN.getValue(),
						TestCaseConstants.EXT_ORG_REF_ID_VALUE.getValue(), TestCaseConstants.P2P_TXN_TYPE_VALUE.getValue()))
				.given()
				.header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue())
				.when().post(TestCaseConstants.TRANSACTIONS_BASE_CONTEXT.getValue()).then().assertThat()
				.statusCode(HttpStatus.OK.value())
				.and().assertThat().body(TestCaseConstants.CODE_NAME.getValue(),
						equalTo(ValidationErrors.RECEIVER_PARTY_DETAILS_MISSING.getEntity() + "_"
								+ ValidationErrors.RECEIVER_PARTY_DETAILS_MISSING.getStatusCode()));
	}
	
	@Test
	public void validateCreateTransactionAPIWithoutKeyInCreditParty() {
		with().contentType(ContentType.JSON).accept(ContentType.JSON)
				.body(DataPreparationUtil.prepareTransactionRequestWithoutKeyInCreditParty(
						TestCaseConstants.DEFAULT_AMOUNT.getValue(),
						TestCaseConstants.INVALID_MSISDN.getValue(), TestCaseConstants.DEFAULT_CURRENCY.getValue(),
						TestCaseConstants.DEFAULT_PAYER_MSISDN.getValue(), TestCaseConstants.DEFAULT_PIN.getValue(),
						TestCaseConstants.EXT_ORG_REF_ID_VALUE.getValue(), TestCaseConstants.P2P_TXN_TYPE_VALUE.getValue()))
				.given()
				.header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue())
				.when().post(TestCaseConstants.TRANSACTIONS_BASE_CONTEXT.getValue()).then().assertThat()
				.statusCode(HttpStatus.BAD_REQUEST.value())
				;
	}
	
	@Test
	public void validateCreateTransactionAPIWithoutMSISDNAsKeyInCreditParty() {
		with().contentType(ContentType.JSON).accept(ContentType.JSON)
				.body(DataPreparationUtil.prepareTransactionRequestWithoutMSISDNAsKeyInCreditParty(
						TestCaseConstants.DEFAULT_AMOUNT.getValue(),
						TestCaseConstants.INVALID_MSISDN.getValue(), TestCaseConstants.DEFAULT_CURRENCY.getValue(),
						TestCaseConstants.DEFAULT_PAYER_MSISDN.getValue(), TestCaseConstants.DEFAULT_PIN.getValue(),
						TestCaseConstants.EXT_ORG_REF_ID_VALUE.getValue(), TestCaseConstants.P2P_TXN_TYPE_VALUE.getValue()))
				.given()
				.header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue())
				.when().post(TestCaseConstants.TRANSACTIONS_BASE_CONTEXT.getValue()).then().assertThat()
				.statusCode(HttpStatus.BAD_REQUEST.value())
				;
	}
	
	@Test
	public void validateCreateTransactionAPIWithoutValueInCreditParty() {
		with().contentType(ContentType.JSON).accept(ContentType.JSON)
				.body(DataPreparationUtil.prepareTransactionRequestWithoutValueInCreditParty(
						TestCaseConstants.DEFAULT_AMOUNT.getValue(),
						TestCaseConstants.INVALID_MSISDN.getValue(), TestCaseConstants.DEFAULT_CURRENCY.getValue(),
						TestCaseConstants.DEFAULT_PAYER_MSISDN.getValue(), TestCaseConstants.DEFAULT_PIN.getValue(),
						TestCaseConstants.EXT_ORG_REF_ID_VALUE.getValue(), TestCaseConstants.P2P_TXN_TYPE_VALUE.getValue()))
				.given()
				.header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue())
				.when().post(TestCaseConstants.TRANSACTIONS_BASE_CONTEXT.getValue()).then().assertThat()
				.statusCode(HttpStatus.BAD_REQUEST.value())
				;
	}
	

	@Test
	public void validateCreateTransactionAPIWithEmptySenderMSISDN() {
		with().contentType(ContentType.JSON).accept(ContentType.JSON)
				.body(DataPreparationUtil.prepareTransactionRequest(TestCaseConstants.DEFAULT_AMOUNT.getValue(),
						TestCaseConstants.DEFAULT_MSISDN.getValue(), TestCaseConstants.DEFAULT_CURRENCY.getValue(),
						TestCaseConstants.EMPTY.getValue(), TestCaseConstants.DEFAULT_PIN.getValue(),
						TestCaseConstants.EXT_ORG_REF_ID_VALUE.getValue(), TestCaseConstants.P2P_TXN_TYPE_VALUE.getValue()))
				.given()
				.header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue())
				.when().post(TestCaseConstants.TRANSACTIONS_BASE_CONTEXT.getValue()).then().assertThat()
				.statusCode(HttpStatus.OK.value()).and().assertThat()
				.body(TestCaseConstants.CODE_NAME.getValue(), equalTo(ValidationErrors.SENDER_MSISDN_MISSING.getEntity() + "_"
						+ ValidationErrors.SENDER_MSISDN_MISSING.getStatusCode()));
	}

	@Test
	public void validateCreateTransactionAPIWithInvalidLengthOfSenderMSISDN() {
		with().contentType(ContentType.JSON).accept(ContentType.JSON)
				.body(DataPreparationUtil.prepareTransactionRequest(TestCaseConstants.DEFAULT_AMOUNT.getValue(),
						TestCaseConstants.DEFAULT_MSISDN.getValue(), TestCaseConstants.DEFAULT_CURRENCY.getValue(),
						TestCaseConstants.INVALID_LENGTH_MSISDN.getValue(), TestCaseConstants.DEFAULT_PIN.getValue(),
						TestCaseConstants.EXT_ORG_REF_ID_VALUE.getValue(), TestCaseConstants.P2P_TXN_TYPE_VALUE.getValue()))
				.given()
				.header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue())
				.when().post(TestCaseConstants.TRANSACTIONS_BASE_CONTEXT.getValue()).then().assertThat()
				.statusCode(HttpStatus.OK.value()).and().assertThat()
				.body(TestCaseConstants.CODE_NAME.getValue(), equalTo(ValidationErrors.INVALID_SENDER_MSISDN_LENGTH.getEntity()
						+ "_" + ValidationErrors.INVALID_SENDER_MSISDN_LENGTH.getStatusCode()));
	}

	@Test
	public void validateCreateTransactionAPIWithNonNumericSenderMSISDN() {
		with().contentType(ContentType.JSON).accept(ContentType.JSON)
				.body(DataPreparationUtil.prepareTransactionRequest(TestCaseConstants.DEFAULT_AMOUNT.getValue(),
						TestCaseConstants.DEFAULT_MSISDN.getValue(), TestCaseConstants.DEFAULT_CURRENCY.getValue(),
						TestCaseConstants.INVALID_MSISDN.getValue(), TestCaseConstants.DEFAULT_PIN.getValue(),
						TestCaseConstants.EXT_ORG_REF_ID_VALUE.getValue(), TestCaseConstants.P2P_TXN_TYPE_VALUE.getValue()))
				.given()
				.header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue())
				.when().post(TestCaseConstants.TRANSACTIONS_BASE_CONTEXT.getValue()).then().assertThat()
				.statusCode(HttpStatus.OK.value()).and().assertThat().body(TestCaseConstants.CODE_NAME.getValue(),
						equalTo(ValidationErrors.SENDER_MSISDN_SHOULD_BE_NUMERIC.getEntity() + "_"
								+ ValidationErrors.SENDER_MSISDN_SHOULD_BE_NUMERIC.getStatusCode()));
	}
	
	@Test
	public void validateCreateTransactionAPIWithoutDebitParty() {
		with().contentType(ContentType.JSON).accept(ContentType.JSON)
				.body(DataPreparationUtil.prepareTransactionRequestWithoutDebitParty(
						TestCaseConstants.DEFAULT_AMOUNT.getValue(), TestCaseConstants.DEFAULT_MSISDN.getValue(),TestCaseConstants.DEFAULT_CURRENCY.getValue(),
						TestCaseConstants.EXT_ORG_REF_ID_VALUE.getValue(), TestCaseConstants.P2P_TXN_TYPE_VALUE.getValue()))
				.given()
				.header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue())
				.when().post(TestCaseConstants.TRANSACTIONS_BASE_CONTEXT.getValue()).then().assertThat()
				.statusCode(HttpStatus.OK.value())
				.and().assertThat().body(TestCaseConstants.CODE_NAME.getValue(),
						equalTo(ValidationErrors.SENDER_PARTY_DETAILS_MISSING.getEntity() + "_"
								+ ValidationErrors.SENDER_PARTY_DETAILS_MISSING.getStatusCode()));
	}
	
	@Test
	public void validateCreateTransactionAPIWithEmptyCurrencyValue() {
		with().contentType(ContentType.JSON).accept(ContentType.JSON)
				.body(DataPreparationUtil.prepareTransactionRequest(TestCaseConstants.DEFAULT_AMOUNT.getValue(),
						TestCaseConstants.DEFAULT_MSISDN.getValue(), TestCaseConstants.EMPTY.getValue(),
						TestCaseConstants.DEFAULT_PAYER_MSISDN.getValue(), TestCaseConstants.DEFAULT_PIN.getValue(),
						TestCaseConstants.EXT_ORG_REF_ID_VALUE.getValue(), TestCaseConstants.P2P_TXN_TYPE_VALUE.getValue()))
				.given()
				.header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue())
				.when().post(TestCaseConstants.TRANSACTIONS_BASE_CONTEXT.getValue()).then().assertThat()
				.statusCode(HttpStatus.OK.value()).and().assertThat().body(TestCaseConstants.CODE_NAME.getValue(),
						equalTo(ValidationErrors.CURRENCY_MISSING.getEntity() + "_"
								+ ValidationErrors.CURRENCY_MISSING.getStatusCode()));
	}

	@Test
	public void validateCreateTransactionAPIWithInvalidCurrencyValue() {
		with().contentType(ContentType.JSON).accept(ContentType.JSON)
				.body(DataPreparationUtil.prepareTransactionRequest(TestCaseConstants.DEFAULT_AMOUNT.getValue(),
						TestCaseConstants.DEFAULT_MSISDN.getValue(), TestCaseConstants.INVALID_CURRENCY.getValue(),
						TestCaseConstants.DEFAULT_PAYER_MSISDN.getValue(), TestCaseConstants.DEFAULT_PIN.getValue(),
						TestCaseConstants.EXT_ORG_REF_ID_VALUE.getValue(), TestCaseConstants.P2P_TXN_TYPE_VALUE.getValue()))
				.given()
				.header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue())
				.when().post(TestCaseConstants.TRANSACTIONS_BASE_CONTEXT.getValue()).then().assertThat()
				.statusCode(HttpStatus.OK.value()).and().assertThat().body(TestCaseConstants.CODE_NAME.getValue(),
						equalTo(ValidationErrors.INVALID_CURRENCY.getEntity() + "_"
								+ ValidationErrors.INVALID_CURRENCY.getStatusCode()));
	}
	
	@Test
	public void validateCreateTransactionAPIWithEmptySenderPIN() {
		with().contentType(ContentType.JSON).accept(ContentType.JSON)
				.body(DataPreparationUtil.prepareTransactionRequest(TestCaseConstants.DEFAULT_AMOUNT.getValue(),
						TestCaseConstants.DEFAULT_MSISDN.getValue(), TestCaseConstants.DEFAULT_CURRENCY.getValue(),
						TestCaseConstants.DEFAULT_PAYER_MSISDN.getValue(), TestCaseConstants.EMPTY.getValue(),
						TestCaseConstants.EXT_ORG_REF_ID_VALUE.getValue(), TestCaseConstants.P2P_TXN_TYPE_VALUE.getValue()))
				.given()
				.header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue())
				.when().post(TestCaseConstants.TRANSACTIONS_BASE_CONTEXT.getValue()).then().assertThat()
				.statusCode(HttpStatus.OK.value()).and().assertThat()
				.body(TestCaseConstants.CODE_NAME.getValue(), equalTo(ValidationErrors.PIN_MISSING.getEntity() + "_"
						+ ValidationErrors.PIN_MISSING.getStatusCode()));
	}

	@Test
	public void validateCreateTransactionAPIWithInvalidLengthOfSenderPIN() {
		with().contentType(ContentType.JSON).accept(ContentType.JSON)
				.body(DataPreparationUtil.prepareTransactionRequest(TestCaseConstants.DEFAULT_AMOUNT.getValue(),
						TestCaseConstants.DEFAULT_MSISDN.getValue(), TestCaseConstants.DEFAULT_CURRENCY.getValue(),
						TestCaseConstants.DEFAULT_PAYER_MSISDN.getValue(), TestCaseConstants.INVALID_PIN.getValue(),
						TestCaseConstants.EXT_ORG_REF_ID_VALUE.getValue(), TestCaseConstants.P2P_TXN_TYPE_VALUE.getValue()))
				.given()
				.header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue())
				.when().post(TestCaseConstants.TRANSACTIONS_BASE_CONTEXT.getValue()).then().assertThat()
				.statusCode(HttpStatus.OK.value()).and().assertThat()
				.body(TestCaseConstants.CODE_NAME.getValue(), equalTo(ValidationErrors.INVALID_PIN_LENGTH.getEntity()
						+ "_" + ValidationErrors.INVALID_PIN_LENGTH.getStatusCode()));
	}

	
	@Test
	public void validateCreateTransactionAPIWithEmptyExtOrgRefId() {
		with().contentType(ContentType.JSON).accept(ContentType.JSON)
				.body(DataPreparationUtil.prepareTransactionRequest(TestCaseConstants.DEFAULT_AMOUNT.getValue(),
						TestCaseConstants.DEFAULT_MSISDN.getValue(), TestCaseConstants.DEFAULT_CURRENCY.getValue(),
						TestCaseConstants.DEFAULT_PAYER_MSISDN.getValue(), TestCaseConstants.DEFAULT_PIN.getValue(),
						TestCaseConstants.EMPTY.getValue(), TestCaseConstants.P2P_TXN_TYPE_VALUE.getValue()))
				.given()
				.header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue())
				.when().post(TestCaseConstants.TRANSACTIONS_BASE_CONTEXT.getValue()).then().assertThat()
				.statusCode(HttpStatus.OK.value()).and().assertThat()
				.body(TestCaseConstants.CODE_NAME.getValue(), equalTo(ValidationErrors.EXT_ORG_REF_ID_MISSING.getEntity() + "_"
						+ ValidationErrors.EXT_ORG_REF_ID_MISSING.getStatusCode()));
	}
	
	@Test
	public void validateCreateTransactionAPIWithEmptyTransactionType() {
		with().contentType(ContentType.JSON).accept(ContentType.JSON)
				.body(DataPreparationUtil.prepareTransactionRequest(TestCaseConstants.DEFAULT_AMOUNT.getValue(),
						TestCaseConstants.DEFAULT_MSISDN.getValue(), TestCaseConstants.DEFAULT_CURRENCY.getValue(),
						TestCaseConstants.DEFAULT_PAYER_MSISDN.getValue(), TestCaseConstants.DEFAULT_PIN.getValue(),
						TestCaseConstants.EXT_ORG_REF_ID_VALUE.getValue(), TestCaseConstants.EMPTY.getValue()))
				.given()
				.header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue())
				.when().post(TestCaseConstants.TRANSACTIONS_BASE_CONTEXT.getValue()).then().assertThat()
				.statusCode(HttpStatus.OK.value()).and().assertThat()
				.body(TestCaseConstants.CODE_NAME.getValue(), equalTo(ValidationErrors.TRANSACTION_TYPE_MISSING.getEntity() + "_"
						+ ValidationErrors.TRANSACTION_TYPE_MISSING.getStatusCode()));
	}
	
	@Test
    public void validateCreateTransactionAPIForMPOnUsWithValidData() {

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
        with().contentType(ContentType.JSON).accept(ContentType.JSON)
                .body(DataPreparationUtil.prepareTransactionRequest(TestCaseConstants.DEFAULT_AMOUNT.getValue(),
                        Long.toString(recevierMSISDN), TestCaseConstants.DEFAULT_CURRENCY.getValue(),
                        TestCaseConstants.DEFAULT_PAYER_MSISDN.getValue(), TestCaseConstants.DEFAULT_PIN.getValue(),
                        TestCaseConstants.EXT_ORG_REF_ID_VALUE.getValue(), TestCaseConstants.MP_TXN_TYPE_VALUE.getValue()))
                .given()
                .header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue())
                .when().post(TestCaseConstants.TRANSACTIONS_BASE_CONTEXT.getValue()).then().assertThat()
                .statusCode(HttpStatus.OK.value()).and().assertThat()
                .body(TestCaseConstants.MAPPED_CODE_NAME.getValue(),
                        equalTo(ThirdPartyResponseCodes.SUCCESS.getMappedCode()));
    }
	
	@Test
	public void validateCreateTransactionAPIWhenGetLangAPIResponseIsOtherThanSuccess() {

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
	}
	

	public String getPath(String interopReferenceId) {
		return resource.getNonFinancialServerUrlValue() + TestCaseConstants.BASE_CONTEXT.getValue()
				+ interopReferenceId + TestCaseConstants.BASE_CONTEXT_END.getValue();
	}
}
