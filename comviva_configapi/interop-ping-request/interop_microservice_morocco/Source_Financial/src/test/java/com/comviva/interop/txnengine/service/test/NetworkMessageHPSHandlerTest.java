package com.comviva.interop.txnengine.service.test;

import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.comviva.interop.txnengine.model.NetworkMessageRequest;
import com.comviva.interop.txnengine.services.NetworkMessageHPSHandler;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class NetworkMessageHPSHandlerTest {
	
	@InjectMocks
	private NetworkMessageHPSHandler networkMessageHPSHandler;
	@Mock
	private NetworkMessageRequest request;
	
	@Test
    public void NetworkMessageHPSHandlerTest() {
		when(request.getNetworkAction()).thenReturn("signOff");
		networkMessageHPSHandler.execute(request, "en");
	}

}
