package com.comviva.interop.txnengine.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.async.DeferredResult;

import com.comviva.interop.txnengine.api.V1Api;
import com.comviva.interop.txnengine.configuration.BrokerServiceURLProperties;
import com.comviva.interop.txnengine.configuration.Resource;
import com.comviva.interop.txnengine.enums.LogConstants;
import com.comviva.interop.txnengine.enums.ServiceTypes;
import com.comviva.interop.txnengine.model.ConfirmTransactionRequest;
import com.comviva.interop.txnengine.model.ConfirmTransactionResponse;
import com.comviva.interop.txnengine.model.PendingTransactionRequest;
import com.comviva.interop.txnengine.model.PendingTransactionsResponse;
import com.comviva.interop.txnengine.model.QuotationRequest;
import com.comviva.interop.txnengine.model.QuotationResponse;
import com.comviva.interop.txnengine.model.ReceiveTransactionRequest;
import com.comviva.interop.txnengine.model.ReceiveTransactionResponse;
import com.comviva.interop.txnengine.model.Request;
import com.comviva.interop.txnengine.model.TransactionRequest;
import com.comviva.interop.txnengine.model.TransactionResponse;
import com.comviva.interop.txnengine.model.TransactionStatusRequest;
import com.comviva.interop.txnengine.model.TransactionStatusResponse;
import com.comviva.interop.txnengine.services.GetListOfTransactionsService;
import com.comviva.interop.txnengine.services.TransactionStatusService;
import com.comviva.interop.txnengine.strategies.ExecutableServicesMapper;
import com.comviva.interop.txnengine.util.ConfirmTransactionResponseCache;
import com.comviva.interop.txnengine.util.CreateTransactionResponseCache;
import com.comviva.interop.txnengine.util.LoggerUtil;
import com.comviva.interop.txnengine.util.PendingTransactionsResponseCache;
import com.comviva.interop.txnengine.util.ReceiveTransactionResponseCache;
import com.comviva.interop.txnengine.util.TransactionQuotationResponseCache;

import io.swagger.annotations.ApiParam;

@Controller
public class V1ApiController implements V1Api {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(V1ApiController.class);

    @Autowired
    private ExecutableServicesMapper executableServicesMapper;
    
    @Autowired
    private TransactionQuotationResponseCache transactionQuotationResponseCache;

    @Autowired
    private CreateTransactionResponseCache createTransactionResponseCache;

    @Autowired
    private TransactionStatusService transactionStatusService;

    @Autowired
    private GetListOfTransactionsService getTransactionsService;
    
    @Autowired
    private ReceiveTransactionResponseCache receiveTransactionResponseCache;
    
    @Autowired
    private BrokerServiceURLProperties brokerServiceURLProperties;
    
    @Autowired
    private Resource resource;
    
    @Autowired
    private PendingTransactionsResponseCache pendingTransactionsResponseCache;
    
    @Autowired
    private ConfirmTransactionResponseCache confirmTransactionResponseCache;
    
    @Autowired
    public V1ApiController(ExecutableServicesMapper executableServicesMapper,
            TransactionQuotationResponseCache transactionQuotationResponseCache) {
        this.executableServicesMapper = executableServicesMapper;
        this.transactionQuotationResponseCache = transactionQuotationResponseCache;
    }

    public DeferredResult<ResponseEntity<TransactionResponse>> createTransaction(
            @ApiParam(value = "Fields of the Transaction request", required = true) @RequestBody TransactionRequest transactionRequest) {
        ServiceTypes serviceType = ServiceTypes.CREATE_TRANSACTION;
        Request request = new Request(transactionRequest);
        DeferredResult<ResponseEntity<TransactionResponse>> deferredResult = createTransactionResponseCache
                .getDeferredResponseObj();
        String interOpReferenceId = executableServicesMapper.generateInteropReferenceId();
        String message = LoggerUtil.prepareLogDetailForCreateTxnRequest(transactionRequest, brokerServiceURLProperties.getUrlCountryIdValue(), interOpReferenceId,
                LogConstants.INCOMING_REQUEST_EVENT_TYPE.getValue(), ServiceTypes.CREATE_TRANSACTION.toString(), resource.getPinLength());
        LOGGER.info("create transaction service, request: {}", message);
        executableServicesMapper.executeService(request, serviceType, interOpReferenceId);
        createTransactionResponseCache.putInCache(interOpReferenceId, deferredResult);
        
        return deferredResult;
    }

