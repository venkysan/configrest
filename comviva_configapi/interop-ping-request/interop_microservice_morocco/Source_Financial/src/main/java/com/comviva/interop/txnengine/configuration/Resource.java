package com.comviva.interop.txnengine.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Configuration
@ConfigurationProperties
@Getter
public class Resource {
    // resource class to load properties from application.properties at app startup
    @Value("${msisdn.length:10}")
    private int msisdnLength;
    
    @Value("${eig.url:#{null}}")
    private String eigUrl;

    @Value("${pin.length:4}")
    private int pinLength;

    @Value("${input.date.format:null}")
    private String inputDateFormat;
    
    @Value("${non.financial.server.url:#{null}}")
    private String nonFinancialServerUrlValue;

    @Value("${nonfin.getuser.url:#{null}}")
    private String nonFinGetUserUrl;
    
    @Value("${wallet.base.url:#{null}}")
    private String walletBaseUrl;
    
    @Value("${nonfin.api.key.header.name:#{null}}")
    private String nonFinApiKeyName;

    @Value("${nonfin.api.key.header.value:#{null}}")
    private String nonFinApiKeyValue;
}
