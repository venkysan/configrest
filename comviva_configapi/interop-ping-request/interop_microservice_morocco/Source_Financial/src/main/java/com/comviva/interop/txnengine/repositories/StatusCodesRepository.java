package com.comviva.interop.txnengine.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.comviva.interop.txnengine.entities.StatusCodes;

@Repository
public interface StatusCodesRepository extends CrudRepository<StatusCodes, String> {

    @Query("SELECT c FROM StatusCodes c")
    List<StatusCodes> getAllCodes();

}