  	public DeferredResult<ResponseEntity<ReceiveTransactionResponse>> receiveTransaction(
			@ApiParam(value = "Fields of the Receive Transaction request", required = true) @RequestBody ReceiveTransactionRequest receiveTransactionRequest) {
		ServiceTypes serviceType = ServiceTypes.RECEIVE_TRANSACTION;
		Request request = new Request(receiveTransactionRequest);
		DeferredResult<ResponseEntity<ReceiveTransactionResponse>> deferredResult = receiveTransactionResponseCache
				.getDeferredResponseObj();
        String interOpReferenceId = executableServicesMapper.generateInteropReferenceId();
		String message = LoggerUtil.prepareLogDetailForReceiveTxnRequest(receiveTransactionRequest, brokerServiceURLProperties.getUrlCountryIdValue(), interOpReferenceId, LogConstants.INCOMING_REQUEST_EVENT_TYPE.getValue(),
		         ServiceTypes.RECEIVE_TRANSACTION.toString());
		LOGGER.info("receive transaction service, request: {}", message);
        executableServicesMapper.executeService(request, serviceType, interOpReferenceId);
        receiveTransactionResponseCache.putInCache(interOpReferenceId, deferredResult);
			    
		return deferredResult;
	}

    public ResponseEntity<TransactionStatusResponse> v1TransactionsGet(
            @ApiParam(value = "Language (ISO 639-1 format, like fr, en...)", required = true) @RequestParam(value = "lang", required = true) String lang,
            @ApiParam(value = "fetch all transactions corresponding to this external ID") @RequestParam(value = "extOrgRefId", required = false) String extOrgRefId,
            @ApiParam(value = "fetch all transactions after the start date. Date-time notation as defined by RFC 3339, section 5.6, for example, 2017-07-21T17:32:28Z") @RequestParam(value = "startdate", required = false) String startdate,
            @ApiParam(value = "fetch all transactions after the start date. Date-time notation as defined by RFC 3339, section 5.6, for example, 2017-07-21T17:32:28Z") @RequestParam(value = "enddate", required = false) String enddate,
            @ApiParam(value = "The number of items to skip before starting to collect", defaultValue = "0") @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
            @ApiParam(value = "The number of items in the result", defaultValue = "10") @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit) {
        String message = LoggerUtil.prepareLogDetailForListOfTxnStatusRequest(brokerServiceURLProperties.getUrlCountryIdValue(),
                null, LogConstants.INCOMING_REQUEST_EVENT_TYPE.getValue(), ServiceTypes.GET_TRANSACTIONS.toString(), lang, extOrgRefId, startdate, enddate, offset, limit);
        LOGGER.info("transaction quotation service, request: {}", message);
        TransactionStatusResponse transactionStatusResponse = getTransactionsService.getTransactions(lang, extOrgRefId,
                startdate, enddate, offset, limit);
        return new ResponseEntity<>(transactionStatusResponse, HttpStatus.OK);
    }

    public ResponseEntity<TransactionStatusResponse> v1TransactionsInteropRefIdGet(
            @ApiParam(value = "Path variable to uniquely identify an transaction (via interop reference identifier)", required = true) @PathVariable("interopRefId") String interopRefId,
            @ApiParam(value = "Language (ISO 639-1 format, like fr, en...)", required = true) @PathVariable("lang") String lang) {

        TransactionStatusRequest transactionStatusRequest = new TransactionStatusRequest(interopRefId, lang);
        String message = LoggerUtil.prepareLogDetailForTxnStatusRequest(transactionStatusRequest, brokerServiceURLProperties.getUrlCountryIdValue(),
                interopRefId, LogConstants.INCOMING_REQUEST_EVENT_TYPE.getValue(), ServiceTypes.GET_TRANSACTION_STATUS.toString());
        LOGGER.info("get transaction status service, request: {}", message);
        Request request = new Request(transactionStatusRequest);
        TransactionStatusResponse transactionStatusResponse = transactionStatusService
                .getTransactionStatusByInteropRefId(request);
        return new ResponseEntity<>(transactionStatusResponse, HttpStatus.OK);
    }

