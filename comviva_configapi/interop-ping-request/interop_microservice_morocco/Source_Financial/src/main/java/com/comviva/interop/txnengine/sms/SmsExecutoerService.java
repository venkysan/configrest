package com.comviva.interop.txnengine.sms;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comviva.interop.txnengine.configuration.SMSServerProperties;

@Service
public class SmsExecutoerService {

    @Autowired
    private SMSFetchingThread smsFetchingThread;

    @Autowired
    private SMSServerProperties resource;

    public SmsExecutoerService(SMSServerProperties resource) {
        this.resource = resource;
    }

    @PostConstruct
    public void init() {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(smsFetchingThread, resource.getInitialDelay(),
                resource.getPeriodTime(), TimeUnit.SECONDS);
    }
}
