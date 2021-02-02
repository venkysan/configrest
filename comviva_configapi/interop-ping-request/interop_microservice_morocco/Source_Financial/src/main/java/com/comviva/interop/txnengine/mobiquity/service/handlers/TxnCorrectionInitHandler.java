package com.comviva.interop.txnengine.mobiquity.service.handlers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comviva.interop.txnengine.configuration.BrokerServiceProperties;
import com.comviva.interop.txnengine.configuration.BrokerServiceURLProperties;
import com.comviva.interop.txnengine.configuration.Resource;
import com.comviva.interop.txnengine.configuration.ServiceTemplateNames;
import com.comviva.interop.txnengine.entities.ChannelUserDetails;
import com.comviva.interop.txnengine.entities.InteropTransactionDetails;
import com.comviva.interop.txnengine.entities.InteropTransactions;
import com.comviva.interop.txnengine.enums.Constants;
import com.comviva.interop.txnengine.enums.LogConstants;
import com.comviva.interop.txnengine.enums.RequestType;
import com.comviva.interop.txnengine.enums.ThirdPartyResponseCodes;
import com.comviva.interop.txnengine.enums.TransactionStatus;
import com.comviva.interop.txnengine.model.Response;
import com.comviva.interop.txnengine.model.TxnCorrection;
import com.comviva.interop.txnengine.repositories.InteropTransactionDetailsRepository;
import com.comviva.interop.txnengine.repositories.InteropTransactionsRepository;
import com.comviva.interop.txnengine.util.MobiquityConst;
import com.comviva.interop.txnengine.util.StringUtils;
import com.comviva.interop.txnengine.util.ThirdPartyCaller;
import com.comviva.interop.txnengine.util.TransactionDataPreparationUtil;

@Service
public class TxnCorrectionInitHandler {

    
    @Autowired
    private BrokerServiceProperties thirdPartyProperties;
    
    @Autowired
    private ThirdPartyCaller thirdPartyCaller;

    @Autowired
    private InteropTransactionDetailsRepository interopTransactionDetailsRepository;

    @Autowired
    private InteropTransactionsRepository interOpTransactionRepository;

    @Autowired
    private TxnCorrectionApprovalHandler txnCorrectionApprovalHandler;    
    
    @Autowired
    private ServiceTemplateNames serviceTemplateNames;
    
    @Autowired
    private BrokerServiceURLProperties brokerServiceURLProperties;
    
    @Autowired
    private Resource resource;
    
    public void doTxnCorrectionInitiation(InteropTransactions interopTransaction, String pin, String lang,
            String interOpRefId, String txnId, ChannelUserDetails channelUserDetails,String txnMode, String em) {
    	InteropTransactionDetails txnDetails = TransactionDataPreparationUtil
                .prepareRequestTransactionDetails(interopTransaction, RequestType.REQTRCORIN.toString(),thirdPartyProperties,Constants.ZERO.getValue(), Constants.ONE.getValue());
    	txnDetails.setTxnMode(txnMode);
        interopTransactionDetailsRepository.save(txnDetails);
        Response txnCorrectionInitResponse = execute(prepareTxnCorrectionForInit(interopTransaction, txnId,
                channelUserDetails, thirdPartyProperties.getTxnCorrectionActionType()), interOpRefId,txnMode);
        if (!Optional.ofNullable(txnCorrectionInitResponse.getMappingResponse()).isPresent()) {
        	interOpTransactionRepository.save(TransactionDataPreparationUtil.updateTransactionStatus(interopTransaction,
                    TransactionStatus.TRANSACTION_AMBIGUOUS_AT_HPS.getStatus()));
            interopTransactionDetailsRepository.save(TransactionDataPreparationUtil.prepareResponseTransactionDetails(
                    interopTransaction, RequestType.REQTRCORIN.toString(), txnCorrectionInitResponse,thirdPartyProperties,Constants.ZERO.getValue(), Constants.ONE.getValue()));
        } else if (ThirdPartyResponseCodes.SUCCESS.getMappedCode()
                .equals(txnCorrectionInitResponse.getMappingResponse().getMappingCode())) {
        	interOpTransactionRepository.save(TransactionDataPreparationUtil.updateTransactionStatus(interopTransaction,
                    TransactionStatus.TRANSACTION_CORRECTION_INITIATED.getStatus()));
            interopTransactionDetailsRepository.save(TransactionDataPreparationUtil.prepareResponseTransactionDetails(
                    interopTransaction, RequestType.TRCORINRESP.toString(), txnCorrectionInitResponse,thirdPartyProperties,Constants.ZERO.getValue(), Constants.ONE.getValue()));
            txnCorrectionApprovalHandler.doTxnCorrectionApprove(interopTransaction, pin,lang, interOpRefId, txnId,
                    channelUserDetails,txnMode, em);
        } else {
        	interOpTransactionRepository.save(TransactionDataPreparationUtil.updateTransactionStatus(interopTransaction,
                    TransactionStatus.TRANSACTION_FAIL_AT_HPS.getStatus()));
            interopTransactionDetailsRepository.save(TransactionDataPreparationUtil.prepareResponseTransactionDetails(
                    interopTransaction, RequestType.REQTRCORIN.toString(), txnCorrectionInitResponse,thirdPartyProperties,Constants.ZERO.getValue(), Constants.ONE.getValue()));
        }
    }

