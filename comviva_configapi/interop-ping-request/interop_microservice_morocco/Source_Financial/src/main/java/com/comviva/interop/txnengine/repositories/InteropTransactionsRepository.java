package com.comviva.interop.txnengine.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.comviva.interop.txnengine.entities.InteropTransactions;

@Repository
public interface InteropTransactionsRepository extends CrudRepository<InteropTransactions, String> {

    @Query("SELECT t FROM InteropTransactions t WHERE t.interopTxnId =:interopTxnId")
    InteropTransactions findInteropTransactionsByTxnId(@Param("interopTxnId") String interopTxnId);

    @Query("SELECT t FROM InteropTransactions t WHERE t.mobiquityTransactionId =:mobiquityTransactionId")
    InteropTransactions findInteropTransactionsByMobiquityTxnId(@Param("mobiquityTransactionId") String mobiquityTransactionId);
    
    @Query("SELECT t FROM InteropTransactions t WHERE t.retrievalReferenceNumber =:retrievalReferenceNumber")
    InteropTransactions findInteropTransactionsByRetrievalReferenceNumber(@Param("retrievalReferenceNumber") String retrievalReferenceNumber);
}
