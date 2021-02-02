package com.comviva.interop.txnengine.services;

import java.math.BigDecimal;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import com.comviva.interop.txnengine.configuration.EigTags;
import com.comviva.interop.txnengine.enums.InteropResponseCodes;
import com.comviva.interop.txnengine.enums.LogConstants;
import com.comviva.interop.txnengine.enums.Sources;
import com.comviva.interop.txnengine.enums.ThirdPartyResponseCodes;
import com.comviva.interop.txnengine.enums.ValidationErrors;
import com.comviva.interop.txnengine.exception.InteropException;
import com.comviva.interop.txnengine.model.NetworkMessageRequest;
import com.comviva.interop.txnengine.model.NetworkMessageResponse;
import com.comviva.interop.txnengine.request.validations.GetDescriptionForCode;
import com.comviva.interop.txnengine.util.CastUtils;
import com.comviva.interop.txnengine.util.LoggerUtil;
import com.comviva.interop.txnengine.util.StringUtils;

@Service
public class NetworkMessageHPSHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(NetworkMessageHPSHandler.class);

	@Autowired
	private NetworkMessageHPS networkMessageHPS;

	@Autowired
	private GetDescriptionForCode getDescriptionForCode;

	@PersistenceContext
	@Autowired
	private EntityManager entityManager;

	@Autowired
	private EigTags eigTags;

	public NetworkMessageResponse execute(NetworkMessageRequest networkMessageRequest, String language) {
		try {
			
			  BigDecimal stan = (BigDecimal)
			  entityManager.createNativeQuery("select STAN_SEQID.nextval from dual")
			  .getSingleResult();
			 
			String retriveRecoveryNumber = StringUtils.generateRRN();
			String stanStr = StringUtils.prepareSTAN(stan.toString());
			Map<String, String> eigResponse = networkMessageHPS.execute(networkMessageRequest, stanStr,
					retriveRecoveryNumber, language);
			String actionCode = eigResponse.get(eigTags.getActionCodeTag());
			if (null == actionCode || "".equals(actionCode)) {
				throw new InteropException(ValidationErrors.THIRD_PARTY_STATUS_CODE_MISSING.getStatusCode(),
						ValidationErrors.THIRD_PARTY_STATUS_CODE_MISSING.getEntity().toString());
			}
			if (InteropResponseCodes.HPS_TXN_NETWORK_MESSAGE_SUCCESS_ACTION_CODE.getStatusCode().equals(actionCode)) {
				String thirdPartyCode = InteropResponseCodes.SUCCESS.getStatusCode();
				return new NetworkMessageResponse(getDescriptionForCode.getMappingCode(thirdPartyCode),
						CastUtils.joinStatusCode(ThirdPartyResponseCodes.HPS_SUCCESS.getEntity().toString(),
								thirdPartyCode),
						getDescriptionForCode.getDescription(ThirdPartyResponseCodes.HPS_SUCCESS.getEntity().toString(),
								thirdPartyCode, language));
			} else {
				throw new InteropException(actionCode, Sources.HPS.toString());
			}
		} catch (InteropException | RestClientException exception) {
			String message = LoggerUtil.prepareLogDetailForNetworkMessageResponse(language, null, null,
					LogConstants.INTERNAL_ERROR.getValue(), null, exception);
			LOGGER.info("InteropException/RestClientException in NetworkMessageHPSHandler: Message: {}", message);
		} catch (Exception e) {
			String message = LoggerUtil.prepareLogDetailForNetworkMessageResponse(language, null, null,
					LogConstants.INTERNAL_ERROR.getValue(), null, e);
			LOGGER.info("Exception in NetworkMessageHPSHandler: Message: {}", message);
		}
		return null;
	}
}
