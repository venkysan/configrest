package com.comviva.interop.txnengine.sms;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.comviva.interop.txnengine.configuration.SMSServerProperties;
import com.comviva.interop.txnengine.entities.SmsDelivery;
import com.comviva.interop.txnengine.enums.LogConstants;
import com.comviva.interop.txnengine.enums.SMSDeliveryStatus;
import com.comviva.interop.txnengine.exception.InteropException;
import com.comviva.interop.txnengine.repositories.SmsDeliveryRepository;
import com.comviva.interop.txnengine.util.LoggerUtil;
import com.comviva.interop.txnengine.util.SMSUtility;

@Component
public class SMSDeliveryThread implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SMSDeliveryThread.class);
    
    private List<SmsDelivery> smsDeliveries;

    private SMSServerProperties smsServerProperties;

    private SmsDeliveryRepository smsDeliveryRepository;

    public SMSDeliveryThread(List<SmsDelivery> smsDeliveries, SMSServerProperties smsServerProperties,
            SmsDeliveryRepository smsDeliveryRepository) {
        this.smsDeliveries = Collections.unmodifiableList(smsDeliveries);
        this.smsServerProperties = smsServerProperties;
        this.smsDeliveryRepository = smsDeliveryRepository;

    }

    public void run() {
        for (SmsDelivery smsDelivery : smsDeliveries) {
            if (smsServerProperties.isTestEnvironment()) {
                smsDelivery.setDeliveredOn(new Date());
                smsDelivery.setStatus(SMSDeliveryStatus.Y.toString());
            } else {
                try {
                    SMSUtility.sendSms(smsServerProperties.getSmsServerAPIKey(), smsDelivery.getMessage(),
                            smsServerProperties.getSmsFromAddress(), smsServerProperties.getCountryISDCode() + smsDelivery.getMsisdn(),
                            smsServerProperties.getSmsServerURL());
                    smsDelivery.setDeliveredOn(new Date());
                    smsDelivery.setStatus(SMSDeliveryStatus.Y.toString());
                    smsDeliveryRepository.save(smsDelivery);
                    String message = LoggerUtil.printLogForSMS(LogConstants.OUTGOING_REQUEST_EVENT_TYPE.getValue(), smsDelivery.getMsisdn(), null, smsDelivery.getMessage(), LogConstants.SMS_NOTIFICATION.getValue());
                    LOGGER.info("SMS sent successfully... {}", message);

                } catch (InteropException e) {
                    smsDelivery.setStatus(SMSDeliveryStatus.N.toString());
                    smsDelivery.setRetryCount(smsDelivery.getRetryCount() + 1);
                    String message = "Delivery failed to " + smsDelivery.getMsisdn() + " due to the exception "
                            + e.getMessage();
                    String responseMessage = LoggerUtil.printLogForSMS(LogConstants.OUTGOING_REQUEST_EVENT_TYPE.getValue(), smsDelivery.getMsisdn(), e, message, LogConstants.SMS_NOTIFICATION.getValue());
                    LOGGER.info("SMS Delivery failed ... {}", responseMessage);
                    smsDeliveryRepository.save(smsDelivery);
                    throw new InteropException("500", "INTEROP");
                }
            }

        }
    }
}
