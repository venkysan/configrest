package com.comviva.interop.txnengine.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.comviva.interop.txnengine.configuration.BrokerServiceURLProperties;
import com.comviva.interop.txnengine.configuration.Resource;
import com.comviva.interop.txnengine.entities.InteropTransactions;
import com.comviva.interop.txnengine.enums.InteropResponseCodes;
import com.comviva.interop.txnengine.enums.LogConstants;
import com.comviva.interop.txnengine.enums.TransactionStatus;
import com.comviva.interop.txnengine.enums.ValidationErrors;
import com.comviva.interop.txnengine.events.PendingTransactionsResponseEvent;
import com.comviva.interop.txnengine.exception.InteropException;
import com.comviva.interop.txnengine.model.PendingTransactionRequest;
import com.comviva.interop.txnengine.model.PendingTransactions;
import com.comviva.interop.txnengine.model.PendingTransactions.ServiceTypeEnum;
import com.comviva.interop.txnengine.model.PendingTransactions.StatusEnum;
import com.comviva.interop.txnengine.model.PendingTransactionsResponse;
import com.comviva.interop.txnengine.model.Request;
import com.comviva.interop.txnengine.request.validations.GetDescriptionForCode;
import com.comviva.interop.txnengine.request.validations.RequestValidations;
import com.comviva.interop.txnengine.util.CastUtils;
import com.comviva.interop.txnengine.util.LoggerUtil;

@Service("PendingTransactionService")
public class PendingTransactionService implements ExecutableServices {

    private static final Logger LOGGER = LoggerFactory.getLogger(PendingTransactionService.class);
    
    @Autowired
    private RequestValidations requestValidations;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private GetDescriptionForCode getDescriptionForCode;
    
    @Autowired
    private Resource resource;
    
    @Autowired
    private BrokerServiceURLProperties brokerServiceURLProperties;
    
    @PersistenceContext
    @Autowired
    private EntityManager entityManager;
    
    private static final String HREF_PREFIX = "/v1/transactions/";

