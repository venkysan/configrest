/**
 * 
 */
package com.comviva.interop.txnengine.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.comviva.interop.txnengine.configuration.BrokerServiceProperties;
import com.comviva.interop.txnengine.entities.InteropTransactionDetails;
import com.comviva.interop.txnengine.entities.InteropTransactions;
import com.comviva.interop.txnengine.enums.ServiceTypes;
import com.comviva.interop.txnengine.enums.Sources;
import com.comviva.interop.txnengine.model.AccountIdentifierBase;
import com.comviva.interop.txnengine.model.ConfirmTransactionResponse;
import com.comviva.interop.txnengine.model.ConfirmTransactionResponse.TransactionTypeEnum;
import com.comviva.interop.txnengine.model.ReceiveTransactionRequest;
import com.comviva.interop.txnengine.model.Request;
import com.comviva.interop.txnengine.model.Response;
import com.comviva.interop.txnengine.model.TransactionRequest;
import com.comviva.interop.txnengine.model.TransactionResponse;

/**
 * This class contains the methods to prepare the transaction data
 * 
 * @author hemanthk
 *
 */
public class TransactionDataPreparationUtil {

    private static final String HREF_PREFIX = "/v1/transactions/";

    private TransactionDataPreparationUtil() {
    }

    public static InteropTransactions prepareInteropTransaction(TransactionRequest transactionRequest, Request request,
             String transactionSubType, String txnStatus) {
        InteropTransactions interopTransactions = new InteropTransactions();
        interopTransactions.setAmount(new BigDecimal(transactionRequest.getAmount()));
        interopTransactions.setCreatedDate(new Date());
        interopTransactions.setCurrency(transactionRequest.getCurrency());
        interopTransactions.setExtOrgRefId(transactionRequest.getExtOrgRefId());
        interopTransactions.setInteropTxnId(request.getInteropReferenceId());
        interopTransactions.setPayeeMsisdn(transactionRequest.getCreditParty().get(0).getValue());
        
        interopTransactions.setPayerMsisdn(transactionRequest.getDebitParty().get(0).getValue());
        
        interopTransactions.setRequestSource(transactionRequest.getRequestSource());
        interopTransactions.setTransactionType(transactionRequest.getTransactionType());
        interopTransactions.setTxnStatus(txnStatus);
        interopTransactions.setUpdatedDate(new Date());
        interopTransactions.setTransactionSubType(transactionSubType);
        return interopTransactions;
    }

    public static InteropTransactionDetails prepareRequestTransactionDetails(InteropTransactions interopTransactions,
            String type,BrokerServiceProperties thirdPartyProperties,String isPayerTechnical, String isPayeeTechnical) {
        InteropTransactionDetails interopTransactionDetails = new InteropTransactionDetails();
        interopTransactionDetails.setAmount(interopTransactions.getAmount());
        interopTransactionDetails.setCreatedDate(new Date());
        interopTransactionDetails.setCurrency(interopTransactions.getCurrency());
        interopTransactionDetails.setInteropTransactions(interopTransactions);
        interopTransactionDetails.setThirdPartyPayee(interopTransactions.getPayeeMsisdn());
        interopTransactionDetails.setThirdPartyPayer(interopTransactions.getPayerMsisdn());
        interopTransactionDetails.setThirdPartyReqType(type);
        interopTransactionDetails.setThirdPartyTxnType(interopTransactions.getTransactionType());
        interopTransactionDetails.setUpdatedDate(new Date());
        interopTransactionDetails.setTxnStatus(interopTransactions.getTxnStatus());
        interopTransactionDetails.setPayeePayId(thirdPartyProperties.getPayeePayId());
        interopTransactionDetails.setPayeeProvider(thirdPartyProperties.getPayeeProviderId());
        interopTransactionDetails.setPayerProvider(thirdPartyProperties.getPayerProviderId());
        interopTransactionDetails.setPayerPayId(thirdPartyProperties.getPayerPayId());
        interopTransactionDetails.setIsPayeeTechnical(isPayeeTechnical);
        interopTransactionDetails.setIsPayerTechnical(isPayerTechnical);
        return interopTransactionDetails;
    }

    public static InteropTransactionDetails prepareResponseTransactionDetails(InteropTransactions interopTransactions,
            String responseType, Response mobiquityResponse,BrokerServiceProperties thirdPartyProperties,String isPayerTechnical, String isPayeeTechnical) {
        InteropTransactionDetails interopTransactionDetails = prepareRequestTransactionDetails(interopTransactions,
                responseType,thirdPartyProperties,isPayerTechnical,isPayeeTechnical);
        if (Optional.ofNullable(mobiquityResponse.getMappingResponse()).isPresent()) {
            interopTransactionDetails.setThirdPartyMappingCode(mobiquityResponse.getMappingResponse().getMappingCode());
            interopTransactionDetails.setThirdPartyRefId(mobiquityResponse.getWalletResponse().getTxnid());
            interopTransactionDetails.setThirdPartyReponseCode(mobiquityResponse.getWalletResponse().getTxnstatus());
            interopTransactionDetails.setThirdPartyResponseMessage(mobiquityResponse.getWalletResponse().getMessage());
            interopTransactionDetails.setTxnMode(mobiquityResponse.getWalletResponse().getTxnmode());
        }
        return interopTransactionDetails;
    }

