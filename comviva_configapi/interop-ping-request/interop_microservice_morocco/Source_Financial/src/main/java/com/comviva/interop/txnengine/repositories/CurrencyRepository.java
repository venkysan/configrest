package com.comviva.interop.txnengine.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.comviva.interop.txnengine.entities.Currency;

@Repository
public interface CurrencyRepository extends CrudRepository<Currency, String> {

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Currency c WHERE c.code = :code")
    boolean isCurrencyCode(@Param("code") String currencyCode);

    @Query("SELECT c FROM Currency c WHERE c.code = :code")
    List<Currency> getCurrencyByCode(@Param("code") String currencyCode);

}
