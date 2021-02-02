package com.comviva.interop.txnengine.repositiry.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.comviva.interop.txnengine.entities.InteropTransactions;
import com.comviva.interop.txnengine.repositories.InteropTransactionsRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class InteropTransactionRepositoryTests {
	private static final String TXNID="12345";
	
	@Test
	public void getTransactionRepositoryTest() {
		InteropTransactionsRepository interopTransactionsRepository = mock(InteropTransactionsRepository.class);
		InteropTransactions interopTransactions =new InteropTransactions();
		interopTransactions.setInteropTxnId(TXNID);
		interopTransactions.setAmount(new BigDecimal("100"));
		interopTransactions.setCurrency("XOF");
		interopTransactions.setCreatedDate(new Date());
		interopTransactions.setExtOrgRefId("sdad423234");
		interopTransactions.setPayeeMsisdn("1212121223");
		interopTransactions.setPayerMsisdn("1212121224");
		interopTransactions.setRequestSource("Interop");
		interopTransactions.setTransactionType("P2P");
		interopTransactions.setTxnStatus("TS");
		interopTransactions.setUpdatedDate(new Date());
		when(interopTransactionsRepository.findInteropTransactionsByTxnId(TXNID)).thenReturn(interopTransactions);
		assertThat(interopTransactions.getInteropTxnId(), is(interopTransactionsRepository.findInteropTransactionsByTxnId(TXNID).getInteropTxnId()));
	}
}
