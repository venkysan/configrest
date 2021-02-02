package com.comviva.interop.txnengine.mobiquity.service.handlers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import com.comviva.interop.txnengine.configuration.BrokerServiceProperties;
import com.comviva.interop.txnengine.configuration.BrokerServiceURLProperties;
import com.comviva.interop.txnengine.configuration.Resource;
import com.comviva.interop.txnengine.configuration.ServiceTemplateNames;
import com.comviva.interop.txnengine.entities.ChannelUserDetails;
import com.comviva.interop.txnengine.entities.InteropTransactions;
import com.comviva.interop.txnengine.enums.Constants;
import com.comviva.interop.txnengine.enums.LogConstants;
import com.comviva.interop.txnengine.enums.RequestType;
import com.comviva.interop.txnengine.enums.SMSNotificationCodes;
import com.comviva.interop.txnengine.enums.ThirdPartyResponseCodes;
import com.comviva.interop.txnengine.enums.TransactionStatus;
import com.comviva.interop.txnengine.model.Response;
import com.comviva.interop.txnengine.model.TxnCorrection;
import com.comviva.interop.txnengine.repositories.InteropTransactionDetailsRepository;
import com.comviva.interop.txnengine.repositories.InteropTransactionsRepository;
import com.comviva.interop.txnengine.util.LoggerUtil;
import com.comviva.interop.txnengine.util.MobiquityConst;
import com.comviva.interop.txnengine.util.StringUtils;
import com.comviva.interop.txnengine.util.ThirdPartyCaller;
import com.comviva.interop.txnengine.util.TransactionDataPreparationUtil;

@Service
public class TxnCorrectionApprovalHandler {
    
	 private static final Logger LOGGER = LoggerFactory.getLogger(TxnCorrectionApprovalHandler.class);
    
    @Autowired
    private BrokerServiceProperties thirdPartyProperties;
    
    @Autowired
    private ThirdPartyCaller thirdPartyCaller;

    @Autowired
    private InteropTransactionDetailsRepository interopTransactionDetailsRepository;

    @Autowired
    private InteropTransactionsRepository interOpTransactionRepository;

    @Autowired
    private UserEnquiryHandler userEnquiryHandler;
    
    @Autowired
    private ServiceTemplateNames serviceTemplateNames;
    
    @Autowired
    private BrokerServiceURLProperties brokerServiceURLProperties;
    
    @Autowired
    private Resource resource;
    
    public void doTxnCorrectionApprove(InteropTransactions interopTransaction, String pin, String lang,
            String interOpRefId, String txnId, ChannelUserDetails channelUserDetails,String txnMode, String em) {
    	try {
    		interopTransactionDetailsRepository.save(TransactionDataPreparationUtil
                    .prepareRequestTransactionDetails(interopTransaction, RequestType.REQTRCORCF.toString(),thirdPartyProperties,Constants.ZERO.getValue(), Constants.ONE.getValue()));
            Response txnCorrectionApproveResponse = execute(prepareTxnCorrection(interopTransaction, txnId,
                    channelUserDetails, thirdPartyProperties.getTxnCorrectionApproveActionType()), interOpRefId,txnMode);
            if (!Optional.ofNullable(txnCorrectionApproveResponse.getMappingResponse()).isPresent()) {
                interOpTransactionRepository.save(TransactionDataPreparationUtil.updateTransactionStatus(interopTransaction,
                        TransactionStatus.TRANSACTION_CORRECTION_AMBIGUOUS.getStatus()));
                interopTransactionDetailsRepository.save(TransactionDataPreparationUtil.prepareResponseTransactionDetails(
                        interopTransaction, RequestType.TRCORCFRESP.toString(), txnCorrectionApproveResponse,thirdPartyProperties,Constants.ZERO.getValue(), Constants.ONE.getValue()));
            } else if (ThirdPartyResponseCodes.SUCCESS.getMappedCode()
                    .equals(txnCorrectionApproveResponse.getMappingResponse().getMappingCode())) {
            	interopTransaction.setTxnCorrectionId(txnCorrectionApproveResponse.getWalletResponse().getTxnid());
            	 interOpTransactionRepository.save(TransactionDataPreparationUtil.updateTransactionStatus(interopTransaction,
                         TransactionStatus.TRANSACTION_FAIL.getStatus()));
                interopTransactionDetailsRepository.save(TransactionDataPreparationUtil.prepareResponseTransactionDetails(
                        interopTransaction, RequestType.TRCORCFRESP.toString(), txnCorrectionApproveResponse,thirdPartyProperties,Constants.ZERO.getValue(), Constants.ONE.getValue()));
                userEnquiryHandler.doUserEnquiry(interopTransaction, pin, lang, interOpRefId,
                        SMSNotificationCodes.OFF_US_TXN_CORRECTION_SUCCESS.toString(),txnMode,em);
            } else {
            	 interOpTransactionRepository.save(TransactionDataPreparationUtil.updateTransactionStatus(interopTransaction,
                         TransactionStatus.TRANSACTION_CORRECTION_INITIATED.getStatus()));
                interopTransactionDetailsRepository.save(TransactionDataPreparationUtil.prepareResponseTransactionDetails(
                        interopTransaction, RequestType.TRCORCFRESP.toString(), txnCorrectionApproveResponse,thirdPartyProperties,Constants.ZERO.getValue(), Constants.ONE.getValue()));
            }
    	}
    	catch(RestClientException restException) {
    		 interOpTransactionRepository.save(TransactionDataPreparationUtil.updateTransactionStatus(interopTransaction,
                     TransactionStatus.TRANSACTION_CORRECTION_INITIATED.getStatus()));
    		 String message = LoggerUtil.printLog(LogConstants.TXN_CORRECTION_APPORVE.getValue(), restException);
    		 LOGGER.info("txn correction approve timed out : {}", message);
    	}
    }

