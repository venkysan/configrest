package com.comviva.interop.txnengine.service.test;

import static io.restassured.RestAssured.with;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import com.comviva.interop.txnengine.enums.TestCaseConstants;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CacheRefreshTests {
	@LocalServerPort
	private int port;
	
	private static final String BASE_CONTEXT="/v1/interop/refresh/";
	
	@Before
    public void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }
	
	@Test
    public void validateWithUnAuthorizedAccess() {
	        with().contentType(ContentType.JSON).accept(ContentType.JSON).when().get(BASE_CONTEXT).
        	then().assertThat().statusCode(HttpStatus.UNAUTHORIZED.value());
    }
	
	@Test
    public void reloadStatusCodes() {
		
		with().contentType(ContentType.JSON).accept(ContentType.JSON).given().header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue()).when().get(BASE_CONTEXT +"StatusCodes").then().assertThat().statusCode(HttpStatus.OK.value());
    }
	
	@Test
    public void reloadLangauge() {
		
		with().contentType(ContentType.JSON).accept(ContentType.JSON).given().header(TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue()).when().get(BASE_CONTEXT +"LanguageCodes").then().assertThat().statusCode(HttpStatus.OK.value());
    }
	
	
}
