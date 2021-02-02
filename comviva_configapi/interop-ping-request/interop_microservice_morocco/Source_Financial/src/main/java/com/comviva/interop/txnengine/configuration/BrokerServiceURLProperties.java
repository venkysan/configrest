package com.comviva.interop.txnengine.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Getter;

@Configuration
@PropertySource("classpath:broker_service_url.properties")
@Getter
public class BrokerServiceURLProperties {

    @Value("${getfee.url:#{null}}")
    private String getFeeUrl;

    @Value("${url.addonId.value:#{null}}")
    private String urlAddonIdValue;

    @Value("${url.countryId.value:#{null}}")
    private String urlCountryIdValue;

    @Value("${url.currency.value:#{null}}")
    private String urlCurrencyValue;

    @Value("${broker.success.code:#{null}}")
    private String brokerSuccessCode;

    @Value("${p2p.url:#{null}}")
    private String p2pUrl;

    @Value("${cashout.init.url:#{null}}")
    private String cashOutInitUrl;

    @Value("${cashout.confirm.url:#{null}}")
    private String cashOutConfirmUrl;

    @Value("${txn.correction.init.url:#{null}}")
    private String txnCorrectionInitUrl;

    @Value("${txn.correction.approve.url:#{null}}")
    private String txnCorrectionApproveUrl;

    @Value("${user.auth.url:#{null}}")
    private String userAuthUrl;

    @Value("${p2p.cashin.url:#{null}}")
    private String p2PCashInUrl;

    @Value("${check.balance.url:#{null}}")
    private String checkBalanceUrl;
    
    @Value("${getlang.sub.url:#{null}}")
    private String getLangSubUrl;
    
    @Value("${c2c.url:#{null}}")
    private String c2cUrl;
    
    @Value("${merchantPayment.init.url:#{null}}")
    private String merchantPaymentInitURL;
    
    @Value("${merchantPayment.confirm.url:#{null}}")
    private String merchantPaymentConfirmURL;
    
    @Value("${getlang.retailer.url:#{null}}")
    private String retailerLangURL;
    
    @Value("${c2c.rtm.url:#{null}}")
    private String c2cRtmUrl;
    
    @Value("${merchantPayment.url:#{null}}")
    private String merchantPaymentURL;
}
