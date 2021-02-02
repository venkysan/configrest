package com.comviva.interop.txnengine.services;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comviva.interop.txnengine.configuration.BrokerServiceURLProperties;
import com.comviva.interop.txnengine.entities.InteropTransactionDetails;
import com.comviva.interop.txnengine.entities.InteropTransactions;
import com.comviva.interop.txnengine.enums.InteropResponseCodes;
import com.comviva.interop.txnengine.enums.LogConstants;
import com.comviva.interop.txnengine.enums.TransactionStatus;
import com.comviva.interop.txnengine.enums.ValidationErrors;
import com.comviva.interop.txnengine.exception.InteropException;
import com.comviva.interop.txnengine.model.AccountIdentifierBase;
import com.comviva.interop.txnengine.model.AccountIdentifierBase.KeyEnum;
import com.comviva.interop.txnengine.model.Request;
import com.comviva.interop.txnengine.model.TransactionData;
import com.comviva.interop.txnengine.model.TransactionStatusRequest;
import com.comviva.interop.txnengine.model.TransactionStatusResponse;
import com.comviva.interop.txnengine.repositories.InteropTransactionDetailsRepository;
import com.comviva.interop.txnengine.repositories.InteropTransactionsRepository;
import com.comviva.interop.txnengine.request.validations.GetDescriptionForCode;
import com.comviva.interop.txnengine.request.validations.RequestValidations;
import com.comviva.interop.txnengine.util.CastUtils;
import com.comviva.interop.txnengine.util.LoggerUtil;

