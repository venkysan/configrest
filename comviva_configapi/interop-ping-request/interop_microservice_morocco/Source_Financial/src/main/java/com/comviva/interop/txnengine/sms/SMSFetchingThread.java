package com.comviva.interop.txnengine.sms;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.comviva.interop.txnengine.configuration.SMSServerProperties;
import com.comviva.interop.txnengine.entities.SmsDelivery;
import com.comviva.interop.txnengine.enums.LogConstants;
import com.comviva.interop.txnengine.enums.SMSDeliveryStatus;
import com.comviva.interop.txnengine.repositories.SmsDeliveryRepository;
import com.comviva.interop.txnengine.util.LoggerUtil;
import com.google.common.collect.Lists;
import com.google.common.math.IntMath;

@Component
public class SMSFetchingThread implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SMSFetchingThread.class);

    private SmsDeliveryRepository smsDeliveryRepository;

    private ExecutorService taskExecutor;

    private SMSServerProperties smsServerProperties;

    @Autowired
    public SMSFetchingThread(SMSServerProperties smsServerProperties, SmsDeliveryRepository smsDeliveryRepository) {
        this.smsServerProperties = smsServerProperties;
        taskExecutor = Executors.newFixedThreadPool(smsServerProperties.getNoOfThreadsToDeliverSMS());
        this.smsDeliveryRepository = smsDeliveryRepository;
    }

    @Override
    public void run() {
        try {
            List<SmsDelivery> smsDeliveries = smsDeliveryRepository.findUnDeliveredMessages(
                    SMSDeliveryStatus.N.toString(), smsServerProperties.getNoOfSmsRetries(), smsServerProperties.getNodeName(),
                    PageRequest.of(0, smsServerProperties.getSmsRecordsLimit(), Sort.by(Sort.Direction.ASC, "smsId")));

            if (null != smsDeliveries && !smsDeliveries.isEmpty()) {
                smsDeliveries.stream().forEach(smsDelivery -> smsDeliveryRepository
                        .updateIntermediateStatus(SMSDeliveryStatus.U.toString(), smsDelivery.getSmsId()));
                List<List<SmsDelivery>> partitions = Lists.partition(smsDeliveries,
                        IntMath.divide(smsDeliveries.size(), smsServerProperties.getNoOfThreadsToDeliverSMS(), RoundingMode.UP));
                List<Callable<Object>> smsDeliveryThreads = new ArrayList<>();
                for (int i = 0; i < partitions.size(); i++) {
                    SMSDeliveryThread it = new SMSDeliveryThread(partitions.get(i), smsServerProperties, smsDeliveryRepository);
                    smsDeliveryThreads.add(Executors.callable(it));
                }

                taskExecutor.invokeAll(smsDeliveryThreads);
            }
        } catch (Exception e) {
            String responseMessage = LoggerUtil.printLogForSMS(LogConstants.OUTGOING_REQUEST_EVENT_TYPE.getValue(), LogConstants.EMPTY_STRING.getValue(), e, LogConstants.EMPTY_STRING.getValue(), LogConstants.SMS_NOTIFICATION.getValue() );
            LOGGER.info("Exception while executing sms fetching thread ... {}", responseMessage);
            Thread t = Thread.currentThread();
            t.getUncaughtExceptionHandler().uncaughtException(t, e);
        }
    }
}