    public Response execute(TxnCorrection txnCorrection, String interopReferenceId,String txnMode) {
        Map<String, String> txnCorrectionApproval = new HashMap<>();
        txnCorrectionApproval.put(MobiquityConst.SESSION_ID.getValue(), interopReferenceId);
        txnCorrectionApproval.put(MobiquityConst.TXNMODE.getValue(), txnMode);
        txnCorrectionApproval.put(MobiquityConst.MSISDN.getValue(), txnCorrection.getMsisdn());
        txnCorrectionApproval.put(MobiquityConst.MSISDN2.getValue(), txnCorrection.getMsisdn2());
        txnCorrectionApproval.put(MobiquityConst.PROVIDER.getValue(), txnCorrection.getProvider());
        txnCorrectionApproval.put(MobiquityConst.PAYID.getValue(), txnCorrection.getPayId());
        txnCorrectionApproval.put(MobiquityConst.AMOUNT.getValue(), txnCorrection.getAmount());
        txnCorrectionApproval.put(MobiquityConst.TXNID.getValue(), txnCorrection.getTxnId());
        txnCorrectionApproval.put(MobiquityConst.USER_TYPE.getValue(), txnCorrection.getUserType());
        txnCorrectionApproval.put(MobiquityConst.USER_TYPE2.getValue(), txnCorrection.getUserType2());
        txnCorrectionApproval.put(MobiquityConst.ACTION.getValue(), txnCorrection.getAction());
        txnCorrectionApproval.put(MobiquityConst.USER_ID.getValue(), txnCorrection.getUserId());
        return thirdPartyCaller.postMobiquityServiceRequest(txnCorrectionApproval,
                serviceTemplateNames.getTxnCorrectionApproveRequestTemplate(), getWalletTxnCorrectionApproveUrl(), interopReferenceId, LogConstants.TXN_CORRECTION_APPORVE.getValue());
    }

    private String getWalletTxnCorrectionApproveUrl() {
        Object[] urlArgs = { resource.getWalletBaseUrl(), brokerServiceURLProperties.getUrlAddonIdValue(),
                brokerServiceURLProperties.getUrlCountryIdValue(), brokerServiceURLProperties.getUrlCurrencyValue() };
        return StringUtils.msgFormat(brokerServiceURLProperties.getTxnCorrectionApproveUrl(), urlArgs);
    }

    public TxnCorrection prepareTxnCorrection(InteropTransactions interopTransaction, String txnId,
            ChannelUserDetails channelUserDetails, String action) {
        TxnCorrection txnCorrection = new TxnCorrection();
        txnCorrection.setMsisdn(interopTransaction.getPayeeMsisdn());
        txnCorrection.setMsisdn2(channelUserDetails.getMsisdn());
        txnCorrection.setAmount(interopTransaction.getAmount().toString());
        txnCorrection.setTxnId(txnId);
        txnCorrection.setUserType(thirdPartyProperties.getSubscriberUserType());
        txnCorrection.setUserType2(thirdPartyProperties.getPayeeUserType());
        txnCorrection.setAction(action);
        txnCorrection.setUserId(channelUserDetails.getUserId());
        return txnCorrection;
    }
}
