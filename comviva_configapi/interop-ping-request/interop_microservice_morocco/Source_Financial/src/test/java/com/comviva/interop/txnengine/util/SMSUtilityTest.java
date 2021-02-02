package com.comviva.interop.txnengine.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class SMSUtilityTest {

	
	Logger logger = LoggerFactory.getLogger(SMSUtilityTest.class);
	
	@Test
    public void sendSmsTest() {
		   
		try {
		
			String message = SMSUtility.sendSms("SDSXXSDDEE", "Enrolment Success", "MOBIQUITY", "123456789", "http://localhost:7070");
			assertThat(message, is(notNullValue()));
		}catch(Exception e) {
			logger.error(e.getMessage());
		}
	
    }
	
	
	
}
