package com.comviva.interop.txnengine.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.async.DeferredResult;

import com.comviva.interop.txnengine.model.ConfirmTransactionRequest;
import com.comviva.interop.txnengine.model.ConfirmTransactionResponse;
import com.comviva.interop.txnengine.model.PendingTransactionsResponse;
import com.comviva.interop.txnengine.model.QuotationRequest;
import com.comviva.interop.txnengine.model.QuotationResponse;
import com.comviva.interop.txnengine.model.ReceiveTransactionRequest;
import com.comviva.interop.txnengine.model.ReceiveTransactionResponse;
import com.comviva.interop.txnengine.model.TransactionRequest;
import com.comviva.interop.txnengine.model.TransactionResponse;
import com.comviva.interop.txnengine.model.TransactionStatusResponse;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

public interface V1Api {

    public static final String APP_JSON = "application/json;charset=UTF-8";
    public static final String APP_XML = "application/xml";

    @ApiOperation(value = "Create a transaction", notes = "", response = TransactionResponse.class, authorizations = {
            @Authorization(value = "Authorization") }, tags = { "Transactions", })
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Ok", response = TransactionResponse.class),
            @ApiResponse(code = 202, message = "Accepted", response = TransactionResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = TransactionResponse.class) })
    @PostMapping(value = "/v1/transactions/", produces = { APP_JSON, APP_XML }, consumes = { APP_JSON, APP_XML })
    DeferredResult<ResponseEntity<TransactionResponse>> createTransaction(
            @ApiParam(value = "Fields of the Transaction request", required = true) @RequestBody TransactionRequest transactionRequest);

    @ApiOperation(value = "Receive the transaction", notes = "", response = ReceiveTransactionResponse.class, authorizations = {
            @Authorization(value = "Authorization") }, tags = { "Transactions", })
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Ok", response = ReceiveTransactionResponse.class),
            @ApiResponse(code = 202, message = "Accepted", response = ReceiveTransactionResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = ReceiveTransactionResponse.class) })
    @PostMapping(value = "/v1/receivetransactions/", produces = { APP_JSON, APP_XML }, consumes = { APP_JSON, APP_XML })
    DeferredResult<ResponseEntity<ReceiveTransactionResponse>> receiveTransaction(
            @ApiParam(value = "Fields of the Transaction request", required = true) @RequestBody ReceiveTransactionRequest transactionRequest);

    @ApiOperation(value = "get Transaction Status by requesting organisation transaction reference", notes = "This endpoint returns the current status of an transaction (via internal interop reference identifier)", response = TransactionStatusResponse.class, authorizations = {
            @Authorization(value = "Authorization") }, tags = { "Transactions", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Represents the response body of an transaction status", response = TransactionStatusResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = TransactionStatusResponse.class) })
    @GetMapping(value = "/v1/transactions/", produces = { APP_JSON, APP_XML })
    ResponseEntity<TransactionStatusResponse> v1TransactionsGet(
            @ApiParam(value = "Language (ISO 639-1 format, like fr, en...)", required = true) @RequestParam(value = "lang", required = true) String lang,
            @ApiParam(value = "fetch all transactions corresponding to this external ID") @RequestParam(value = "extOrgRefId", required = false) String extOrgRefId,
            @ApiParam(value = "fetch all transactions after the start date. Date-time notation as defined by RFC 3339, section 5.6, for example, 2017-07-21T17:32:28Z") @RequestParam(value = "startdate", required = false) String startdate,
            @ApiParam(value = "fetch all transactions after the start date. Date-time notation as defined by RFC 3339, section 5.6, for example, 2017-07-21T17:32:28Z") @RequestParam(value = "enddate", required = false) String enddate,
            @ApiParam(value = "The number of items to skip before starting to collect", defaultValue = "0") @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
            @ApiParam(value = "The number of items in the result", defaultValue = "10") @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit);

    @ApiOperation(value = "get Transaction Status", notes = "This endpoint returns the current status of an transaction (via internal interop reference identifier)", response = TransactionStatusResponse.class, authorizations = {
            @Authorization(value = "Authorization") }, tags = { "Transactions", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Represents the response body of an transaction status", response = TransactionStatusResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = TransactionStatusResponse.class) })
    @GetMapping(value = "/v1/transactions/{interopRefId}/{lang}", produces = { APP_JSON, APP_XML })
    ResponseEntity<TransactionStatusResponse> v1TransactionsInteropRefIdGet(
            @ApiParam(value = "Path variable to uniquely identify an transaction (via interop reference identifier)", required = true) @PathVariable("interopRefId") String interopRefId,
            @ApiParam(value = "Language (ISO 639-1 format, like fr, en...)", required = true) @PathVariable("lang") String lang);

    @ApiOperation(value = "get quotation for the transaction", notes = "This endpoint returns the quotation for the transaction", response = QuotationResponse.class, authorizations = {
            @Authorization(value = "Authorization") }, tags = { "Quotation", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Represents the response body of the quotation response", response = QuotationResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = QuotationResponse.class) })
    @PostMapping(value = "/v1/transactions/quotation", produces = { APP_JSON, APP_XML }, consumes = { APP_JSON,
            APP_XML })
    DeferredResult<ResponseEntity<QuotationResponse>> v1TransactionsQuotationPost(
            @ApiParam(value = "Fields of the Quotation request", required = true) @RequestBody QuotationRequest quotationRequest);
    
    @ApiOperation(value = "get Pending Transactions for the subscriber", notes = "This endpoint returns the pending transactions for the subscriber", response = TransactionStatusResponse.class, authorizations = {
            @Authorization(value = "Authorization") }, tags = { "Transactions", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Represents the response body of a pending transaction status", response = PendingTransactionsResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized") })
    @GetMapping(value = "/v1/transactions/pending/{msisdn}", produces = { APP_JSON })
    DeferredResult<ResponseEntity<PendingTransactionsResponse>> getPendingTransactions(
            @ApiParam(value = "Path variable to uniquely identify a pending transaction (via msisdn)", required = true) @PathVariable(value = "msisdn", required = true) String msisdn,
            @ApiParam(value = "Number of Pending Transactions showing latest first") @RequestParam(value = "numberOfTransactions", required = false) String numberOfTransactions,
            @ApiParam(value = "Language (ISO 639-1 format, like fr, en...)", required = true) @RequestParam(value = "lang", required = true) String lang);

    @ApiOperation(value = "Action on transaction confirmation", notes = "", response = ConfirmTransactionResponse.class, authorizations = {
            @Authorization(value = "Authorization") }, tags = { "Transactions", })
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Ok", response = ConfirmTransactionResponse.class),
            @ApiResponse(code = 202, message = "Accepted", response = ConfirmTransactionResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized") })
    @PostMapping(value = "/v1/transactions/action", produces = { APP_JSON }, consumes = { APP_JSON })
    DeferredResult<ResponseEntity<ConfirmTransactionResponse>> actionOnTransactionConfirmation(
            @ApiParam(value = "Fields of the Transaction request", required = true) @RequestBody ConfirmTransactionRequest confirmTransactionRequest);

}