    public DeferredResult<ResponseEntity<QuotationResponse>> v1TransactionsQuotationPost(
            @ApiParam(value = "Fields of the Quotation request", required = true) @RequestBody QuotationRequest quotationRequest) {
        ServiceTypes serviceType = ServiceTypes.TRANSACTION_QUOTATION;
        Request request = new Request(quotationRequest);
        DeferredResult<ResponseEntity<QuotationResponse>> deferredResult = transactionQuotationResponseCache
                .getDeferredResponseObj();
        String interOpReferenceId = executableServicesMapper.generateInteropReferenceId();
        String message = LoggerUtil.prepareLogDetailForTxnQuotationRequest(quotationRequest, brokerServiceURLProperties.getUrlCountryIdValue(), 
                interOpReferenceId, LogConstants.INCOMING_REQUEST_EVENT_TYPE.getValue(), ServiceTypes.TRANSACTION_QUOTATION.toString());
        LOGGER.info("transaction quotation service, request: {}", message);
        executableServicesMapper.executeService(request, serviceType, interOpReferenceId);
        transactionQuotationResponseCache.putInCache(interOpReferenceId, deferredResult);
        return deferredResult;
    }

	@Override
	public DeferredResult<ResponseEntity<PendingTransactionsResponse>> getPendingTransactions(String msisdn,
			String numberOfTransactions, String lang) {
		 	ServiceTypes serviceType = ServiceTypes.PENDING_TRANSACTIONS;
		 	PendingTransactionRequest pendingTransactionRequest = new PendingTransactionRequest(msisdn, numberOfTransactions, lang);
	        DeferredResult<ResponseEntity<PendingTransactionsResponse>> deferredResult = pendingTransactionsResponseCache
	                .getDeferredResponseObj();
	        String interOpReferenceId = executableServicesMapper.generateInteropReferenceId();
	        String message = LoggerUtil.prepareLogDetailForPendingTxnRequest(pendingTransactionRequest, brokerServiceURLProperties.getUrlCountryIdValue(), 
	                interOpReferenceId, LogConstants.INCOMING_REQUEST_EVENT_TYPE.getValue(), ServiceTypes.PENDING_TRANSACTIONS.toString());
	        LOGGER.info("Pending Transactions service, request: {}", message);
	        Request request = new Request(pendingTransactionRequest);
	        executableServicesMapper.executeService(request, serviceType, interOpReferenceId);
	        pendingTransactionsResponseCache.putInCache(interOpReferenceId, deferredResult);
	        return deferredResult;
	}

	@Override
	public DeferredResult<ResponseEntity<ConfirmTransactionResponse>> actionOnTransactionConfirmation(
			ConfirmTransactionRequest confirmTransactionRequest) {
		ServiceTypes serviceType = ServiceTypes.ACTION_ON_TRANSACTION_CONFIRMATION;
        DeferredResult<ResponseEntity<ConfirmTransactionResponse>> deferredResult = confirmTransactionResponseCache.getDeferredResponseObj();
        String interOpReferenceId = executableServicesMapper.generateInteropReferenceId();
        String message = LoggerUtil.prepareLogDetailForConfirmTxnRequest(confirmTransactionRequest, brokerServiceURLProperties.getUrlCountryIdValue(), 
                interOpReferenceId, LogConstants.INCOMING_REQUEST_EVENT_TYPE.getValue(), ServiceTypes.ACTION_ON_TRANSACTION_CONFIRMATION.toString());
        LOGGER.info("Confirm Transaction service, request: {}", message);
        Request request = new Request(confirmTransactionRequest);
        executableServicesMapper.executeService(request, serviceType, interOpReferenceId);
        confirmTransactionResponseCache.putInCache(interOpReferenceId, deferredResult);
        return deferredResult;
	}
}