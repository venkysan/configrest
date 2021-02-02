package com.comviva.interop.txnengine.services;

import java.math.BigDecimal;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import com.comviva.interop.txnengine.configuration.BrokerServiceProperties;
import com.comviva.interop.txnengine.configuration.BrokerServiceURLProperties;
import com.comviva.interop.txnengine.configuration.CurrencyLoader;
import com.comviva.interop.txnengine.configuration.EigTags;
import com.comviva.interop.txnengine.entities.ChannelUserDetails;
import com.comviva.interop.txnengine.entities.InteropTransactionDetails;
import com.comviva.interop.txnengine.entities.InteropTransactions;
import com.comviva.interop.txnengine.enums.Constants;
import com.comviva.interop.txnengine.enums.InteropResponseCodes;
import com.comviva.interop.txnengine.enums.LogConstants;
import com.comviva.interop.txnengine.enums.RequestType;
import com.comviva.interop.txnengine.enums.SMSNotificationCodes;
import com.comviva.interop.txnengine.enums.Sources;
import com.comviva.interop.txnengine.enums.ThirdPartyResponseCodes;
import com.comviva.interop.txnengine.enums.TransactionStatus;
import com.comviva.interop.txnengine.enums.TransactionTypes;
import com.comviva.interop.txnengine.enums.ValidationErrors;
import com.comviva.interop.txnengine.exception.InteropException;
import com.comviva.interop.txnengine.mobiquity.service.handlers.UserAuthenticationHandler;
import com.comviva.interop.txnengine.mobiquity.service.handlers.UserEnquiryHandler;
import com.comviva.interop.txnengine.model.Request;
import com.comviva.interop.txnengine.model.Response;
import com.comviva.interop.txnengine.model.TransactionRequest;
import com.comviva.interop.txnengine.repositories.ChannelUserDetailsRepository;
import com.comviva.interop.txnengine.repositories.InteropTransactionDetailsRepository;
import com.comviva.interop.txnengine.repositories.InteropTransactionsRepository;
import com.comviva.interop.txnengine.request.validations.GetDescriptionForCode;
import com.comviva.interop.txnengine.util.CastUtils;
import com.comviva.interop.txnengine.util.LoggerUtil;
import com.comviva.interop.txnengine.util.StringUtils;
import com.comviva.interop.txnengine.util.TransactionDataPreparationUtil;

