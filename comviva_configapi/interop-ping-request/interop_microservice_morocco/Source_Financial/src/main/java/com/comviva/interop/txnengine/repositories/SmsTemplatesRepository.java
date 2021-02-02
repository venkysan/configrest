package com.comviva.interop.txnengine.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.comviva.interop.txnengine.entities.SmsTemplates;

/**
 * This interface used for CRUD operations for SmsTemplates
 * 
 * @author radhakrishnab
 *
 */

@Repository
public interface SmsTemplatesRepository extends CrudRepository<SmsTemplates, String> {

    @Query("SELECT s FROM SmsTemplates s WHERE s.notificationCode = :notificationCode and s.languageCode = :languageCode ")
    public SmsTemplates findSmsTemplateByTypeAndLang(@Param("notificationCode") String notificationCode,
            @Param("languageCode") String languageCode);

}
