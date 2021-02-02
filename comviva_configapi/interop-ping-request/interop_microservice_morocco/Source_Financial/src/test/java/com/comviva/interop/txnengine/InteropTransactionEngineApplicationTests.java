package com.comviva.interop.txnengine;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.comviva.interop.txnengine.api.V1Api;


@RunWith(SpringRunner.class)
@SpringBootTest
public class InteropTransactionEngineApplicationTests {

	@Autowired
	private V1Api v1Api;
	
	@Test
	public void contextLoads() {
		InteropTransactionEngineApplication.main(new String[] { "--spring.main.web-environment=false",
		});
		assertThat(v1Api).isNotNull();
	}
	
}
