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
import com.comviva.interop.txnengine.enums.NonFinancialServiceTypes;
import com.comviva.interop.txnengine.enums.RequestType;
import com.comviva.interop.txnengine.enums.ThirdPartyResponseCodes;
import com.comviva.interop.txnengine.enums.TransactionStatus;
import com.comviva.interop.txnengine.model.Response;
import com.comviva.interop.txnengine.repositories.InteropTransactionDetailsRepository;
import com.comviva.interop.txnengine.repositories.InteropTransactionsRepository;
import com.comviva.interop.txnengine.util.MobiquityConst;
import com.comviva.interop.txnengine.util.StringUtils;
import com.comviva.interop.txnengine.util.ThirdPartyCaller;
import com.comviva.interop.txnengine.util.TransactionDataPreparationUtil;

@Service
public class UserAuthenticationHandler {
	
    
    @Autowired
    private BrokerServiceProperties thirdPartyProperties;
    
    @Autowired
    private ThirdPartyCaller thirdPartyCaller;

    @Autowired
    private InteropTransactionDetailsRepository interopTransactionDetailsRepository;

    @Autowired
    private InteropTransactionsRepository interOpTransactionRepository;

    @Autowired
    private TxnCorrectionInitHandler txnCorrectionInitHandler;
    
    @Autowired
    private ServiceTemplateNames serviceTemplateNames;
    
    @Autowired
    private BrokerServiceURLProperties brokerServiceURLProperties;
    
    @Autowired
    private Resource resource;

    public void doTxnCorrection(InteropTransactions interopTransaction, String pin, String lang,
            String interOpRefId, String txnId, ChannelUserDetails channelUserDetails,String txnMode, String em) {
    	InteropTransactionDetails txnDetails= TransactionDataPreparationUtil
                .prepareRequestTransactionDetails(interopTransaction, RequestType.REQUSERAUTH.toString(),thirdPartyProperties,Constants.ZERO.getValue(), Constants.ONE.getValue());
    	txnDetails.setTxnMode(txnMode);
        interopTransactionDetailsRepository.save(txnDetails);
        Response authenticationResponse = execute(thirdPartyProperties.getAuthorizationUserName(),thirdPartyProperties.getAuthorizationPassword(), interOpRefId,txnMode);
        if (!Optional.ofNullable(authenticationResponse.getMappingResponse()).isPresent()) {
            interOpTransactionRepository.save(TransactionDataPreparationUtil.updateTransactionStatus(interopTransaction,
                    TransactionStatus.TRANSACTION_AMBIGUOUS_AT_HPS.getStatus()));
            interopTransactionDetailsRepository.save(TransactionDataPreparationUtil.prepareResponseTransactionDetails(
                    interopTransaction, RequestType.RESUSERAUTH.toString(), authenticationResponse,thirdPartyProperties,Constants.ZERO.getValue(), Constants.ONE.getValue()));
        } else if (ThirdPartyResponseCodes.SUCCESS.getMappedCode()
                .equals(authenticationResponse.getMappingResponse().getMappingCode())) {
        	interOpTransactionRepository.save(TransactionDataPreparationUtil.updateTransactionStatus(interopTransaction,
                    TransactionStatus.TRANSACTION_FAIL_AT_HPS.getStatus()));
            interopTransactionDetailsRepository.save(TransactionDataPreparationUtil.prepareResponseTransactionDetails(
                    interopTransaction, RequestType.RESUSERAUTH.toString(), authenticationResponse,thirdPartyProperties,Constants.ZERO.getValue(), Constants.ONE.getValue()));
            txnCorrectionInitHandler.doTxnCorrectionInitiation(interopTransaction, pin, lang, interOpRefId, txnId,channelUserDetails,txnMode, em);
        } else {
        	interOpTransactionRepository.save(TransactionDataPreparationUtil.updateTransactionStatus(interopTransaction,
                    TransactionStatus.TRANSACTION_FAIL_AT_HPS.getStatus()));
            interopTransactionDetailsRepository.save(TransactionDataPreparationUtil.prepareResponseTransactionDetails(
                    interopTransaction, RequestType.RESUSERAUTH.toString(), authenticationResponse,thirdPartyProperties,Constants.ZERO.getValue(), Constants.ONE.getValue()));
        }
    }

    public Response execute(String loginId, String password, String interopReferenceId,String txnMode) {
        Map<String, String> userAuth = new HashMap<>();
        userAuth.put(MobiquityConst.SESSION_ID.getValue(), interopReferenceId);
        userAuth.put(MobiquityConst.TXNMODE.getValue(), txnMode);
        userAuth.put(MobiquityConst.LOGIN_ID.getValue(), loginId);
        userAuth.put(MobiquityConst.PASSWORD.getValue(), password);
        return thirdPartyCaller.postMobiquityServiceRequest(userAuth, serviceTemplateNames.getUserAuthRequestTemplate(),
                getWalletUserAuthUrl(), interopReferenceId, NonFinancialServiceTypes.USER_AUTHENTICATION.getServiceType());
    }

    private String getWalletUserAuthUrl() {
        Object[] urlArgs = { resource.getWalletBaseUrl(), brokerServiceURLProperties.getUrlAddonIdValue(),
                brokerServiceURLProperties.getUrlCountryIdValue(), brokerServiceURLProperties.getUrlCurrencyValue() };
        return StringUtils.msgFormat(brokerServiceURLProperties.getUserAuthUrl(), urlArgs);
    }    
}
