package com.comviva.interop.txnengine.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Date;

import org.junit.Test;

public class DateUtilsTest {

	@Test
    public void getTransmissionDateTimeTest() {
		Date date = DateUtils.getTransmissionDateTime("2204050101");
		assertThat(DateUtils.getTransmissionDateTime(date), is("22123600101"));
		Date date1 = DateUtils.getTransactionLocalDateTime("2204050101");
		assertThat(DateUtils.getTransactionLocalDateTime(date1), is(notNullValue()));
		Date date2 = DateUtils.getExchangeDate("2204050101");
		assertThat(DateUtils.getExchangeDate(date2), is(notNullValue()));
		Date date3 = DateUtils.getSettlementDate("2204050101");
		assertThat(DateUtils.getSettlementDate(date3), is(notNullValue()));
		
		Date date4 = DateUtils.getTransmissionDateTime("ABCED");
		assertThat(DateUtils.getTransmissionDateTime(date4), is(notNullValue()));
		Date date5 = DateUtils.getTransactionLocalDateTime("ABCED");
		assertThat(DateUtils.getTransactionLocalDateTime(date5), is(notNullValue()));
		Date date6 = DateUtils.getExchangeDate("ABCED");
		assertThat(DateUtils.getExchangeDate(date6), is(notNullValue()));
		Date date7 = DateUtils.getSettlementDate("ABCED");
		assertThat(DateUtils.getSettlementDate(date7), is(notNullValue()));
		
	}
	
	
    
	
}
