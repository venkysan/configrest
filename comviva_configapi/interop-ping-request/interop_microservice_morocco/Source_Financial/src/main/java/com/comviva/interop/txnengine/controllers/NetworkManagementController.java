package com.comviva.interop.txnengine.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.comviva.interop.txnengine.configuration.ServiceResources;
import com.comviva.interop.txnengine.enums.LogConstants;
import com.comviva.interop.txnengine.exception.InteropException;
import com.comviva.interop.txnengine.model.NetworkMessageRequest;
import com.comviva.interop.txnengine.model.NetworkMessageResponse;
import com.comviva.interop.txnengine.services.NetworkMessageService;
import com.comviva.interop.txnengine.util.LoggerUtil;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;

@Controller
@RequestMapping("/v1/edp")
public class NetworkManagementController {
	private static final Logger LOGGER = LoggerFactory.getLogger(NetworkManagementController.class);

	@Autowired
	private ServiceResources serviceResource;

	@Autowired
	private NetworkMessageService networkMessageService;

	@ApiOperation(value = "EDP Network Management", notes = "", response = NetworkMessageResponse.class, authorizations = {
			@Authorization(value = "Authorization") }, tags = { "EDP Network Management", })
	@PostMapping(value = "​/networkMessage", consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
	public ResponseEntity<NetworkMessageResponse> networkMessage(
			@ApiParam(value = "Fields of the NetworkMessage request", required = true) @RequestBody NetworkMessageRequest networkMessageRequest) {
		String message = LoggerUtil.prepareLogDetailForNetworkMessageRequest(networkMessageRequest, null, null,
				LogConstants.INCOMING_REQUEST_EVENT_TYPE.getValue());
		LOGGER.info("NetworkMessage request: {}", message);
		NetworkMessageResponse response = new NetworkMessageResponse();
		try {
			response = networkMessageService.networkMessage(networkMessageRequest);
			String responseMessage = LoggerUtil.prepareLogDetailForNetworkMessageResponse(
					serviceResource.getDefaultLanguage(), null, null,
					LogConstants.OUTGOING_RESPONSE_EVENT_TYPE.getValue(), response, null);
			LOGGER.info("NetworkMessage response: {}", responseMessage);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (InteropException ex) {
			String exceptionMessage = LoggerUtil.prepareLogDetailForNetworkMessageResponse(
					serviceResource.getDefaultLanguage(), null, null,
					LogConstants.OUTGOING_RESPONSE_EVENT_TYPE.getValue(), response, ex);
			LOGGER.info("InteropException  NetworkMessage response: {}", exceptionMessage);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
		catch (Exception e) {
			String exceptionMessage = LoggerUtil.prepareLogDetailForNetworkMessageResponse(
					serviceResource.getDefaultLanguage(), null, null,
					LogConstants.OUTGOING_RESPONSE_EVENT_TYPE.getValue(), response, e);
			LOGGER.info("Exception NetworkMessage response: {}", exceptionMessage);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}
}