    public Response execute(TxnCorrection txnCorrection, String interopReferenceId,String txnMode) {
        Map<String, String> txnCorrectionInit = new HashMap<>();
        txnCorrectionInit.put(MobiquityConst.SESSION_ID.getValue(), interopReferenceId);
        txnCorrectionInit.put(MobiquityConst.TXNMODE.getValue(), txnMode);
        txnCorrectionInit.put(MobiquityConst.MSISDN.getValue(), txnCorrection.getMsisdn());
        txnCorrectionInit.put(MobiquityConst.MSISDN2.getValue(), txnCorrection.getMsisdn2());
        txnCorrectionInit.put(MobiquityConst.AMOUNT.getValue(), txnCorrection.getAmount());
        txnCorrectionInit.put(MobiquityConst.TXNID.getValue(), txnCorrection.getTxnId());
        txnCorrectionInit.put(MobiquityConst.USER_TYPE.getValue(), txnCorrection.getUserType());
        txnCorrectionInit.put(MobiquityConst.USER_TYPE2.getValue(), txnCorrection.getUserType2());
        txnCorrectionInit.put(MobiquityConst.REMARKS.getValue(), txnCorrection.getRemarks());
        txnCorrectionInit.put(MobiquityConst.SCREVERSAL.getValue(), txnCorrection.getScReversal());
        txnCorrectionInit.put(MobiquityConst.ACTION.getValue(), txnCorrection.getAction());
        txnCorrectionInit.put(MobiquityConst.USER_ID.getValue(), txnCorrection.getUserId());
        return thirdPartyCaller.postMobiquityServiceRequest(txnCorrectionInit,
                serviceTemplateNames.getTxnCorrectionInitRequestTemplate(), getWalletTxnCorrectionInitUrl(), interopReferenceId, LogConstants.TXN_CORRECTION_INIT.getValue());
    }

    private String getWalletTxnCorrectionInitUrl() {
        Object[] urlArgs = { resource.getWalletBaseUrl(), brokerServiceURLProperties.getUrlAddonIdValue(),
                brokerServiceURLProperties.getUrlCountryIdValue(), brokerServiceURLProperties.getUrlCurrencyValue() };
        return StringUtils.msgFormat(brokerServiceURLProperties.getTxnCorrectionInitUrl(), urlArgs);
    }

    private TxnCorrection prepareTxnCorrectionForInit(InteropTransactions interopTransaction, String txnId,
            ChannelUserDetails channelUserDetails, String action) {
        TxnCorrection txnCorrection = txnCorrectionApprovalHandler.prepareTxnCorrection(interopTransaction, txnId,
                channelUserDetails, action);
        txnCorrection.setRemarks(thirdPartyProperties.getTxnCorrectionRemarks());
        txnCorrection.setScReversal(thirdPartyProperties.getTxnScreversal());
        return txnCorrection;
    }
}
