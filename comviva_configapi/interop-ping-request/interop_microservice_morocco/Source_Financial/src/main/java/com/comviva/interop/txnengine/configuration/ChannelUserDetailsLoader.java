package com.comviva.interop.txnengine.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comviva.interop.txnengine.entities.ChannelUserDetails;
import com.comviva.interop.txnengine.enums.LogConstants;
import com.comviva.interop.txnengine.repositories.ChannelUserDetailsRepository;
import com.comviva.interop.txnengine.util.LoggerUtil;

@Component
public class ChannelUserDetailsLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelUserDetailsLoader.class);

    @Autowired
    private ChannelUserDetailsRepository channelUserDetailsRepository;

    public ChannelUserDetails channelUserDetailsByMsisdn(String msisdn) {
        String message = LoggerUtil.printLogForChannelUserByMSISDN(LogConstants.CHANNEL_USER_BY_MSISDN_EVENT.getValue(), null, msisdn);
        LOGGER.info("findChannelUserDetailsByMsisdn  {}", message);
        return channelUserDetailsRepository.findChannelUserDetailsByMsisdn(msisdn);
    }
}