    public static InteropTransactions updateTransactionStatus(InteropTransactions interopTransactions, String status) {
    	if(null != interopTransactions) {
    		interopTransactions.setTxnStatus(status);
            interopTransactions.setUpdatedDate(new Date());	
    	}
        return interopTransactions;
    }

    public static TransactionResponse prepareTransactionResponse(InteropTransactions interopTransactions, String code,
            String mappedCode, String message) {
        TransactionResponse transactionResponse = new TransactionResponse();
        transactionResponse.setCode(CastUtils.joinStatusCode(Sources.BROKER.toString(), code));
        transactionResponse.setMappedCode(mappedCode);
        transactionResponse.setMessage(message);
        transactionResponse.setCreditParty(prepareAccountIdentifier(interopTransactions.getPayeeMsisdn()));
        transactionResponse.setDebitParty(prepareAccountIdentifier(interopTransactions.getPayerMsisdn()));
        transactionResponse.setExtOrgRefId(interopTransactions.getExtOrgRefId());
        transactionResponse.setHref(HREF_PREFIX + interopTransactions.getInteropTxnId());
        transactionResponse.setInteropRefId(interopTransactions.getInteropTxnId());
        transactionResponse.setStatus(interopTransactions.getTxnStatus());
        transactionResponse.setTransactionSubmitTime(interopTransactions.getCreatedDate());
        transactionResponse.setTransactionType(interopTransactions.getTransactionType());
        return transactionResponse;
    }

    public static List<AccountIdentifierBase> prepareAccountIdentifier(String msisdn) {
        List<AccountIdentifierBase> accountIdentifierBases = new ArrayList<>();
        AccountIdentifierBase accountIdentifierBase = new AccountIdentifierBase();
        accountIdentifierBase.setKey(AccountIdentifierBase.KeyEnum.msisdn);
        accountIdentifierBase.setValue(msisdn);
        accountIdentifierBases.add(accountIdentifierBase);
        return accountIdentifierBases;
    }
    
    public static InteropTransactions prepareInteropReceiveTransaction(ReceiveTransactionRequest receiveTransactionRequest, Request request,
            String transactionType, String status, boolean isMPPull) {
        InteropTransactions interopTransactions = new InteropTransactions();
        interopTransactions.setInteropTxnId(request.getInteropReferenceId());
        interopTransactions.setTransactionType(transactionType);
        interopTransactions.setAmount(new BigDecimal(receiveTransactionRequest.getTransactionAmount()));
        interopTransactions.setCurrency(receiveTransactionRequest.getCurrencyCodeOfTheTransaction());
        if(isMPPull) {
        	 interopTransactions.setPayerMsisdn(receiveTransactionRequest.getDestinationAccountNumber());
             interopTransactions.setPayeeMsisdn(receiveTransactionRequest.getSourceAccountNumber());
        }
        else {
        	 interopTransactions.setPayerMsisdn(receiveTransactionRequest.getSourceAccountNumber());
             interopTransactions.setPayeeMsisdn(receiveTransactionRequest.getDestinationAccountNumber());	
        }
        interopTransactions.setTxnStatus(status);
        interopTransactions.setCreatedDate(new Date());
        interopTransactions.setTransactionSubType(ServiceTypes.RECEIVE_TRANSACTION.toString());
        interopTransactions.setProcessingCode(receiveTransactionRequest.getProcessingCode());
        interopTransactions.setSystemTraceAuditNumber(receiveTransactionRequest.getSystemAuditNumber());
        interopTransactions.setRetrievalReferenceNumber(receiveTransactionRequest.getReferenceNumberOfTheRecovery());
        return interopTransactions;
    }
    
    public static ConfirmTransactionResponse prepareConfirmTransactionResponse(InteropTransactions interopTransactions, String code,
            String mappedCode, String message) {
    	ConfirmTransactionResponse transactionResponse = new ConfirmTransactionResponse();
        transactionResponse.setMessage(message);
        transactionResponse.setCreditParty(prepareAccountIdentifier(interopTransactions.getPayeeMsisdn()));
        transactionResponse.setHref(HREF_PREFIX + interopTransactions.getInteropTxnId());
        transactionResponse.setInteropRefId(interopTransactions.getInteropTxnId());
        transactionResponse.setStatus(interopTransactions.getTxnStatus());
        transactionResponse.setTransactionSubmitTime(interopTransactions.getCreatedDate());
        transactionResponse.setTransactionType(TransactionTypeEnum.fromValue(interopTransactions.getTransactionType()));
        transactionResponse.setCode(CastUtils.joinStatusCode(Sources.BROKER.toString(), code));
        transactionResponse.setMappedCode(mappedCode);
        transactionResponse.setDebitParty(prepareAccountIdentifier(interopTransactions.getPayerMsisdn()));
        return transactionResponse;
    }
}
