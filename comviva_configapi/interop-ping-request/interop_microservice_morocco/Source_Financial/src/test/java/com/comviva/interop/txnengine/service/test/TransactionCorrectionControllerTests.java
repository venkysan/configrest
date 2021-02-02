package com.comviva.interop.txnengine.service.test;

import static io.restassured.RestAssured.with;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import com.comviva.interop.txnengine.configuration.BrokerServiceProperties;
import com.comviva.interop.txnengine.configuration.BrokerServiceURLProperties;
import com.comviva.interop.txnengine.configuration.ServiceResources;
import com.comviva.interop.txnengine.controllers.TransactionCorrectionController;
import com.comviva.interop.txnengine.entities.InteropTransactions;
import com.comviva.interop.txnengine.enums.LogConstants;
import com.comviva.interop.txnengine.enums.TestCaseConstants;
import com.comviva.interop.txnengine.mobiquity.service.handlers.UserAuthenticationHandler;
import com.comviva.interop.txnengine.repositories.ChannelUserDetailsRepository;
import com.comviva.interop.txnengine.repositories.InteropTransactionsRepository;
import com.comviva.interop.txnengine.request.validations.GetDescriptionForCode;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TransactionCorrectionControllerTests {

	@LocalServerPort
	private int port;

	@Mock
	private InteropTransactionsRepository interopTransactionsRepository;

	@Mock
	private GetDescriptionForCode getDescriptionForCode;

	@Mock
	private ServiceResources serviceResources;

	@Mock
	private UserAuthenticationHandler userAuthenticationHandler;

	@Mock
	private ChannelUserDetailsRepository channelUserDetailsRepository;

	@Mock
	private BrokerServiceURLProperties brokerServiceURLProperties;

	@InjectMocks
	private TransactionCorrectionController transactionCorrectionController;

	@Mock
	private BrokerServiceProperties thirdPartyProperties;

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
	public void validateTransactionCorrectionAPIIfAlreadyCorrected() {
		InteropTransactions interopTransactions = new InteropTransactions();
		interopTransactions.setTxnCorrectionId("TI001");
		when(interopTransactionsRepository
				.findInteropTransactionsByMobiquityTxnId(LogConstants.EMPTY_STRING.getValue()))
						.thenReturn(interopTransactions);
		transactionCorrectionController.doTxnCorrection(LogConstants.EMPTY_STRING.getValue(),
				LogConstants.EMPTY_STRING.getValue());
		Mockito.verify(interopTransactionsRepository, Mockito.times(1))
				.findInteropTransactionsByMobiquityTxnId(LogConstants.EMPTY_STRING.getValue());
	}

	@Test
	public void validateTransactionCorrectionAPIForExceptionCase() {
		when(thirdPartyProperties.getChannelUserMsisdn()).thenReturn(TestCaseConstants.CHANNEL_USER_MSISDN.getValue());
		when(interopTransactionsRepository
				.findInteropTransactionsByMobiquityTxnId(LogConstants.EMPTY_STRING.getValue()))
						.thenThrow(DataIntegrityViolationException.class);
		transactionCorrectionController.doTxnCorrection(LogConstants.EMPTY_STRING.getValue(),
				LogConstants.EMPTY_STRING.getValue());
		Mockito.verify(interopTransactionsRepository, Mockito.times(1))
				.findInteropTransactionsByMobiquityTxnId(LogConstants.EMPTY_STRING.getValue());
	}
}
