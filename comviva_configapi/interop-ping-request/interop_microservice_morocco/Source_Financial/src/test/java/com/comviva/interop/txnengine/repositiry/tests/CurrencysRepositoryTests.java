package com.comviva.interop.txnengine.repositiry.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.comviva.interop.txnengine.entities.Currency;
import com.comviva.interop.txnengine.enums.TestCaseConstants;
import com.comviva.interop.txnengine.repositories.CurrencyRepository;

@RunWith(SpringRunner.class)
public class CurrencysRepositoryTests {
	
	@MockBean
	CurrencyRepository currencysRepository;
	
	@Test
	public void findAllCurrencyRepositoryTest() {
		Currency currency = new Currency();
		currency.setCode(TestCaseConstants.DEFAULT_CURRENCY.getValue());
		currency.setCountry("ÏNDIA");
		currency.setCurrencyName("RUPEE");
		List<Currency> list = new ArrayList<>();
		list.add(currency);
		when(currencysRepository.findAll()).thenReturn(list);
		Iterator<Currency> currencyIterable = currencysRepository.findAll().iterator();
		while (currencyIterable.hasNext()) {
			Currency currency2 = currencyIterable.next(); 
			assertThat(currency2.getCode(), is(TestCaseConstants.DEFAULT_CURRENCY.getValue()));	
			assertThat(currency2.getCountry(), is("ÏNDIA"));
			assertThat(currency2.getCurrencyName(), is("RUPEE"));
		}
	}
}
