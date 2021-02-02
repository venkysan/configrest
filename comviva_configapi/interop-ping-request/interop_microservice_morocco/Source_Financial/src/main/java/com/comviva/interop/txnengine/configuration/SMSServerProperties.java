package com.comviva.interop.txnengine.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Getter;

@Configuration
@PropertySource("classpath:sms_server.properties")
@Getter
public class SMSServerProperties {

    @Value("${number.of.threads.to.deliver.sms:2}")
    private int noOfThreadsToDeliverSMS;

    @Value("${sms.server.url:#{null}}")
    private String smsServerURL;

    @Value("${sms.from.address:#{null}}")
    private String smsFromAddress;

    @Value("${country.isd.code:#{null}}")
    private String countryISDCode;

    @Value("${no.of.sms.retries:3}")
    private int noOfSmsRetries;

    @Value("${is.test.environment}")
    private boolean isTestEnvironment;

    @Value("${initial.delay:0}")
    private int initialDelay;

    @Value("${period.time:1}")
    private int periodTime;

    @Value("${sms.server.api.key:#{null}}")
    private String smsServerAPIKey;

    @Value("${sms.records.limit:#{10}}")
    private int smsRecordsLimit;
    
    @Value("${node.name:#{null}}")
    private String nodeName;
}
