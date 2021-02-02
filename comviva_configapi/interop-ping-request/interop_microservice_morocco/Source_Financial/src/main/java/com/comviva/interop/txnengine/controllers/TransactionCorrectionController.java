package com.comviva.interop.txnengine.controllers;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.comviva.interop.txnengine.configuration.BrokerServiceProperties;
import com.comviva.interop.txnengine.configuration.BrokerServiceURLProperties;
import com.comviva.interop.txnengine.configuration.ServiceResources;
import com.comviva.interop.txnengine.entities.ChannelUserDetails;
import com.comviva.interop.txnengine.entities.InteropTransactions;
import com.comviva.interop.txnengine.enums.InteropResponseCodes;
import com.comviva.interop.txnengine.enums.LogConstants;
import com.comviva.interop.txnengine.enums.UnicId;
import com.comviva.interop.txnengine.enums.ValidationErrors;
import com.comviva.interop.txnengine.exception.InteropException;
import com.comviva.interop.txnengine.mobiquity.service.handlers.UserAuthenticationHandler;
import com.comviva.interop.txnengine.model.TransactionCorrectionResponse;

import com.comviva.interop.txnengine.model.TxnMode;
import com.comviva.interop.txnengine.repositories.ChannelUserDetailsRepository;
import com.comviva.interop.txnengine.repositories.InteropTransactionsRepository;
import com.comviva.interop.txnengine.request.validations.GetDescriptionForCode;
import com.comviva.interop.txnengine.util.CastUtils;
import com.comviva.interop.txnengine.util.LoggerUtil;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;

