package com.comviva.interop.txnengine.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.comviva.interop.txnengine.model.OrangeMoneyOperations;
import com.comviva.interop.txnengine.model.OrangeMoneyTechnicalAccounts;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

public interface ReportsApi {

    @ApiOperation(value = "Get orange money operations", notes = "This data describes the Tango payment operations\r\n" + 
            "that has been performed by the addon.\r\n" + 
            "This API is for technical reconciliation between addon and Tango.",
            response = OrangeMoneyOperations.class, authorizations = {
            @Authorization(value = "Authorization") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Represents the response body of Orange Money operations", response = OrangeMoneyOperations.class),
            @ApiResponse(code = 401, message = "Unauthorized") })
    @GetMapping(value = "/v1/{countryId}/om-operations", produces = {"application/json;charset=UTF-8"})
    ResponseEntity<OrangeMoneyOperations> getOrangeMoneyOperations(
            @ApiParam(value = "the country identifier according to ISO 3166-1", required = true) @PathVariable("countryId") String countryId,
            @ApiParam(value = "Timestamp from which to read reconciliation data."
                    + "This is date in RFC3339 format. Example 2002-10-02T15:00:00Z\r\n" + 
                    "\r\n" + 
                    "This parameter is taken by API client from the last operation it has received (field creationDate)", required = true) @RequestParam(value = "from") String from,
            @ApiParam(value = "Max number of operation to be returned by server.", required = true) @RequestParam(value = "limit") int limit);
    
    @ApiOperation(value = "Gets orange money technical accounts", notes = "This API is used by global reporting technical reconciliation to check that all technical account transactions have been made by the addon.",
            response = OrangeMoneyTechnicalAccounts.class, authorizations = {
            @Authorization(value = "Authorization") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Represents the response body of Orange Money technical accounts", response = OrangeMoneyTechnicalAccounts.class),
            @ApiResponse(code = 401, message = "Unauthorized") })
    @GetMapping(value = "/v1/{countryId}/technical‐wallets", produces = {"application/json;charset=UTF-8"})
    ResponseEntity<OrangeMoneyTechnicalAccounts> getTechnicalWallets(
            @ApiParam(value = "the country identifier according to ISO 3166-1", required = true) @PathVariable("countryId") String countryId);

}
