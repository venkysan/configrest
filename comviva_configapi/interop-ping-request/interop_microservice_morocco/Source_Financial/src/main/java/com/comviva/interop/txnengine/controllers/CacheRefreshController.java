package com.comviva.interop.txnengine.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.comviva.interop.txnengine.enums.LogConstants;
import com.comviva.interop.txnengine.request.validations.GetDescriptionForCode;
import com.comviva.interop.txnengine.request.validations.RequestValidations;
import com.comviva.interop.txnengine.util.LoggerUtil;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@Controller
@RequestMapping("/v1/interop/refresh")
public class CacheRefreshController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheRefreshController.class);
    private GetDescriptionForCode getDescriptionForCode;
    private RequestValidations requestValidations;
    private static final String STATUS_CODE_REFRESH_MESSAGE = "Status Codes loaded successfully";
    private static final String LANGUAGE_CODE_REFRESH_MESSAGE = "Language Codes loaded successfully";

    @Autowired
    public CacheRefreshController(GetDescriptionForCode getDescriptionForCode, RequestValidations requestValidations) {
        this.getDescriptionForCode = getDescriptionForCode;
        this.requestValidations = requestValidations;
    }

    @ApiOperation(value = "reloadStatusCodes", notes = "", response = String.class, authorizations = {
            @Authorization(value = "Authorization") }, tags = { "CacheRefresh Actions", })
    @GetMapping(value = "/StatusCodes")
    public ResponseEntity<String> reloadStatusCodes() {
        String message = LoggerUtil.printLog(LogConstants.LOADING_STATUS_CODES_EVENT.getValue(), null, LogConstants.LOADING_STATUS_CODES_START_USE_CASE.getValue());
        LOGGER.info("Reloading Status Codes from Database...{}" ,message);
        getDescriptionForCode.updateStatusCodes();
        message = LoggerUtil.printLog(LogConstants.LOADING_STATUS_CODES_EVENT.getValue(), null, LogConstants.LOADING_STATUS_CODES_COMPLETED_USE_CASE.getValue());
        LOGGER.info("Done reloading Status Codes from Database...{}" ,message);
        return new ResponseEntity<>(STATUS_CODE_REFRESH_MESSAGE, HttpStatus.OK);
    }

    @ApiOperation(value = "reloadLanguageCodes", notes = "", response = String.class, authorizations = {
            @Authorization(value = "Authorization") }, tags = { "CacheRefresh Actions", })
    @GetMapping(value = "/LanguageCodes")
    public ResponseEntity<String> reloadLanguageCodes() {
        String message = LoggerUtil.printLog(LogConstants.LOADING_LANGUAGE_CODES_EVENT.getValue(), null, LogConstants.LOADING_LANGUAGE_CODES_START_USE_CASE.getValue());
        LOGGER.info("Reloading Language Codes from Database...{}" ,message);
        requestValidations.updateLanguageCodes();
        message = LoggerUtil.printLog(LogConstants.LOADING_LANGUAGE_CODES_EVENT.getValue(), null, LogConstants.LOADING_LANGUAGE_CODES_COMPLETED_USE_CASE.getValue());
        LOGGER.info("Done reloading Language Codes from Database...{}" ,message);
        return new ResponseEntity<>(LANGUAGE_CODE_REFRESH_MESSAGE, HttpStatus.OK);
    }

}
