package com.comviva.interop.txnengine.services;

import java.util.concurrent.Callable;

import com.comviva.interop.txnengine.entities.InteropTransactions;
import com.comviva.interop.txnengine.enums.TransactionStatus;
import com.comviva.interop.txnengine.repositories.InteropTransactionsRepository;


public class ConfirmTxnVerificationThread implements Callable<Boolean> {
	
    private String interOpTxnId;
        
    private InteropTransactionsRepository interOpTransactionRepository;
    
    public ConfirmTxnVerificationThread(String interOpTxnId, InteropTransactionsRepository interOpTransactionRepository) {
		this.interOpTxnId = interOpTxnId;
		this.interOpTransactionRepository = interOpTransactionRepository;
	}
    
	@Override
	public Boolean call() throws Exception {
		InteropTransactions interopTransactions = interOpTransactionRepository.findInteropTransactionsByTxnId(this.interOpTxnId);
		if(null != interopTransactions && !TransactionStatus.TRANSACTION_INITIATED.getStatus().equals(interopTransactions.getTxnStatus())) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
}
