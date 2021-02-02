package com.comviva.interop.txnengine.service.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import com.comviva.interop.txnengine.configuration.NonFinancialServerProperties;
import com.comviva.interop.txnengine.configuration.Resource;
import com.comviva.interop.txnengine.enums.TestCaseConstants;
import com.comviva.interop.txnengine.enums.TransactionTypes;
import com.comviva.interop.txnengine.services.GetDefaultWalletStatusHandler;
import com.comviva.interop.txnengine.util.DataPreparationUtil;
import com.comviva.interop.txnengine.util.ThirdPartyCaller;

import io.restassured.RestAssured;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GetDefaultWalletStatusHandlerTests {
	@LocalServerPort
	private int port;

	@Before
	public void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
	}

	@MockBean
	private NonFinancialServerProperties nonFinancialServerProperties;

	@MockBean
	private ThirdPartyCaller thirdPartyCaller;
	
	@MockBean
	private Resource resource;

	@Test
	public void validateAPIWithUnRegisteredDefaultWalletStatus() {
		when(resource.getNonFinGetUserUrl()).thenReturn("URL");
		when(resource.getNonFinApiKeyName()).thenReturn(TestCaseConstants.API_KEY_HEADER.getValue());
		when(resource.getNonFinApiKeyValue()).thenReturn(TestCaseConstants.API_KEY_HEADER_VALUE.getValue());
		when(nonFinancialServerProperties.getCodeTag()).thenReturn(TestCaseConstants.CODE_NAME.getValue());
		when(nonFinancialServerProperties.getDefaultWalletStatusTag())
				.thenReturn(TestCaseConstants.DEFAULT_WALLET_STATUE.getValue());
		when(thirdPartyCaller.getDefaultWalletStatusFromNonFinancialService("URL"+TestCaseConstants.DEFAULT_PAYER_MSISDN.getValue(),
				TestCaseConstants.API_KEY_HEADER.getValue(), TestCaseConstants.API_KEY_HEADER_VALUE.getValue(), TestCaseConstants.DEFAULT_PAYER_MSISDN.getValue(), TestCaseConstants.DEFAULT_PAYEE_MSISDN.getValue(), TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue()))
						.thenReturn(DataPreparationUtil
								.getUserHandlerResponse(TestCaseConstants.NOT_REGISTERED_WITH_HPS.getValue()));
		GetDefaultWalletStatusHandler getDefaultWalletStatusHandler = new GetDefaultWalletStatusHandler(
				thirdPartyCaller, nonFinancialServerProperties, resource);
		boolean isP2POnUs = getDefaultWalletStatusHandler.isUserRegisteredWithSameMFS(TestCaseConstants.DEFAULT_PAYER_MSISDN.getValue(), 
				TestCaseConstants.DEFAULT_PAYEE_MSISDN.getValue(), TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue(), TransactionTypes.MERCHPAY_PULL.getTransactionType());
		assertThat(isP2POnUs, is(Boolean.TRUE));
	}
}
