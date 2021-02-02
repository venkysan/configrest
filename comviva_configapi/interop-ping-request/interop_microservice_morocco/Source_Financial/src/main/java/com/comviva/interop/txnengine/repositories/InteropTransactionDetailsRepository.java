package com.comviva.interop.txnengine.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.comviva.interop.txnengine.entities.InteropTransactionDetails;

@Repository
public interface InteropTransactionDetailsRepository extends CrudRepository<InteropTransactionDetails, String> {

    @Query("SELECT t FROM InteropTransactionDetails t WHERE t.interopTransactions.interopTxnId =:interopTxnId")
    List<InteropTransactionDetails> findInteropTransactionDetailsByTxnId(@Param("interopTxnId") String interopTxnId);

}
