package com.comviva.interop.txnengine.mobiquity.service.handlers;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comviva.interop.txnengine.configuration.BrokerServiceProperties;
import com.comviva.interop.txnengine.configuration.BrokerServiceURLProperties;
import com.comviva.interop.txnengine.configuration.Resource;
import com.comviva.interop.txnengine.configuration.SMSServerProperties;
import com.comviva.interop.txnengine.configuration.ServiceTemplateNames;
import com.comviva.interop.txnengine.entities.InteropTransactionDetails;
import com.comviva.interop.txnengine.entities.InteropTransactions;
import com.comviva.interop.txnengine.entities.SmsTemplates;
import com.comviva.interop.txnengine.enums.Constants;
import com.comviva.interop.txnengine.enums.NonFinancialServiceTypes;
import com.comviva.interop.txnengine.enums.RequestType;
import com.comviva.interop.txnengine.enums.ThirdPartyResponseCodes;
import com.comviva.interop.txnengine.model.Response;
import com.comviva.interop.txnengine.repositories.InteropTransactionDetailsRepository;
import com.comviva.interop.txnengine.repositories.SmsDeliveryRepository;
import com.comviva.interop.txnengine.repositories.SmsTemplatesRepository;
import com.comviva.interop.txnengine.util.MobiquityConst;
import com.comviva.interop.txnengine.util.SMSUtility;
import com.comviva.interop.txnengine.util.StringUtils;
import com.comviva.interop.txnengine.util.ThirdPartyCaller;
import com.comviva.interop.txnengine.util.TransactionDataPreparationUtil;

@Service
public class UserEnquiryHandler {
    
    
    private BrokerServiceProperties thirdPartyProperties;
    
    private ThirdPartyCaller thirdPartyCaller;

    @Autowired
    private SmsDeliveryRepository smsDeliveryRepository;
    
    @Autowired
    private SmsTemplatesRepository smsTemplatesRepository;
    
    @Autowired
    private InteropTransactionDetailsRepository interopTransactionDetailsRepository;

    @Autowired
    private SMSServerProperties smsServerProperties;
    
    @Autowired
    private ServiceTemplateNames serviceTemplateNames;
    
    @Autowired
    private BrokerServiceURLProperties brokerServiceURLProperties;
    
    @Autowired
    private Resource resource;
    
    @Autowired
    public UserEnquiryHandler(BrokerServiceProperties thirdPartyProperties, ThirdPartyCaller thirdPartyCaller) {
        this.thirdPartyProperties = thirdPartyProperties;
        this.thirdPartyCaller = thirdPartyCaller;
    }

    public void doUserEnquiry(InteropTransactions interopTransaction, String pin, String lang,
            String interopRefId, String notificationType,String txnMode, String em) {
    	InteropTransactionDetails txnDetails = TransactionDataPreparationUtil
                .prepareRequestTransactionDetails(interopTransaction, RequestType.USRENQREQ.toString(),thirdPartyProperties,Constants.ZERO.getValue(), Constants.ONE.getValue());
    	txnDetails.setTxnMode(txnMode);
        interopTransactionDetailsRepository.save(txnDetails);
        Response userEnquiryResponse = execute(interopTransaction.getPayerMsisdn(), interopRefId, pin, txnMode, em);
        if (!Optional.ofNullable(userEnquiryResponse.getMappingResponse()).isPresent()) {
            interopTransactionDetailsRepository.save(TransactionDataPreparationUtil.prepareResponseTransactionDetails(
                    interopTransaction, RequestType.USRENQRESP.toString(), userEnquiryResponse,thirdPartyProperties,Constants.ZERO.getValue(), Constants.ONE.getValue()));
        } else if (ThirdPartyResponseCodes.SUCCESS.getMappedCode()
                .equals(userEnquiryResponse.getMappingResponse().getMappingCode())) {
            interopTransactionDetailsRepository.save(TransactionDataPreparationUtil.prepareResponseTransactionDetails(
                    interopTransaction, RequestType.USRENQRESP.toString(), userEnquiryResponse,thirdPartyProperties,Constants.ZERO.getValue(), Constants.ONE.getValue()));
            addSmsDelivery(interopTransaction.getPayerMsisdn(), notificationType, lang,
                    userEnquiryResponse.getWalletResponse().getBalance());
        } else {
            interopTransactionDetailsRepository.save(TransactionDataPreparationUtil.prepareResponseTransactionDetails(
                    interopTransaction, RequestType.USRENQRESP.toString(), userEnquiryResponse,thirdPartyProperties,Constants.ZERO.getValue(), Constants.ONE.getValue()));
        }
    }

    public Response execute(String msisdn, String interopReferenceId, String pin,String txnMode, String em) {
        Map<String, String> checkBalance = new HashMap<>();
        checkBalance.put(MobiquityConst.SESSION_ID.getValue(), interopReferenceId);
        checkBalance.put(MobiquityConst.TXNMODE.getValue(), txnMode);
        checkBalance.put(MobiquityConst.MSISDN.getValue(), msisdn);
        checkBalance.put(MobiquityConst.PROVIDER.getValue(), thirdPartyProperties.getPayerProviderId());
        checkBalance.put(MobiquityConst.PAYID.getValue(), thirdPartyProperties.getPayerPayId());
        checkBalance.put(MobiquityConst.USER_TYPE.getValue(), thirdPartyProperties.getSubscriberUserType());
        checkBalance.put(MobiquityConst.PIN.getValue(), pin);
        checkBalance.put(MobiquityConst.EM.getValue(), em);
        return thirdPartyCaller.postMobiquityServiceRequest(checkBalance,
                serviceTemplateNames.getCheckBalanceRequestTemplate(), getWalletCheckBalanceUrl(), interopReferenceId, NonFinancialServiceTypes.USER_ENQUIRY.getServiceType());
    }

    private String getWalletCheckBalanceUrl() {
        Object[] urlArgs = { resource.getWalletBaseUrl(), brokerServiceURLProperties.getUrlAddonIdValue(),
                brokerServiceURLProperties.getUrlCountryIdValue(), brokerServiceURLProperties.getUrlCurrencyValue() };
        return StringUtils.msgFormat(brokerServiceURLProperties.getCheckBalanceUrl(), urlArgs);
    }

    private void addSmsDelivery(String msisdn, String serviceType, String languageCode, String balance) {
        SmsTemplates smsTemplates = smsTemplatesRepository.findSmsTemplateByTypeAndLang(serviceType, languageCode);
        Object[] args = { balance };
        smsDeliveryRepository
                .save(SMSUtility.prepareSMSDelivery(new MessageFormat(smsTemplates.getDescription()).format(args),
                        msisdn, languageCode, serviceType, smsServerProperties.getNodeName()));
    }
}
