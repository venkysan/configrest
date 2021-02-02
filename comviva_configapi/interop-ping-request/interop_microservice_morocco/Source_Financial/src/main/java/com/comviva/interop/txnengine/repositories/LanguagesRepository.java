package com.comviva.interop.txnengine.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.comviva.interop.txnengine.entities.Languages;

@Repository
public interface LanguagesRepository extends CrudRepository<Languages, String> {

    @Query("SELECT langCode FROM Languages ")
    Set<String> getAllLangCodes();

    @Query("SELECT l FROM Languages l ")
    public List<Languages> getAllLanguages();
}
