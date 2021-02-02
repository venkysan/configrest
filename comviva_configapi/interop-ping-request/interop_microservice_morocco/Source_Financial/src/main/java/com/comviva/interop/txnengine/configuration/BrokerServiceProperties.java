package com.comviva.interop.txnengine.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Getter;

@Configuration
@PropertySource("classpath:broker_service.properties")
@Getter
public class BrokerServiceProperties {

    @Value("${channel.user.msisdn:#{null}}")
    private String channelUserMsisdn;

    @Value("${usertype.subscriber:#{null}}")
    private String subscriberUserType;

    @Value("${payerproviderid:#{null}}")
    private String payerProviderId;

    @Value("${payerpayid:#{null}}")
    private String payerPayId;

    @Value("${payeeusertype.channel.user:#{null}}")
    private String payeeUserType;

    @Value("${payeeproviderid:#{null}}")
    private String payeeProviderId;

    @Value("${payeepayid:#{null}}")
    private String payeePayId;

    @Value("${confirm.status}")
    private String confirmStatus;

    @Value("${txn.correction.action}")
    private String txnCorrectionActionType;

    @Value("${txn.screversal}")
    private String txnScreversal;

    @Value("${txn.correction.remarks}")
    private String txnCorrectionRemarks;

    @Value("${txn.correction.approve.action}")
    private String txnCorrectionApproveActionType;

    @Value("${identification.code.acquiring.organization}")
    private String identificationCodeOfTheAcquiringOrganization;

    @Value("${identification.code.sending.organization}")
    private String identificationCodeOfTheSendingOrganization;

    @Value("${cardholder.billing.exchange.rate}")
    private String cardholderBillingEexchangeRate;

    @Value("${business.type}")
    private String businessType;
    
    @Value("${business.type.merchant.payment}")
    private String businessTypeMerchantPayment;

    @Value("${authorization.username}")
    private String authorizationUserName;

    @Value("${authorization.password}")
    private String authorizationPassword;
    
    @Value("${c2c.connector}")
    private String connector;
    
    @Value("${c2c.blocksms}")
    private String blocksms;
    
    @Value("${merchant.code}")
    private String merchantCode;

    @Value("${private.additional.data.p2p}")
    private String privateAdditionalDataP2P;

    @Value("${private.additional.data.mp}")
    private String privateAdditionalDataMerchantPayment;

    @Value("${security.check.information}")
    private String securityCheckInformation;

    @Value("${card.accepter.terminal.id}")
    private String cardAccepterTerminalId;

    @Value("${identification.code.of.card.acceptor}")
    private String identificationCodeOfCardAcceptor;

    @Value("${name.and.address.of.card.acceptor}")
    private String nameAndAddressOfCardAcceptor;
}