@Controller
@RequestMapping("/v1/transactions")
public class TransactionCorrectionController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TransactionCorrectionController.class);
	
	@Autowired
	private InteropTransactionsRepository interopTransactionsRepository;
	
	@Autowired
	private GetDescriptionForCode getDescriptionForCode;
	
	@Autowired
	private ServiceResources serviceResources;
	
	@Autowired
	private UserAuthenticationHandler userAuthenticationHandler;
	
	@Autowired
	private ChannelUserDetailsRepository channelUserDetailsRepository;
	
	@Autowired
	private BrokerServiceURLProperties brokerServiceURLProperties;
	
	@Autowired
	private BrokerServiceProperties borkerServiceProperties;
    
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ApiOperation(value = "Transaction Correction with Mobiquity", notes = "", response = TransactionCorrectionResponse.class, authorizations = {
	            @Authorization(value = "Authorization")}, tags={ "Transaction Correction with Mobiquity", })
	@PostMapping(value="/txn_correction" , produces = "application/json;charset=UTF-8")
	public ResponseEntity<TransactionCorrectionResponse> doTxnCorrection(@ApiParam(value = "Transaction Id", required = false) @RequestParam(value = "transactionId", required = false) String transactionId,
			@ApiParam(value = "Retrieval Reference Number", required = false) @RequestParam(value = "retrievalReferenceNumber", required = false) String retrievalReferenceNumber) {
		String message = LoggerUtil.printLogForTxnCorrectionRequest(LogConstants.INCOMING_REQUEST_EVENT_TYPE.getValue(), null, 
				LogConstants.TXN_CORRECTION.getValue(), transactionId, retrievalReferenceNumber);
	 	LOGGER.info("Transaction Correction Request {}", message);
		TransactionCorrectionResponse transactionCorrectionResponse = new TransactionCorrectionResponse();
		try {
			if(transactionId == null && retrievalReferenceNumber == null) {
				throw new InteropException(ValidationErrors.INVALID_TXN_CORRECTION_REF_ID.getStatusCode(), ValidationErrors.INVALID_TXN_CORRECTION_REF_ID.getEntity().toString(), 
						getDescriptionForCode.getMappingCode(ValidationErrors.INVALID_TXN_CORRECTION_REF_ID.getStatusCode()));
			}
			InteropTransactions interopTransaction = null;
			if(transactionId != null) {
				interopTransaction = interopTransactionsRepository.findInteropTransactionsByMobiquityTxnId(transactionId);	
			}
			else if(retrievalReferenceNumber != null) {
				interopTransaction = interopTransactionsRepository.findInteropTransactionsByRetrievalReferenceNumber(retrievalReferenceNumber);
			}
			
			if(!Optional.ofNullable(interopTransaction).isPresent()) {
				throw new InteropException(ValidationErrors.INVALID_TRANSACTION_ID.getStatusCode(), ValidationErrors.INVALID_TRANSACTION_ID.getEntity().toString(), 
						getDescriptionForCode.getMappingCode(ValidationErrors.INVALID_TRANSACTION_ID.getStatusCode()));
			}
			if(interopTransaction.getTxnCorrectionId() != null) {
				throw new InteropException(ValidationErrors.ALREADY_TRANSACTION_CORRECTED.getStatusCode(), ValidationErrors.ALREADY_TRANSACTION_CORRECTED.getEntity().toString(), 
						getDescriptionForCode.getMappingCode(ValidationErrors.ALREADY_TRANSACTION_CORRECTED.getStatusCode()));
			}
			ChannelUserDetails channelUserDetails = getChannelUserDetails();
	    	TxnMode txnMode= new TxnMode(interopTransaction.getTransactionSubType(),brokerServiceURLProperties.getUrlCountryIdValue(),brokerServiceURLProperties.getUrlCurrencyValue(),
	                 brokerServiceURLProperties.getUrlAddonIdValue(),interopTransaction.getInteropTxnId(),UnicId.ONE.getVal());
			userAuthenticationHandler.doTxnCorrection(interopTransaction, "",serviceResources.getDefaultLanguage(), interopTransaction.getInteropTxnId(), 
					transactionId, channelUserDetails, txnMode.toString(), "");
			transactionCorrectionResponse.setCode(CastUtils.joinStatusCode(InteropResponseCodes.SUCCESS.getEntity().toString(), InteropResponseCodes.SUCCESS.getStatusCode()));
			transactionCorrectionResponse.setMappedCode(getDescriptionForCode.getMappingCode(InteropResponseCodes.SUCCESS.getStatusCode()));
			transactionCorrectionResponse.setMessage(getDescriptionForCode.getDescription(InteropResponseCodes.SUCCESS.getEntity().toString(),
					InteropResponseCodes.SUCCESS.getStatusCode(), serviceResources.getDefaultLanguage()));
			transactionCorrectionResponse.setInteropTxnId(interopTransaction.getInteropTxnId());
			transactionCorrectionResponse.setTransactionId(interopTransaction.getTxnCorrectionId());
			message = LoggerUtil.prepareLogDetailForTxnCorrectionResponse(brokerServiceURLProperties.getUrlCountryIdValue(), interopTransaction.getInteropTxnId(),
					LogConstants.OUTGOING_RESPONSE_EVENT_TYPE.getValue(), transactionCorrectionResponse, null);
		 	LOGGER.info("Transaction Correction Response {}", message);
		 	return new ResponseEntity(transactionCorrectionResponse, HttpStatus.OK);
		}
		catch(InteropException e) {
			String description=getDescriptionForCode.getDescription(e.getEntity(), e.getStatusCode(), serviceResources.getDefaultLanguage());
			transactionCorrectionResponse.setCode(CastUtils.joinStatusCode(e.getEntity(), e.getStatusCode()));
			transactionCorrectionResponse.setMessage(description);
			transactionCorrectionResponse.setMappedCode(getDescriptionForCode.getMappingCode(e.getStatusCode()));
			message = LoggerUtil.prepareLogDetailForTxnCorrectionResponse(brokerServiceURLProperties.getUrlCountryIdValue(), transactionId,
					LogConstants.OUTGOING_RESPONSE_EVENT_TYPE.getValue(), transactionCorrectionResponse, e);
		 	LOGGER.info("Transaction Correction Failure Response {}", message);
			return new ResponseEntity(transactionCorrectionResponse, HttpStatus.OK);
		}
		catch(Exception e) {
			 String description = getDescriptionForCode.getDescription(
	                    InteropResponseCodes.INTERNAL_ERROR.getEntity().toString(),
	                    InteropResponseCodes.INTERNAL_ERROR.getStatusCode(), serviceResources.getDefaultLanguage());
			transactionCorrectionResponse.setCode(CastUtils.joinStatusCode(InteropResponseCodes.INTERNAL_ERROR.getEntity().toString(), InteropResponseCodes.INTERNAL_ERROR.getStatusCode()));
			transactionCorrectionResponse.setMessage(description);
			transactionCorrectionResponse.setMappedCode(getDescriptionForCode.getMappingCode(InteropResponseCodes.INTERNAL_ERROR.getStatusCode()));
			message = LoggerUtil.prepareLogDetailForTxnCorrectionResponse(brokerServiceURLProperties.getUrlCountryIdValue(), transactionId,
					LogConstants.OUTGOING_RESPONSE_EVENT_TYPE.getValue(), transactionCorrectionResponse, e);
		 	LOGGER.info("Transaction Correction Exception Response {}", message);
			return new ResponseEntity(transactionCorrectionResponse, HttpStatus.OK);
		}
	 	
	 }
	
	 private ChannelUserDetails getChannelUserDetails() {
			return channelUserDetailsRepository.findChannelUserDetailsByMsisdn(borkerServiceProperties.getChannelUserMsisdn());
	}
}
