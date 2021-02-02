package com.comviva.interop.txnengine.service.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.comviva.interop.txnengine.entities.InteropTransactions;
import com.comviva.interop.txnengine.enums.TestCaseConstants;
import com.comviva.interop.txnengine.enums.TransactionStatus;
import com.comviva.interop.txnengine.repositories.InteropTransactionsRepository;
import com.comviva.interop.txnengine.services.ConfirmTxnVerificationThread;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ConfirmTxnVerificationThreadTests {
	
	@Test
	public void verifyConfirmTxnThreadNegativeCase() throws Exception {		
		
		InteropTransactionsRepository interopTransactionsRepository = mock(InteropTransactionsRepository.class);
		when(interopTransactionsRepository.findInteropTransactionsByTxnId(TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue())).thenReturn(ArgumentMatchers.any());
		
		ConfirmTxnVerificationThread confirmTxnVerificationThread = new ConfirmTxnVerificationThread(TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue(), interopTransactionsRepository);
		assertEquals(Boolean.FALSE, confirmTxnVerificationThread.call());
	}
	
	@Test
	public void verifyConfirmTxnThreadPositiveCase() throws Exception {		
		
		InteropTransactionsRepository interopTransactionsRepository = mock(InteropTransactionsRepository.class);
		when(interopTransactionsRepository.findInteropTransactionsByTxnId(TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue())).thenReturn(new InteropTransactions());
		
		ConfirmTxnVerificationThread confirmTxnVerificationThread = new ConfirmTxnVerificationThread(TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue(), interopTransactionsRepository);
		assertEquals(Boolean.TRUE, confirmTxnVerificationThread.call());
	}
	
	@Test
	public void verifyConfirmTxnThreadPositiveCaseWithTIStatus() throws Exception {		
		
		InteropTransactions interopTransactions = new InteropTransactions();
		interopTransactions.setTxnStatus(TransactionStatus.TRANSACTION_INITIATED.getStatus());
		InteropTransactionsRepository interopTransactionsRepository = mock(InteropTransactionsRepository.class);
		when(interopTransactionsRepository.findInteropTransactionsByTxnId(TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue())).thenReturn(interopTransactions);
		
		ConfirmTxnVerificationThread confirmTxnVerificationThread = new ConfirmTxnVerificationThread(TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue(), interopTransactionsRepository);
		assertEquals(Boolean.FALSE, confirmTxnVerificationThread.call());
	}
	
	@Test
	public void verifyConfirmTxnThreadPositiveCaseWithTSStatus() throws Exception {		
		
		InteropTransactions interopTransactions = new InteropTransactions();
		interopTransactions.setTxnStatus(TransactionStatus.TRANSACTION_SUCCESS.getStatus());
		InteropTransactionsRepository interopTransactionsRepository = mock(InteropTransactionsRepository.class);
		when(interopTransactionsRepository.findInteropTransactionsByTxnId(TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue())).thenReturn(interopTransactions);
		
		ConfirmTxnVerificationThread confirmTxnVerificationThread = new ConfirmTxnVerificationThread(TestCaseConstants.INTEROP_REFERENCE_ID_VALUE.getValue(), interopTransactionsRepository);
		assertEquals(Boolean.TRUE, confirmTxnVerificationThread.call());
	}
}
