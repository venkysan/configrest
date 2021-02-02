package com.comviva.interop.txnengine.services;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comviva.interop.txnengine.configuration.Resource;
import com.comviva.interop.txnengine.entities.InteropTransactions;
import com.comviva.interop.txnengine.enums.InteropResponseCodes;
import com.comviva.interop.txnengine.enums.LogConstants;
import com.comviva.interop.txnengine.enums.TransactionStatus;
import com.comviva.interop.txnengine.enums.ValidationErrors;
import com.comviva.interop.txnengine.exception.InteropException;
import com.comviva.interop.txnengine.model.AccountIdentifierBase;
import com.comviva.interop.txnengine.model.AccountIdentifierBase.KeyEnum;
import com.comviva.interop.txnengine.model.TransactionData;
import com.comviva.interop.txnengine.model.TransactionStatusResponse;
import com.comviva.interop.txnengine.request.validations.GetDescriptionForCode;
import com.comviva.interop.txnengine.request.validations.RequestValidations;
import com.comviva.interop.txnengine.util.CastUtils;
import com.comviva.interop.txnengine.util.LoggerUtil;
import com.comviva.interop.txnengine.util.StringUtils;

@Service("GetTransactionsService")
public class GetListOfTransactionsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetListOfTransactionsService.class);
    
    @Autowired
    private RequestValidations requestValidations;

    @Autowired
    private GetDescriptionForCode getDescriptionForCode;

    @Autowired
    private Resource resource;

    @PersistenceContext
    @Autowired
    private EntityManager entityManager;

    public TransactionStatusResponse getTransactions(String lang, String extOrgRefId, String startDate, String endDate,
            int offset, int limit) {
        TransactionStatusResponse transactionStatusResponse = new TransactionStatusResponse();
        
        try {

            validateRequest(lang, startDate, endDate);
            String dynamicQuery = getDynamicQuery(extOrgRefId, startDate, endDate);

            TypedQuery<InteropTransactions> typedQuery = entityManager.createQuery(dynamicQuery,
                    InteropTransactions.class);

            if (!StringUtils.checkIsNullOrEmpty(extOrgRefId)) {
                typedQuery.setParameter("extOrgRefId", extOrgRefId);
            }
            if (!StringUtils.checkIsNullOrEmpty(startDate)) {
                typedQuery.setParameter("startDate",
                        StringUtils.stringToDateFormat(startDate, resource.getInputDateFormat()));
            }
            if (!StringUtils.checkIsNullOrEmpty(endDate)) {
                typedQuery.setParameter("endDate",
                        StringUtils.stringToDateFormat(endDate, resource.getInputDateFormat()));
            }

            typedQuery.setFirstResult(offset);
            typedQuery.setMaxResults(limit);
            List<InteropTransactions> interopTransactions = typedQuery.getResultList();
            List<TransactionData> transactionDatas = new ArrayList<>();
            if (interopTransactions != null && !interopTransactions.isEmpty()) {
                transactionStatusResponse
                        .setCode(CastUtils.joinStatusCode(InteropResponseCodes.SUCCESS.getEntity().toString(),
                                InteropResponseCodes.SUCCESS.getStatusCode()));
                transactionStatusResponse.setMappedCode(
                        getDescriptionForCode.getMappingCode(InteropResponseCodes.SUCCESS.getStatusCode()));
                transactionStatusResponse.setMessage(
                        getDescriptionForCode.getDescription(InteropResponseCodes.SUCCESS.getEntity().toString(),
                                InteropResponseCodes.SUCCESS.getStatusCode(), lang));

                for (InteropTransactions interopTransaction : interopTransactions) {
                    TransactionData transactionData = new TransactionData();
                    List<AccountIdentifierBase> creditParty = new ArrayList<>();
                    AccountIdentifierBase accountIdentifierBaseCr = new AccountIdentifierBase();
                    accountIdentifierBaseCr.setKey(KeyEnum.msisdn);
                    accountIdentifierBaseCr.setValue(interopTransaction.getPayeeMsisdn());
                    creditParty.add(accountIdentifierBaseCr);
                    List<AccountIdentifierBase> debitParty = new ArrayList<>();
                    AccountIdentifierBase accountIdentifierBaseDr = new AccountIdentifierBase();
                    accountIdentifierBaseDr.setKey(KeyEnum.msisdn);
                    accountIdentifierBaseDr.setValue(interopTransaction.getPayerMsisdn());
                    debitParty.add(accountIdentifierBaseDr);
                    transactionData.setAmount(interopTransaction.getAmount().toString());
                    transactionData.setCurrency(interopTransaction.getCurrency());
                    transactionData.setCreditParty(creditParty);
                    transactionData.setDebitParty(debitParty);
                    transactionData.setExtOrgRefId(interopTransaction.getExtOrgRefId());
                    transactionData.setRequestSource(interopTransaction.getRequestSource());
                    transactionData.setInteropRefId(interopTransaction.getInteropTxnId());
                    transactionData
                            .setStatus(TransactionStatus.getStatus(interopTransaction.getTxnStatus()).getDescription());
                    transactionData.setTransactionSubmitTime(interopTransaction.getUpdatedDate());
                    transactionData.setTransactionType(interopTransaction.getTransactionType());
                    transactionDatas.add(transactionData);
                }

            } else {
                transactionStatusResponse
                        .setCode(CastUtils.joinStatusCode(InteropResponseCodes.NO_RECORDS_FOUND.getEntity().toString(),
                                InteropResponseCodes.NO_RECORDS_FOUND.getStatusCode()));
                transactionStatusResponse.setMappedCode(
                        getDescriptionForCode.getMappingCode(InteropResponseCodes.NO_RECORDS_FOUND.getStatusCode()));
                transactionStatusResponse.setMessage(getDescriptionForCode.getDescription(
                        InteropResponseCodes.NO_RECORDS_FOUND.getEntity().toString(),
                        InteropResponseCodes.NO_RECORDS_FOUND.getStatusCode(), lang));

            }
            transactionStatusResponse.setData(transactionDatas);
            String message = LoggerUtil.prepareLogDetailForListOfTxnStatusResponse(LogConstants.OUTGOING_RESPONSE_EVENT_TYPE.getValue(), transactionStatusResponse, null);
            LOGGER.info("get list of transactions  response: {}", message);
        } catch (InteropException e) {
            transactionStatusResponse.setCode(CastUtils.joinStatusCode(e.getEntity(), e.getStatusCode()));
            transactionStatusResponse.setMappedCode(getDescriptionForCode.getMappingCode(e.getStatusCode()));
            transactionStatusResponse
                    .setMessage(getDescriptionForCode.getDescription(e.getEntity(), e.getStatusCode(), lang));
            String message = LoggerUtil.prepareLogDetailForListOfTxnStatusResponse(LogConstants.OUTGOING_RESPONSE_EVENT_TYPE.getValue(), transactionStatusResponse, e);
            LOGGER.info("get list of transactions service, response: {}", message);
        } catch (Exception e) {
            transactionStatusResponse
                    .setCode(CastUtils.joinStatusCode(InteropResponseCodes.INTERNAL_ERROR.getEntity().toString(),
                            InteropResponseCodes.INTERNAL_ERROR.getStatusCode()));
            transactionStatusResponse.setMappedCode(
                    getDescriptionForCode.getMappingCode(InteropResponseCodes.INTERNAL_ERROR.getStatusCode()));
            transactionStatusResponse.setMessage(
                    getDescriptionForCode.getDescription(InteropResponseCodes.INTERNAL_ERROR.getEntity().toString(),
                            InteropResponseCodes.INTERNAL_ERROR.getStatusCode(), lang));
            String message = LoggerUtil.prepareLogDetailForListOfTxnStatusResponse(LogConstants.OUTGOING_RESPONSE_EVENT_TYPE.getValue(), transactionStatusResponse, e);
            LOGGER.info("get list of transactions service, response: {}", message);
        }
        return transactionStatusResponse;
    }

    private String getDynamicQuery(String extOrgRefId, String startDate, String endDate) {
        StringBuilder dynamicQuery = new StringBuilder();
        dynamicQuery.append("SELECT t FROM InteropTransactions t  ");

        if ((!StringUtils.checkIsNullOrEmpty(extOrgRefId) || !StringUtils.checkIsNullOrEmpty(startDate)
                || !StringUtils.checkIsNullOrEmpty(endDate))) {
            dynamicQuery.append(" WHERE ");
        }
        if (!StringUtils.checkIsNullOrEmpty(extOrgRefId)) {
            dynamicQuery.append("t.extOrgRefId = :extOrgRefId ");
        }

        if (!StringUtils.checkIsNullOrEmpty(extOrgRefId) && !StringUtils.checkIsNullOrEmpty(startDate)) {
            dynamicQuery.append(" and ");
        }

        if (!StringUtils.checkIsNullOrEmpty(startDate)) {
            dynamicQuery.append(" t.createdDate >= :startDate ");
        }

        if (!StringUtils.checkIsNullOrEmpty(startDate) && !StringUtils.checkIsNullOrEmpty(endDate)) {
            dynamicQuery.append(" and ");
        }

        if (!StringUtils.checkIsNullOrEmpty(endDate)) {
            dynamicQuery.append("t.createdDate <= :endDate ");
        }

        return dynamicQuery.toString();
    }

    public void validateRequest(String lang, String startDate, String endDate) {

        ValidationErrors langValidation = requestValidations.validateLanguage(lang);
        if (!ValidationErrors.VALID.getStatusCode().equals(langValidation.getStatusCode())) {
            throw new InteropException(langValidation.getStatusCode(), langValidation.getEntity().toString());
        }
        if (startDate != null) {
            ValidationErrors dateValidation = requestValidations.isDateValid(startDate, resource.getInputDateFormat());
            if (!ValidationErrors.VALID.getStatusCode().equals(dateValidation.getStatusCode())) {
                throw new InteropException(dateValidation.getStatusCode(), dateValidation.getEntity().toString());
            }
        }
        if (endDate != null) {
            ValidationErrors dateValidation = requestValidations.isDateValid(endDate, resource.getInputDateFormat());
            if (!ValidationErrors.VALID.getStatusCode().equals(dateValidation.getStatusCode())) {
                throw new InteropException(dateValidation.getStatusCode(), dateValidation.getEntity().toString());
            }
        }

    }
}
