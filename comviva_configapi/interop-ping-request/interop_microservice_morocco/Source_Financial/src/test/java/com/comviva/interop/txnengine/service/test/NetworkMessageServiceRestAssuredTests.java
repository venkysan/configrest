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

import com.comviva.interop.txnengine.enums.TestCaseConstants;
import com.comviva.interop.txnengine.enums.ValidationErrors;
import com.comviva.interop.txnengine.util.DataPreparationUtil;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class NetworkMessageServiceRestAssuredTests {

	@LocalServerPort
	private int port;

	@Before
	public void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
	}

	@Test
	public void validateNetworkMessageAPISuccessCase() {
		with().contentType(ContentType.JSON).accept(ContentType.JSON)
				.body(DataPreparationUtil.getNetworkMessageRequest()).given()
				.header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue())
				.when().post(TestCaseConstants.NETWORK_MESSAGE_URL.getValue()).then().assertThat()
				.contentType(ContentType.JSON).and().assertThat().statusCode(HttpStatus.OK.value()).and().assertThat()
				.body(TestCaseConstants.CODE_NAME.getValue(),
						equalTo(ValidationErrors.INVALID_ACTION_TYPE.getEntity().toString() + "_"
								+ ValidationErrors.INVALID_ACTION_TYPE.getStatusCode()));
	}
}