@Service
public class OffUsTransactionHPSHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(OffUsTransactionHPSHandler.class);
    
    @Autowired
    private BrokerServiceProperties thirdPartyProperties;

    @Autowired
    private OffusHPS offusHPS;

    @Autowired
    private GetDescriptionForCode getDescriptionForCode;

    @Autowired
    private InteropTransactionDetailsRepository interopTransactionDetailsRepository;

    @Autowired
    private InteropTransactionsRepository interOpTransactionRepository;

    @Autowired
    private ChannelUserDetailsRepository channelUserDetailsRepository;

    @Autowired
    private CurrencyLoader currencyLoader;

    @PersistenceContext
    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserEnquiryHandler userEnquiryHandler;

    @Autowired
    private UserAuthenticationHandler usAuthenticationHandler;
    
    @Autowired
    private EigTags eigTags;
    
    @Autowired
    private BrokerServiceURLProperties brokerServiceURLProperties;
    
    @Async("ThirdPartyCallsAsyncPool")
    public void execute(Request request, String txnId,String txnMode, InteropTransactions interopTransaction) {
        TransactionRequest requestBody = CastUtils.toTransactionRequest(request.getRequestAttr());
        ChannelUserDetails channelUserDetails = channelUserDetailsRepository.findChannelUserDetailsByMsisdn(thirdPartyProperties.getChannelUserMsisdn());
        try {
            BigDecimal stan = (BigDecimal) entityManager.createNativeQuery("select STAN_SEQID.nextval from dual")
                    .getSingleResult();
            String retriveRecoveryNumber = StringUtils.generateRRN();
            String currencyCode = currencyLoader.getCurrencyByCode(requestBody.getCurrency());
            String stanStr = StringUtils.prepareSTAN(stan.toString());
            interopTransaction.setSystemTraceAuditNumber(stanStr);
            interopTransaction.setRetrievalReferenceNumber(retriveRecoveryNumber);
            setProcessingCode(interopTransaction, requestBody);
            saveHPSTransactionRequest(interopTransaction,retriveRecoveryNumber);
            Map<String, String> eigResponse = offusHPS.execute(requestBody, stanStr,retriveRecoveryNumber , currencyCode); 
            String thirdPartyCode = eigResponse.get(eigTags.getStatusCodeTag());
            String actionCode = eigResponse.get(eigTags.getActionCodeTag());
            interopTransaction.setAuthorizationCode(eigResponse.get(eigTags.getAuthorizationCodeTag()));
            saveHPSTransactionResponse(interopTransaction);
            if (null == thirdPartyCode || "".equals(thirdPartyCode)) {
                throw new InteropException(ValidationErrors.THIRD_PARTY_STATUS_CODE_MISSING.getStatusCode(),
                        ValidationErrors.THIRD_PARTY_STATUS_CODE_MISSING.getEntity().toString());
            }
            if (ThirdPartyResponseCodes.HPS_SUCCESS.getMappedCode()
                    .equals(getDescriptionForCode.getMappingCode(thirdPartyCode))
                    && InteropResponseCodes.HPS_TXN_SUCCESS_ACTION_CODE.getStatusCode().equals(actionCode)) {
            	interOpTransactionRepository.save(TransactionDataPreparationUtil.updateTransactionStatus(interopTransaction,
                        TransactionStatus.TRANSACTION_SUCCESS.getStatus()));
            	if(!TransactionTypes.MERCHPAY_PULL.getTransactionType().equals(requestBody.getTransactionType())) {
            		userEnquiryHandler.doUserEnquiry(interopTransaction, requestBody.getDebitPartyCredentials().getPin(), requestBody.getLang(),
            				request.getInteropReferenceId(),SMSNotificationCodes.OFF_US_SUCCESS.toString(),txnMode, requestBody.getDebitPartyCredentials().getEm());
            	}
            } else if (ThirdPartyResponseCodes.EIG_REQUEST_TIMEOUT.getMappedCode()
                    .equals(getDescriptionForCode.getMappingCode(thirdPartyCode))) { // verify with failure action codes
                interOpTransactionRepository.save(TransactionDataPreparationUtil.updateTransactionStatus(interopTransaction,
                        TransactionStatus.TRANSACTION_AMBIGUOUS_AT_HPS.getStatus()));
               
            } else if (ThirdPartyResponseCodes.EIG_GENERAL_ERROR.getMappedCode()
                    .equals(getDescriptionForCode.getMappingCode(thirdPartyCode))) {
                interOpTransactionRepository.save(TransactionDataPreparationUtil.updateTransactionStatus(interopTransaction, TransactionStatus.TRANSACTION_FAIL_AT_HPS.getStatus()));
                if(!TransactionTypes.MERCHPAY_PULL.getTransactionType().equals(requestBody.getTransactionType())) {
                	usAuthenticationHandler.doTxnCorrection(interopTransaction, requestBody.getDebitPartyCredentials().getPin() , requestBody.getLang(),
                			request.getInteropReferenceId(), txnId, channelUserDetails,txnMode, requestBody.getDebitPartyCredentials().getEm());	
                }
            }
            else {
                throw new InteropException(thirdPartyCode, Sources.HPS.toString());
            }
        } catch (InteropException | RestClientException exception) {
            interOpTransactionRepository.save(TransactionDataPreparationUtil.updateTransactionStatus(interopTransaction,
                    TransactionStatus.TRANSACTION_FAIL_AT_HPS.getStatus()));
            if(!TransactionTypes.MERCHPAY_PULL.getTransactionType().equals(requestBody.getTransactionType())) {
            	usAuthenticationHandler.doTxnCorrection(interopTransaction, requestBody.getDebitPartyCredentials().getEm() , requestBody.getLang(),
            			request.getInteropReferenceId(), txnId, channelUserDetails,txnMode, requestBody.getDebitPartyCredentials().getEm());	
            }
            String message = LoggerUtil.prepareLogDetailForCreateTransactionResponse(requestBody, brokerServiceURLProperties.getUrlCountryIdValue(), request.getInteropReferenceId(), LogConstants.INTERNAL_ERROR.getValue(), null, exception);
            LOGGER.info("InteropException/RestClientException in OffUsTransactionHPSHandler: Message: {}", message);
        } catch (Exception e) {
        	interOpTransactionRepository.save(TransactionDataPreparationUtil.updateTransactionStatus(interopTransaction,
                    TransactionStatus.TRANSACTION_FAIL_AT_HPS.getStatus()));
            String message = LoggerUtil.prepareLogDetailForCreateTransactionResponse(requestBody, brokerServiceURLProperties.getUrlCountryIdValue(), request.getInteropReferenceId(), LogConstants.INTERNAL_ERROR.getValue(), null, e);
            LOGGER.info("Exception in OffUsTransactionHPSHandler: Message: {}", message);
        }
    }

    private void saveHPSTransactionRequest(InteropTransactions interopTransaction,String retriveRecoveryNumber) {
        InteropTransactionDetails interopTransactionDetails = TransactionDataPreparationUtil
                .prepareRequestTransactionDetails(interopTransaction, RequestType.HPS_FINANCIAL_REQUEST.toString(),thirdPartyProperties, Constants.ZERO.getValue(), Constants.ONE.getValue());
        interopTransactionDetails.setThirdPartyRefId(retriveRecoveryNumber);
        interopTransactionDetailsRepository.save(interopTransactionDetails);
    }

    private void saveHPSTransactionResponse(InteropTransactions interopTransaction) {
        InteropTransactionDetails interopTransactionResponseDetails = TransactionDataPreparationUtil
                .prepareResponseTransactionDetails(interopTransaction, RequestType.HPS_FINANCIAL_RESPONSE.toString(),
                        new Response(),thirdPartyProperties, Constants.ZERO.getValue(), Constants.ONE.getValue());
        interOpTransactionRepository.save(interopTransaction);
        interopTransactionDetailsRepository.save(interopTransactionResponseDetails);
    }
    
    private void setProcessingCode(InteropTransactions interopTransaction, TransactionRequest requestBody) {
    	if(TransactionTypes.P2P.getTransactionType().equals(requestBody.getTransactionType())) {
            interopTransaction.setProcessingCode(Constants.P2P_PROCESSING_CODE.getValue());
        }
        else if(TransactionTypes.MERCHPAY.getTransactionType().equals(requestBody.getTransactionType())) {
            interopTransaction.setProcessingCode(Constants.MP_PROCESSING_CODE.getValue());
        }
        else if(TransactionTypes.MERCHPAY_PULL.getTransactionType().equals(requestBody.getTransactionType())) {
        	interopTransaction.setProcessingCode(Constants.MP_PULL_PROCESSING_CODE.getValue());
        }
    }
}
