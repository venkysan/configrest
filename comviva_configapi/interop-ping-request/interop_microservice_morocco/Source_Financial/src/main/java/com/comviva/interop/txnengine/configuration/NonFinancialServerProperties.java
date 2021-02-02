package com.comviva.interop.txnengine.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Getter;

@Configuration
@PropertySource("classpath:non_financial_server.properties")
@Getter
public class NonFinancialServerProperties {

    @Value("${nonfin.getuser.defaultWalletStatus.tag:#{null}}")
    private String defaultWalletStatusTag;

    @Value("${mapped.code.tag}")
    private String mappedCodeTag;

    @Value("${code.tag}")
    private String codeTag;
}
