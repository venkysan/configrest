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
import com.comviva.interop.txnengine.enums.TestCaseConstants;
import com.comviva.interop.txnengine.enums.ThirdPartyResponseCodes;
import com.comviva.interop.txnengine.util.DataPreparationUtil;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReportsAPIControllerTests {

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
    public void verifyGetOMoperations() {
        
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
        
        with().contentType(ContentType.JSON).accept(ContentType.JSON).given()
        .header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue())
        .when()
        .get("/v1/" + TestCaseConstants.DEFAULT_COUNTRY_ID.getValue() + "/om-operations"+ "?"
                + TestCaseConstants.FROM_PARAM.getValue() + "2019-05-01T17:32:28Z"
                +"&"
                + TestCaseConstants.LIMIT_PARAM.getValue() + "2")
        .then().assertThat().statusCode(HttpStatus.OK.value()).and().assertThat();

    }
    
    @Test
    public void verifyGetTechnicalWallets() {
        with().contentType(ContentType.JSON).accept(ContentType.JSON).given()
        .header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue())
        .when()
        .get("/v1/" + TestCaseConstants.DEFAULT_COUNTRY_ID.getValue() + "/technical‐wallets")
        .then().assertThat().statusCode(HttpStatus.OK.value()).and().assertThat();
    }
    
    public String getPath(String interopReferenceId) {
        return resource.getNonFinancialServerUrlValue() + TestCaseConstants.BASE_CONTEXT.getValue()
                + interopReferenceId + TestCaseConstants.BASE_CONTEXT_END.getValue();
    }
}
