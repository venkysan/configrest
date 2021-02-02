package com.comviva.interop.txnengine.services;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comviva.interop.txnengine.configuration.BrokerServiceProperties;
import com.comviva.interop.txnengine.configuration.EigTags;
import com.comviva.interop.txnengine.configuration.Resource;
import com.comviva.interop.txnengine.configuration.ServiceTemplateNames;
import com.comviva.interop.txnengine.enums.Constants;
import com.comviva.interop.txnengine.enums.ISOFields;
import com.comviva.interop.txnengine.enums.LogConstants;
import com.comviva.interop.txnengine.enums.RequestSource;
import com.comviva.interop.txnengine.enums.SourceAccountType;
import com.comviva.interop.txnengine.enums.TransactionTypes;
import com.comviva.interop.txnengine.model.TransactionRequest;
import com.comviva.interop.txnengine.util.StringUtils;
import com.comviva.interop.txnengine.util.ThirdPartyCaller;

@Service
public class OffusHPS {

    @Autowired
    private Resource resource;
    
    @Autowired
    private ThirdPartyCaller thirdPartyCaller;
    
    @Autowired
    private ServiceTemplateNames serviceTemplateNames;

    @Autowired
    private BrokerServiceProperties thirdPartyProperties;
    
    @Autowired
    private EigTags eigTags;


