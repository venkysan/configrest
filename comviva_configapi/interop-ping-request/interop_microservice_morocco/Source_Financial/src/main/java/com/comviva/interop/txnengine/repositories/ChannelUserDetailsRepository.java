package com.comviva.interop.txnengine.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.comviva.interop.txnengine.entities.ChannelUserDetails;

@Repository
public interface ChannelUserDetailsRepository extends CrudRepository<ChannelUserDetails, String> {

    @Query("SELECT t FROM ChannelUserDetails t WHERE t.userId =:userId")
    ChannelUserDetails findChannelUserDetailsByUserId(@Param("userId") String userId);

    @Query("SELECT t FROM ChannelUserDetails t WHERE t.msisdn =:msisdn")
    ChannelUserDetails findChannelUserDetailsByMsisdn(@Param("msisdn") String msisdn);

}
