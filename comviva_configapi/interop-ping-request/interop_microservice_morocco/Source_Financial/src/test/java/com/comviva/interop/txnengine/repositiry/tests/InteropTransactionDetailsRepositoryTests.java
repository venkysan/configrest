package com.comviva.interop.txnengine.repositiry.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.comviva.interop.txnengine.entities.InteropTransactionDetails;
import com.comviva.interop.txnengine.entities.InteropTransactions;
import com.comviva.interop.txnengine.repositories.InteropTransactionDetailsRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class InteropTransactionDetailsRepositoryTests {
	private static final String TXNID="12345";
	
	@Test
	public void getTransactionDetailsRepositoryTest() {
		InteropTransactionDetailsRepository interopTransactionsDetailsRepository = mock(InteropTransactionDetailsRepository.class);
		List<InteropTransactionDetails> interopTransactions =new ArrayList<>();
		InteropTransactionDetails interopTransactionDetails=new InteropTransactionDetails();
		InteropTransactions interoptxn=new InteropTransactions();
        interoptxn.setInteropTxnId(TXNID);
        interopTransactionDetails.setInteropTransactions(interoptxn);
		interopTransactionDetails.setAmount(new BigDecimal("100"));
		interopTransactionDetails.setCurrency("XOF");
		interopTransactionDetails.setCreatedDate(new Date());
		interopTransactionDetails.setThirdPartyPayer("1212121224");
		interopTransactionDetails.setThirdPartyTxnType("P2P");
		interopTransactionDetails.setTxnStatus("TS");
		interopTransactionDetails.setUpdatedDate(new Date());
		interopTransactionDetails.setThirdPartyRefId("AD23434");
		interopTransactions.add(interopTransactionDetails);
		when(interopTransactionsDetailsRepository.findInteropTransactionDetailsByTxnId(TXNID)).thenReturn(interopTransactions);
		assertThat(interopTransactions.get(0).getInteropTransactions().getInteropTxnId(), is(interopTransactionsDetailsRepository.findInteropTransactionDetailsByTxnId(TXNID).get(0).getInteropTransactions().getInteropTxnId()));
	}
}