    public Map<String, String> execute(TransactionRequest transactionRequest, String stan, String rrn,
            String currencyCode) {

        Map<String, String> eigRequest = new HashMap<>();
        eigRequest.put(eigTags.getInterfaceIdTag(), eigTags.getFinancialTxnInterfaceId());
        eigRequest.put(eigTags.getUserLanguageTag(),
                transactionRequest.getLang().toLowerCase() + "_" + transactionRequest.getLang().toUpperCase());
        eigRequest.put(eigTags.getRequestDateTag(),
                new SimpleDateFormat(eigTags.getEigDateFormat()).format(new Date()));

        eigRequest.put(ISOFields.PAN.getValue(), transactionRequest.getCreditParty().get(0).getValue());
        if(TransactionTypes.P2P.getTransactionType().equals(transactionRequest.getTransactionType())) {
            eigRequest.put(eigTags.getServiceTypeTag(), ISOFields.FINANCIAL_TRANSACTION.getValue());
            eigRequest.put(ISOFields.PROCESSING_CODE.getValue(), SourceAccountType.getType(transactionRequest.getSourceAccountType()).getProcessingCode());
            eigRequest.put(ISOFields.PAN.getValue(), transactionRequest.getCreditParty().get(0).getValue());
            eigRequest.put(ISOFields.PRIVATE_ADDITIONAL_DATA.getValue(),thirdPartyProperties.getPrivateAdditionalDataP2P());
            eigRequest.put(ISOFields.BUSINNES_TYPE.getValue(), thirdPartyProperties.getBusinessType());
        }
        else if(TransactionTypes.MERCHPAY.getTransactionType().equals(transactionRequest.getTransactionType())) {
            eigRequest.put(eigTags.getServiceTypeTag(), ISOFields.FINANCIAL_TRANSACTION_MP_PUSH.getValue());
            eigRequest.put(ISOFields.PROCESSING_CODE.getValue(), Constants.MP_PROCESSING_CODE.getValue());
            eigRequest.put(ISOFields.CARD_ACCEPTER_TERMINAL_ID.getValue(),thirdPartyProperties.getCardAccepterTerminalId());
            eigRequest.put(ISOFields.IDENTIFICATION_CODE_OF_CARD_ACCEPTOR.getValue(), thirdPartyProperties.getIdentificationCodeOfCardAcceptor());
            eigRequest.put(ISOFields.NAME_AND_ADDRESS_OF_CARD_ACCEPTOR.getValue(), thirdPartyProperties.getNameAndAddressOfCardAcceptor());
            eigRequest.put(ISOFields.PAN.getValue(), transactionRequest.getDebitParty().get(0).getValue());
            eigRequest.put(ISOFields.PRIVATE_ADDITIONAL_DATA.getValue(),thirdPartyProperties.getPrivateAdditionalDataMerchantPayment());
            eigRequest.put(ISOFields.BUSINNES_TYPE.getValue(), thirdPartyProperties.getBusinessTypeMerchantPayment());
        }
        else if(TransactionTypes.MERCHPAY_PULL.getTransactionType().equals(transactionRequest.getTransactionType())) {
            eigRequest.put(eigTags.getServiceTypeTag(), ISOFields.FINANCIAL_TRANSACTION_MP_PULL.getValue());
            eigRequest.put(ISOFields.PROCESSING_CODE.getValue(), Constants.MP_PULL_PROCESSING_CODE.getValue());
            eigRequest.put(ISOFields.PAN.getValue(), transactionRequest.getCreditParty().get(0).getValue());
            eigRequest.put(ISOFields.PRIVATE_ADDITIONAL_DATA.getValue(),thirdPartyProperties.getPrivateAdditionalDataMerchantPayment());
            eigRequest.put(ISOFields.BUSINNES_TYPE.getValue(), thirdPartyProperties.getBusinessTypeMerchantPayment());
        }
        
        eigRequest.put(ISOFields.SERVICE_ENTRY_MODE.getValue(), RequestSource.getValueBySource(transactionRequest.getRequestSource()));
        eigRequest.put(ISOFields.TRANSACTION_AMOUNT.getValue(),
                StringUtils.convertTxnAmountinISO(transactionRequest.getAmount()));
        eigRequest.put(ISOFields.CONSOLIDATION_AMOUNT.getValue(),
                StringUtils.convertTxnAmountinISO(transactionRequest.getAmount()));
        eigRequest.put(ISOFields.CARD_HOLDER_BILL_AMOUNT.getValue(),
                StringUtils.convertTxnAmountinISO(transactionRequest.getAmount()));
        eigRequest.put(ISOFields.DATE_AND_TIME_OF_TRANSMISSION.getValue(), new SimpleDateFormat("yyMMddHHmm").format(new Date()));
        eigRequest.put(ISOFields.CARD_HOLDER_BILLING_EXCHANGE_RATE.getValue(),
                thirdPartyProperties.getCardholderBillingEexchangeRate());
        eigRequest.put(ISOFields.SYSTEM_AUDIT_NUMBER.getValue(), stan);
        eigRequest.put(ISOFields.DATE_AND_TIME_OF_TXN.getValue(), new SimpleDateFormat("yyMMddHHmmss").format(new Date()));
        eigRequest.put(ISOFields.DATE_AND_TIME_OF_TXN.getValue(), new SimpleDateFormat("yyMMddHHmmss").format(new Date()));
        eigRequest.put(ISOFields.SETTLEMENT_DATE.getValue(), new SimpleDateFormat("yyMMdd").format(new Date()));
        eigRequest.put(ISOFields.EXCHANGE_DATE.getValue(), new SimpleDateFormat("MMdd").format(new Date()));
        eigRequest.put(ISOFields.COUNTRY_CODE_OF_THE_ACQUIRING_ORG.getValue(), currencyCode);
        eigRequest.put(ISOFields.COUNTRY_CODE_OF_THE_SENDER_ORG.getValue(), currencyCode);
        eigRequest.put(ISOFields.SERVICE_POINT_DATA_CODE.getValue(), Constants.SERVICE_POINT_DATA_CODE.getValue());
        eigRequest.put(ISOFields.FUNCTION_CODE.getValue(), Constants.FUNCTION_CODE.getValue());
        eigRequest.put(ISOFields.IDENTIFICATION_CODE_OF_THE_ACQUIRING_ORG.getValue(),
                thirdPartyProperties.getIdentificationCodeOfTheAcquiringOrganization());
        eigRequest.put(ISOFields.IDENTIFICATION_CODE_OF_THE_SENDING_ORG.getValue(),
                thirdPartyProperties.getIdentificationCodeOfTheSendingOrganization());
        eigRequest.put(ISOFields.REFERENCE_NUMBER_OF_THE_RECOVERY.getValue(), rrn);
        eigRequest.put(ISOFields.CURRENCY_CODE_OF_THE_TXN.getValue(), currencyCode);
        eigRequest.put(ISOFields.CURRENCY_CODE_OF_THE_CONSOLIDATION.getValue(), currencyCode);
        eigRequest.put(ISOFields.CURRENCY_CODE_OF_THE_CARD_HOLDER_INVOICE.getValue(), currencyCode);
        eigRequest.put(ISOFields.SECURITY_CHECK_INFORMATION.getValue(), thirdPartyProperties.getSecurityCheckInformation());
        eigRequest.put(ISOFields.SOURCE_ACCOUNT_NUMBER.getValue(), transactionRequest.getDebitParty().get(0).getValue());
        eigRequest.put(ISOFields.DESTINATION_ACCOUNT_NUMBER.getValue(),
                transactionRequest.getCreditParty().get(0).getValue());
        eigRequest.put(ISOFields.SOURCE_ACCOUNT_TYPE.getValue(),
        		SourceAccountType.getType(transactionRequest.getSourceAccountType()).getAccountTypeValue());


        return thirdPartyCaller.postRequestMapResponse(eigRequest, serviceTemplateNames.getFinancialTxnRequestTemplate(),
                resource.getEigUrl(), serviceTemplateNames.getFinancialTxnResponseTemplate(), null, LogConstants.HPS_TRANSACTION.getValue());
    }

}
