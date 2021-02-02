package com.comviva.interop.txnengine.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comviva.interop.txnengine.configuration.ServiceResources;
import com.comviva.interop.txnengine.enums.InteropResponseCodes;
import com.comviva.interop.txnengine.enums.LogConstants;
import com.comviva.interop.txnengine.enums.ValidationErrors;
import com.comviva.interop.txnengine.exception.InteropException;
import com.comviva.interop.txnengine.model.NetworkMessageRequest;
import com.comviva.interop.txnengine.model.NetworkMessageResponse;
import com.comviva.interop.txnengine.request.validations.GetDescriptionForCode;
import com.comviva.interop.txnengine.request.validations.RequestValidations;
import com.comviva.interop.txnengine.util.CastUtils;
import com.comviva.interop.txnengine.util.LoggerUtil;

@Service("NetworkMessageService")
public class NetworkMessageService {
	private static final Logger LOGGER = LoggerFactory.getLogger(NetworkMessageService.class);

	@Autowired
	private RequestValidations requestValidations;

	@Autowired
	private ServiceResources serviceResources;
	
	@Autowired
	private NetworkMessageHPSHandler networkMessageHPSHandler;

	@Autowired
	private GetDescriptionForCode getDescriptionForCode;

	public NetworkMessageResponse networkMessage(NetworkMessageRequest networkMessageRequest) {
		NetworkMessageResponse networkMessageResponse = new NetworkMessageResponse();
		try {
			validateInputs(networkMessageRequest);
			networkMessageResponse = networkMessageHPSHandler.execute(networkMessageRequest, serviceResources.getDefaultLanguage());
		} catch (InteropException e) {
			networkMessageResponse.setCode(CastUtils.joinStatusCode(e.getEntity(), e.getStatusCode()));
			networkMessageResponse.setMappedCode(getDescriptionForCode.getMappingCode(e.getStatusCode()));
			networkMessageResponse.setMessage(getDescriptionForCode.getDescription(e.getEntity(), e.getStatusCode(),
					serviceResources.getDefaultLanguage()));
			String message = LoggerUtil.prepareLogDetailForNetworkMessageResponse("", null,
					null, LogConstants.OUTGOING_RESPONSE_EVENT_TYPE.getValue(),
					networkMessageResponse, e);
			LOGGER.info("get Network Message service, response: {}", message);
		} catch (Exception e) {
			networkMessageResponse
					.setCode(CastUtils.joinStatusCode(InteropResponseCodes.INTERNAL_ERROR.getEntity().toString(),
							InteropResponseCodes.INTERNAL_ERROR.getStatusCode()));
			networkMessageResponse.setMappedCode(
					getDescriptionForCode.getMappingCode(InteropResponseCodes.INTERNAL_ERROR.getStatusCode()));
			networkMessageResponse.setMessage(getDescriptionForCode.getDescription(
					InteropResponseCodes.INTERNAL_ERROR.getEntity().toString(),
					InteropResponseCodes.INTERNAL_ERROR.getStatusCode(), serviceResources.getDefaultLanguage()));
			String message = LoggerUtil.prepareLogDetailForNetworkMessageResponse("", null,
					null, LogConstants.OUTGOING_RESPONSE_EVENT_TYPE.getValue(),
					networkMessageResponse, e);
			LOGGER.info("get transaction status service, response: {}", message);

		}
		return networkMessageResponse;
	}
	
	private void validateInputs(NetworkMessageRequest networkMessageRequest) {
		ValidationErrors validationError;
		validationError = requestValidations
				.requestNewtorkActionValidation(networkMessageRequest.getNetworkAction());
		if (!ValidationErrors.VALID.getStatusCode().equals(validationError.getStatusCode())) {
			throw new InteropException(validationError.getStatusCode(), validationError.getEntity().toString());
		}
	}
}
