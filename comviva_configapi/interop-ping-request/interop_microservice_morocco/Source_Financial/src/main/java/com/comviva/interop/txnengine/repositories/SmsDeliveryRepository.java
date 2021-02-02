package com.comviva.interop.txnengine.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.comviva.interop.txnengine.entities.SmsDelivery;

/**
 * This interface used for CRUD operations for SmsDelivery
 * 
 * @author radhakrishnab
 *
 */

@Repository("smsDeliveryRepository")
public interface SmsDeliveryRepository extends CrudRepository<SmsDelivery, String> {

    @Transactional
    @Modifying
    @Query("UPDATE SmsDelivery s SET s.status = :status, s.deliveredOn =:deliveredOn WHERE s.smsId = :smsId")
    public int updateDeliveryStatus(@Param("status") String preferenceValue, @Param("deliveredOn") String deliveredOn,
            @Param("smsId") String smsId);

    @Query(value = "SELECT s FROM SmsDelivery s WHERE s.status = :status AND s.retryCount < :retryCount AND s.nodeName = :nodeName ")
    public List<SmsDelivery> findUnDeliveredMessages(@Param("status") String status,
            @Param("retryCount") int retryCount, @Param("nodeName") String nodeName, Pageable page);

    @Transactional
    @Modifying
    @Query("UPDATE SmsDelivery s SET s.status = :status WHERE s.smsId = :smsId")
    public int updateIntermediateStatus(@Param("status") String preferenceValue, @Param("smsId") String smsId);

}