    @Override
    @Async
    public void execute(Request request) {
        PendingTransactionRequest requestBody = CastUtils.toPendingTransactionRequest(request.getRequestAttr());
        try {
        	 validateRequest(requestBody);
        	 String dynamicQuery = "SELECT e FROM InteropTransactions e WHERE e.txnStatus = :txnStatus and e.payerMsisdn = :payerMsisdn order by createdDate desc";
             TypedQuery<InteropTransactions> typedQuery = entityManager.createQuery(dynamicQuery, InteropTransactions.class);
             typedQuery.setParameter("txnStatus", TransactionStatus.TRANSACTION_INITIATED.getStatus());
             typedQuery.setParameter("payerMsisdn", requestBody.getMsisdn());
             if(null != requestBody.getNumberOfTransactions()) {
            	 typedQuery.setMaxResults(Integer.parseInt(requestBody.getNumberOfTransactions()));	 
             }
             List<InteropTransactions> transactions = typedQuery.getResultList();
             PendingTransactionsResponse pendingTransactionsResponse = new PendingTransactionsResponse();
             List<PendingTransactions> pendingTransactionsList = new ArrayList<>(); 
             if(null != transactions && !transactions.isEmpty()) {
            	 for(InteropTransactions interopTransactions: transactions) {
            		 PendingTransactions pendingTransactions = new PendingTransactions();
            		 pendingTransactions.setFrom(interopTransactions.getPayeeMsisdn());
            		 pendingTransactions.setHref(HREF_PREFIX+interopTransactions.getInteropTxnId());
            		 pendingTransactions.setInteropRefId(interopTransactions.getInteropTxnId());
            		 pendingTransactions.setMsisdn(interopTransactions.getPayerMsisdn());
            		 pendingTransactions.setServiceType(ServiceTypeEnum.fromValue(interopTransactions.getTransactionType()));
            		 pendingTransactions.setStatus(StatusEnum.fromValue(interopTransactions.getTxnStatus()));
            		 pendingTransactions.setTxnAmount(interopTransactions.getAmount().toString());
            		 pendingTransactions.setTxnDate(interopTransactions.getCreatedDate().toString());
            		 pendingTransactionsList.add(pendingTransactions);
            	 }
            	 pendingTransactionsResponse.setCode(CastUtils.joinStatusCode(InteropResponseCodes.SUCCESS.getEntity().toString(),InteropResponseCodes.SUCCESS.getStatusCode()));
                 pendingTransactionsResponse.setMappedCode(getDescriptionForCode.getMappingCode(InteropResponseCodes.SUCCESS.getStatusCode()));
                 pendingTransactionsResponse.setMessage(getDescriptionForCode.getDescription(InteropResponseCodes.SUCCESS.getEntity().toString(),
     					InteropResponseCodes.SUCCESS.getStatusCode(), requestBody.getLang()));
                 pendingTransactionsResponse.setData(pendingTransactionsList);
             }
             else {
            	 pendingTransactionsResponse.setCode(CastUtils.joinStatusCode(InteropResponseCodes.NO_RECORDS_FOUND.getEntity().toString(),InteropResponseCodes.NO_RECORDS_FOUND.getStatusCode()));
                 pendingTransactionsResponse.setMappedCode(getDescriptionForCode.getMappingCode(InteropResponseCodes.NO_RECORDS_FOUND.getStatusCode()));
                 pendingTransactionsResponse.setMessage(getDescriptionForCode.getDescription(InteropResponseCodes.NO_RECORDS_FOUND.getEntity().toString(),
     					InteropResponseCodes.NO_RECORDS_FOUND.getStatusCode(), requestBody.getLang()));
             }
           this.publishResponseEvent(pendingTransactionsResponse, request.getInteropReferenceId(), requestBody, null);
        } catch (InteropException e) {
            String description = getDescriptionForCode.getDescription(e.getEntity(), e.getStatusCode(),
                    requestBody.getLang());
            PendingTransactionsResponse transactionResponse = new PendingTransactionsResponse(description,
                    CastUtils.joinStatusCode(e.getEntity(), e.getStatusCode()),
                    Optional.ofNullable(e.getMappedCode()).isPresent() ? e.getMappedCode()
                            : getDescriptionForCode.getMappingCode(e.getStatusCode()));
            this.publishResponseEvent(transactionResponse, request.getInteropReferenceId(), requestBody, e);
        } catch (Exception ex) {
            String description = getDescriptionForCode.getDescription(
                    InteropResponseCodes.INTERNAL_ERROR.getEntity().toString(),
                    InteropResponseCodes.INTERNAL_ERROR.getStatusCode(), requestBody.getLang());
            PendingTransactionsResponse transactionResponse = new PendingTransactionsResponse(description,
                    CastUtils.joinStatusCode(InteropResponseCodes.INTERNAL_ERROR.getEntity().toString(),
                            InteropResponseCodes.INTERNAL_ERROR.getStatusCode()),
                    getDescriptionForCode.getMappingCode(InteropResponseCodes.INTERNAL_ERROR.getStatusCode()));
            this.publishResponseEvent(transactionResponse, request.getInteropReferenceId(), requestBody, ex);
        }
    }
    
    private void validateRequest(PendingTransactionRequest requestBody) {
    	ValidationErrors msisdnValidation = requestValidations.validateSenderMsisdn(requestBody.getMsisdn(), resource.getMsisdnLength());
   	 	if (!ValidationErrors.VALID.getStatusCode().equals(msisdnValidation.getStatusCode())) {
            throw new InteropException(msisdnValidation.getStatusCode(), msisdnValidation.getEntity().toString());
        }
   	 
   	 	ValidationErrors langValidation = requestValidations.validateLanguage(requestBody.getLang());
        if (!ValidationErrors.VALID.getStatusCode().equals(langValidation.getStatusCode())) {
            throw new InteropException(langValidation.getStatusCode(), langValidation.getEntity().toString());
        }
        
        if (null != requestBody.getNumberOfTransactions() && !requestValidations.isNumeric(requestBody.getNumberOfTransactions())) {
            throw new InteropException(ValidationErrors.NO_OF_TXNS_SHOULD_BE_NUMERIC.getStatusCode(), ValidationErrors.NO_OF_TXNS_SHOULD_BE_NUMERIC.getEntity().toString());
        }
    }

    private void publishResponseEvent(PendingTransactionsResponse transactionResponse, String reqId, PendingTransactionRequest req, Exception ex) {
        String message = LoggerUtil.prepareLogDetailForPendingTransactionsResponse(req, brokerServiceURLProperties.getUrlCountryIdValue(),
                reqId, LogConstants.OUTGOING_RESPONSE_EVENT_TYPE.getValue(),  transactionResponse, ex);
        LOGGER.info("Pending Transactions service response: {}", message);
        applicationEventPublisher.publishEvent(new PendingTransactionsResponseEvent(this, transactionResponse, reqId));
    }
}
