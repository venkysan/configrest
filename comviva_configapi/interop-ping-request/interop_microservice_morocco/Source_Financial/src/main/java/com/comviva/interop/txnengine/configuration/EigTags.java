package com.comviva.interop.txnengine.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Getter;

@Configuration
@PropertySource("classpath:eigtag.properties")
@Getter
public class EigTags {
    
    @Value("${service.type.tag}")
    private String serviceTypeTag;

    @Value("${interface.id.tag}")
    private String interfaceIdTag;

    @Value("${msisdn.tag}")
    private String msisdnTag;

    @Value("${interopReferenceId.tag}")
    private String interopReferenceIdTag;

    @Value("${entity.tag}")
    private String entityTag;

    @Value("${status.code.tag}")
    private String statusCodeTag;

    @Value("${userlanguage.tag}")
    private String userLanguageTag;

    @Value("${request.date.tag}")
    private String requestDateTag;

    @Value("${eig.date.format}")
    private String eigDateFormat;
    
    @Value("${financial.transaction.interface.id}")
    private String financialTxnInterfaceId;

    @Value("${action.code.tag}")
    private String actionCodeTag;

    @Value("${authorization.code.tag}")
    private String authorizationCodeTag;
    
    @Value("${network.message.interface.id}")
    private String networkMessageInterfaceId;

}
