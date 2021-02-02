package com.comviva.interop.txnengine;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import com.comviva.interop.txnengine.configuration.HomeController;

@RunWith(SpringRunner.class)
public class HomeControllerTests {
	
	 	@Test
		public void verifyIndex() {
			assertThat("redirect:swagger-ui.html", is(new HomeController().index()));
		}
}