@Service("TransactionStatusService")
public class TransactionStatusService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionStatusService.class);
    
    @Autowired
    private RequestValidations requestValidations;

    @Autowired
    private InteropTransactionsRepository interopTransactionsRepository;

    @Autowired
    private InteropTransactionDetailsRepository interopTransactionDetailsRepository;

    @Autowired
    private GetDescriptionForCode getDescriptionForCode;
    
    @Autowired
    private BrokerServiceURLProperties brokerServiceURLProperties;

    public TransactionStatusResponse getTransactionStatusByInteropRefId(Request request) {
        TransactionStatusResponse transactionStatusResponse = new TransactionStatusResponse();

        TransactionStatusRequest transactionStatusRequest = CastUtils
                .toTransactionStatusRequest(request.getRequestAttr());
        try {
            ValidationErrors langValidation = requestValidations.validateLanguage(transactionStatusRequest.getLang());
            if (!ValidationErrors.VALID.getStatusCode().equals(langValidation.getStatusCode())) {
                throw new InteropException(langValidation.getStatusCode(), langValidation.getEntity().toString());
            }
            InteropTransactions interopTransactions = interopTransactionsRepository
                    .findInteropTransactionsByTxnId(transactionStatusRequest.getInteropreferenceid());
            List<TransactionData> transactionDetails = new ArrayList<>();
            if (interopTransactions != null) {
                transactionStatusResponse
                        .setCode(CastUtils.joinStatusCode(InteropResponseCodes.SUCCESS.getEntity().toString(),
                                InteropResponseCodes.SUCCESS.getStatusCode()));
                transactionStatusResponse.setMappedCode(
                        getDescriptionForCode.getMappingCode(InteropResponseCodes.SUCCESS.getStatusCode()));
                transactionStatusResponse.setMessage(
                        getDescriptionForCode.getDescription(InteropResponseCodes.SUCCESS.getEntity().toString(),
                                InteropResponseCodes.SUCCESS.getStatusCode(), transactionStatusRequest.getLang()));

                List<InteropTransactionDetails> interopTransactionDetails = interopTransactionDetailsRepository
                        .findInteropTransactionDetailsByTxnId(transactionStatusRequest.getInteropreferenceid());
                if (!interopTransactionDetails.isEmpty()) {
                    for (InteropTransactionDetails interopTransactionDetail : interopTransactionDetails) {
                        TransactionData transactionData = new TransactionData();
                        List<AccountIdentifierBase> creditParty = new ArrayList<>();
                        AccountIdentifierBase accountIdentifierBaseCr = new AccountIdentifierBase();
                        accountIdentifierBaseCr.setKey(KeyEnum.msisdn);
                        accountIdentifierBaseCr.setValue(interopTransactionDetail.getThirdPartyPayee());
                        creditParty.add(accountIdentifierBaseCr);
                        List<AccountIdentifierBase> debitParty = new ArrayList<>();
                        AccountIdentifierBase accountIdentifierBaseDr = new AccountIdentifierBase();
                        accountIdentifierBaseDr.setKey(KeyEnum.msisdn);
                        accountIdentifierBaseDr.setValue(interopTransactionDetail.getThirdPartyPayer());
                        debitParty.add(accountIdentifierBaseDr);
                        transactionData.setAmount(interopTransactionDetail.getAmount().toString());
                        transactionData.setCurrency(interopTransactionDetail.getCurrency());
                        transactionData.setCreditParty(creditParty);
                        transactionData.setDebitParty(debitParty);
                        transactionData.setExtOrgRefId(interopTransactionDetail.getThirdPartyRefId());
                        transactionData.setRequestSource(interopTransactions.getRequestSource());
                        transactionData.setInteropRefId(interopTransactions.getInteropTxnId());
                        transactionData.setStatus(
                                TransactionStatus.getStatus(interopTransactions.getTxnStatus()).getDescription());
                        transactionData.setTransactionSubmitTime(interopTransactions.getUpdatedDate());
                        transactionData.setTransactionType(interopTransactions.getTransactionType());
                        transactionDetails.add(transactionData);
                    }

                }
            } else {
                transactionStatusResponse
                        .setCode(CastUtils.joinStatusCode(InteropResponseCodes.NO_RECORDS_FOUND.getEntity().toString(),
                                InteropResponseCodes.NO_RECORDS_FOUND.getStatusCode()));
                transactionStatusResponse.setMappedCode(
                        getDescriptionForCode.getMappingCode(InteropResponseCodes.NO_RECORDS_FOUND.getStatusCode()));
                transactionStatusResponse.setMessage(getDescriptionForCode.getDescription(
                        InteropResponseCodes.NO_RECORDS_FOUND.getEntity().toString(),
                        InteropResponseCodes.NO_RECORDS_FOUND.getStatusCode(), transactionStatusRequest.getLang()));

            }
            transactionStatusResponse.setData(transactionDetails);
            String message = LoggerUtil.prepareLogDetailForTxnStatusResponse(transactionStatusRequest, brokerServiceURLProperties.getUrlCountryIdValue(),
                    request.getInteropReferenceId(), LogConstants.OUTGOING_RESPONSE_EVENT_TYPE.getValue(),
                    transactionStatusResponse, null);
            LOGGER.info("get transaction status  response: {}", message);
        } catch (InteropException e) {
            transactionStatusResponse.setCode(CastUtils.joinStatusCode(e.getEntity(), e.getStatusCode()));
            transactionStatusResponse.setMappedCode(getDescriptionForCode.getMappingCode(e.getStatusCode()));
            transactionStatusResponse.setMessage(getDescriptionForCode.getDescription(e.getEntity(), e.getStatusCode(),
                    transactionStatusRequest.getLang()));
            String message = LoggerUtil.prepareLogDetailForTxnStatusResponse(transactionStatusRequest, brokerServiceURLProperties.getUrlCountryIdValue(),
                    request.getInteropReferenceId(), LogConstants.OUTGOING_RESPONSE_EVENT_TYPE.getValue(),
                    transactionStatusResponse, e);
            LOGGER.info("get transaction status service, response: {}", message);
        } catch (Exception e) {
            transactionStatusResponse
                    .setCode(CastUtils.joinStatusCode(InteropResponseCodes.INTERNAL_ERROR.getEntity().toString(),
                            InteropResponseCodes.INTERNAL_ERROR.getStatusCode()));
            transactionStatusResponse.setMappedCode(
                    getDescriptionForCode.getMappingCode(InteropResponseCodes.INTERNAL_ERROR.getStatusCode()));
            transactionStatusResponse.setMessage(
                    getDescriptionForCode.getDescription(InteropResponseCodes.INTERNAL_ERROR.getEntity().toString(),
                            InteropResponseCodes.INTERNAL_ERROR.getStatusCode(), transactionStatusRequest.getLang()));
            String message = LoggerUtil.prepareLogDetailForTxnStatusResponse(transactionStatusRequest, brokerServiceURLProperties.getUrlCountryIdValue(),
                    request.getInteropReferenceId(), LogConstants.OUTGOING_RESPONSE_EVENT_TYPE.getValue(),
                    transactionStatusResponse, e);
            LOGGER.info("get transaction status service, response: {}", message);

        }
        return transactionStatusResponse;
    }
}
