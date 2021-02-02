package com.comviva.interop.txnengine.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.comviva.interop.txnengine.entities.BrokerNonFinServiceDetails;

@Repository
public interface BrokerNonFinServiceDetailsRepository extends CrudRepository<BrokerNonFinServiceDetails, String> {
}
