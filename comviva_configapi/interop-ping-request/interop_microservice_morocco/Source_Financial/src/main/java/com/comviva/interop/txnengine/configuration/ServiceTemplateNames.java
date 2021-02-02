package com.comviva.interop.txnengine.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Getter;

@Configuration
@PropertySource("classpath:service_template_names.properties")
@Getter
public class ServiceTemplateNames {
    
    @Value("${getfee.request.template:#{null}}")
    private String getFeeRequestTemplate;

    @Value("${p2p.request.template:#{null}}")
    private String p2PRequestTemplate;

    @Value("${cashout.init.request.template:#{null}}")
    private String cashOutInitRequestTemplate;

    @Value("${cashout.confirm.request.template:#{null}}")
    private String cashOutConfirmRequestTemplate;

    @Value("${txn.correction.init.request.template:#{null}}")
    private String txnCorrectionInitRequestTemplate;

    @Value("${txn.correction.approve.request.template:#{null}}")
    private String txnCorrectionApproveRequestTemplate;

    @Value("${user.auth.request.template:#{null}}")
    private String userAuthRequestTemplate;

    @Value("${p2p.cashin.request.template:#{null}}")
    private String p2PCashInRequestTemplate;

    @Value("${check.balance.request.template:#{null}}")
    private String checkBalanceRequestTemplate;
    
    @Value("${c2c.request.template:#{null}}")
    private String c2CRequestTemplate;
    
    @Value("${merchantPayment.init.request.template:#{null}}")
    private String merchantPaymentInitRequestTemplate;
    
    @Value("${merchantPayment.confirm.request.template:#{null}}")
    private String merchantPaymentConfirmRequestTemplate;
    
    @Value("${financial.transaction.request.template}")
    private String financialTxnRequestTemplate;

    @Value("${financial.transaction.response.template}")
    private String financialTxnResponseTemplate;
    
    @Value("${c2crtm.request.template:#{null}}")
    private String c2CRtmRequestTemplate;
    
    @Value("${merchantPayment.onestep.request.template:#{null}}")
    private String merchantPaymentOneStepRequestTemplate;
    
    @Value("${network.message.request.template:#{null}}")
    private String networkMessageRequestTemplate;
    
    @Value("${network.message.response.template:#{null}}")
    private String networkMessageResponseTemplate;
}
