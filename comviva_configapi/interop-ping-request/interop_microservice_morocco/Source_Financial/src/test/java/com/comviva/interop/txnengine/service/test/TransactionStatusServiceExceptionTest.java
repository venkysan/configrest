package com.comviva.interop.txnengine.service.test;

import static io.restassured.RestAssured.with;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import com.comviva.interop.txnengine.enums.TestCaseConstants;
import com.comviva.interop.txnengine.request.validations.RequestValidations;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TransactionStatusServiceExceptionTest {
	
	@LocalServerPort
	private int port;
	
	@MockBean
	private RequestValidations requestValidations;
	
	@Before
	public void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
	}

	@Test
	public void validateTransactionStatusExceptionCase() {
		when(requestValidations.validateLanguage(Mockito.anyString())).thenReturn(null);
		with().contentType(ContentType.JSON).accept(ContentType.JSON).given()
		.header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue()).when().get(TestCaseConstants.TRANSACTIONS_BASE_CONTEXT.getValue() + TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue() + "/" + TestCaseConstants.LANGUAGE_CODE.getValue()).then()
		.assertThat().statusCode(HttpStatus.OK.value())
		.and().assertThat()
		.body(TestCaseConstants.MAPPED_CODE_NAME.getValue(),
				equalTo(TestCaseConstants.ERRORDEFAULT_INTERNAL.getValue()));
	}
}
