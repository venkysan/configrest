package com.comviva.interop.txnengine.services;

import java.net.SocketTimeoutException;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import com.comviva.interop.txnengine.configuration.BrokerServiceProperties;
import com.comviva.interop.txnengine.configuration.BrokerServiceURLProperties;
import com.comviva.interop.txnengine.configuration.SMSServerProperties;
import com.comviva.interop.txnengine.configuration.ServiceResources;
import com.comviva.interop.txnengine.entities.InteropTransactionDetails;
import com.comviva.interop.txnengine.entities.InteropTransactions;
import com.comviva.interop.txnengine.entities.SmsTemplates;
import com.comviva.interop.txnengine.enums.Constants;
import com.comviva.interop.txnengine.enums.InteropResponseCodes;
import com.comviva.interop.txnengine.enums.LogConstants;
import com.comviva.interop.txnengine.enums.RequestType;
import com.comviva.interop.txnengine.enums.SMSNotificationCodes;
import com.comviva.interop.txnengine.enums.Sources;
import com.comviva.interop.txnengine.enums.SuccessStatus;
import com.comviva.interop.txnengine.enums.ThirdPartyResponseCodes;
import com.comviva.interop.txnengine.enums.TransactionStatus;
import com.comviva.interop.txnengine.enums.TransactionSubTypes;
import com.comviva.interop.txnengine.enums.TransactionTypes;
import com.comviva.interop.txnengine.enums.UnicId;
import com.comviva.interop.txnengine.events.ReceiveTransactionResponseEvent;
import com.comviva.interop.txnengine.exception.InteropException;
import com.comviva.interop.txnengine.mobiquity.service.handlers.C2CHandler;
import com.comviva.interop.txnengine.mobiquity.service.handlers.P2PCashInHandler;
import com.comviva.interop.txnengine.mobiquity.service.handlers.RetailerGetLangHandler;
import com.comviva.interop.txnengine.mobiquity.service.handlers.SubscriberGetLangHandler;
import com.comviva.interop.txnengine.model.ReceiveTransactionRequest;
import com.comviva.interop.txnengine.model.ReceiveTransactionResponse;
import com.comviva.interop.txnengine.model.Request;
import com.comviva.interop.txnengine.model.RequestValidationResponse;
import com.comviva.interop.txnengine.model.Response;
import com.comviva.interop.txnengine.model.TxnMode;
import com.comviva.interop.txnengine.repositories.InteropTransactionDetailsRepository;
import com.comviva.interop.txnengine.repositories.InteropTransactionsRepository;
import com.comviva.interop.txnengine.repositories.SmsDeliveryRepository;
import com.comviva.interop.txnengine.repositories.SmsTemplatesRepository;
import com.comviva.interop.txnengine.request.validations.GetDescriptionForCode;
import com.comviva.interop.txnengine.request.validations.ValidateReceiveTransactionRequestBody;
import com.comviva.interop.txnengine.util.CastUtils;
import com.comviva.interop.txnengine.util.LoggerUtil;
import com.comviva.interop.txnengine.util.SMSUtility;
import com.comviva.interop.txnengine.util.TransactionDataPreparationUtil;

@Service("ReceiveTransactionService")
public class ReceiveTransactionService implements ExecutableServices {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReceiveTransactionService.class);

	@Autowired
	private ValidateReceiveTransactionRequestBody requestValidations;

	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;

	@Autowired
	private GetDescriptionForCode getDescriptionForCode;

	@Autowired
	private SubscriberGetLangHandler mobiquityGetLangHandler;

	@Autowired
	private P2PCashInHandler p2PCashInHandler;

	@Autowired
	private ServiceResources serviceResources;

	@Autowired
	private SMSServerProperties resource;

	@Autowired
	private BrokerServiceProperties thirdPartyProperties;

	@Autowired
	private InteropTransactionsRepository interOpTransactionRepository;

	@Autowired
	private InteropTransactionDetailsRepository interopTransactionDetailsRepository;

	@Autowired
	private SmsTemplatesRepository smsTemplatesRepository;

	@Autowired
	private SmsDeliveryRepository smsDeliveryRepository;

	@Autowired
	private C2CHandler c2CHandler;

	@Autowired
	private BrokerServiceURLProperties brokerServiceURLProperties;

	@Autowired
	private RetailerGetLangHandler retailerGetLangHandler;

	@Autowired
	public ReceiveTransactionService(ValidateReceiveTransactionRequestBody requestValidations,
			ApplicationEventPublisher applicationEventPublisher, GetDescriptionForCode getDescriptionForCode,
			SubscriberGetLangHandler mobiquityGetLangHandler) {
		super();
		this.requestValidations = requestValidations;
		this.applicationEventPublisher = applicationEventPublisher;
		this.getDescriptionForCode = getDescriptionForCode;
		this.mobiquityGetLangHandler = mobiquityGetLangHandler;
	}

	@Override
    @Async
    public void execute(Request request) {
		String authorizationCode = getAuthorizationCode();
        ReceiveTransactionRequest requestBody = CastUtils.toReceiveTransactionRequest(request.getRequestAttr());
        try {
        	validateRequest(requestBody);
        	requestProcess(requestBody,request,authorizationCode);
        } catch (InteropException e) {
        	updateTransactionStatus(request.getInteropReferenceId(), TransactionStatus.TRANSACTION_FAIL.getStatus());
            ReceiveTransactionResponse receiveTransactionResponse = getReceiveTransactionResponse(requestBody, InteropResponseCodes.HPS_TXN_FAILED_ACTION_CODE.getStatusCode(),authorizationCode);
            this.publishResponseEvent(receiveTransactionResponse, request.getInteropReferenceId(), e);
        }catch(RestClientException e) {
            if(e.getCause() instanceof SocketTimeoutException) {
            	updateTransactionStatus(request.getInteropReferenceId(), TransactionStatus.TRANSACTION_AMBIGUOUS.getStatus());
            }
            else {
            	updateTransactionStatus(request.getInteropReferenceId(), TransactionStatus.TRANSACTION_FAIL.getStatus());
            }
            ReceiveTransactionResponse receiveTransactionResponse = getReceiveTransactionResponse(requestBody, InteropResponseCodes.HPS_TXN_FAILED_ACTION_CODE.getStatusCode(),authorizationCode);
            this.publishResponseEvent(receiveTransactionResponse, request.getInteropReferenceId(), e);
        } catch (Exception ex) {
        	updateTransactionStatus(request.getInteropReferenceId(), TransactionStatus.TRANSACTION_FAIL.getStatus());
            ReceiveTransactionResponse receiveTransactionResponse = getReceiveTransactionResponse(requestBody, InteropResponseCodes.HPS_TXN_FAILED_ACTION_CODE.getStatusCode(),authorizationCode);
            this.publishResponseEvent(receiveTransactionResponse, request.getInteropReferenceId(), ex);
        }
    }
	
	private void requestProcess(ReceiveTransactionRequest requestBody,Request request,String authorizationCode) throws InterruptedException, ExecutionException {
		TransactionTypes recTxnType = getTransactionType(requestBody.getProcessingCode());
        Response getLangResponse = new Response();
        String requestType = null;
        String responseType = null;
        Boolean isMPPull = Constants.MP_PULL_PROCESSING_CODE.getValue().equals(requestBody.getProcessingCode()) ? Boolean.TRUE : Boolean.FALSE;
        InteropTransactions interopTransaction = null;
        if(TransactionTypes.MERCHPAY.equals(recTxnType) && !isMPPull) {
        	interopTransaction = interOpTransactionRepository.save(TransactionDataPreparationUtil.prepareInteropReceiveTransaction(requestBody, request,recTxnType.getTransactionType(), TransactionStatus.TRANSACTION_IN_PROGRESS.getStatus(), Boolean.FALSE));
            getLangResponse = retailerGetLangHandler.callBrokerForRGetLangService(requestBody.getSourceAccountNumber(), requestBody.getDestinationAccountNumber(), request.getInteropReferenceId());
            requestType = RequestType.RTMREQ.toString();
            responseType = RequestType.RMRESP.toString();
        }
        else if(TransactionTypes.P2P.equals(recTxnType) || isMPPull ) {
        	if(!isMPPull) {
        		interopTransaction = interOpTransactionRepository.save(TransactionDataPreparationUtil.prepareInteropReceiveTransaction(requestBody, request,recTxnType.getTransactionType(), TransactionStatus.TRANSACTION_IN_PROGRESS.getStatus(), Boolean.FALSE));
        	}
            getLangResponse =  mobiquityGetLangHandler.execute(requestBody.getSourceAccountNumber(),requestBody.getDestinationAccountNumber(),
            		request.getInteropReferenceId(), recTxnType);
            requestType = RequestType.RCIREQ.toString();
            responseType = RequestType.RCIRESP.toString();
        }
        if  ((ThirdPartyResponseCodes.SUCCESS.getMappedCode()
				.equals(getLangResponse.getMappingResponse().getMappingCode())) ||
				(!ThirdPartyResponseCodes.USER_INVALID.getMappedCode().equals(getLangResponse.getMappingResponse().getMappingCode()) &&
						brokerServiceURLProperties.getBrokerSuccessCode().equals(getLangResponse.getBrokerResponse().getBrokerCode()))) {
        	performTxn(isMPPull, requestBody, request, recTxnType, authorizationCode, requestType, responseType,  interopTransaction);
        } else {
            throw new InteropException(getLangResponse.getMappingResponse().getMappingCode(),Sources.BROKER.toString());
        }
	}

	private TransactionTypes getTransactionType(String processingCode) {
		TransactionTypes recTxnType = null;
		if (Constants.MP_PROCESSING_CODE.getValue().equals(processingCode)
				|| Constants.MP_PULL_PROCESSING_CODE.getValue().equals(processingCode)) {
			recTxnType = TransactionTypes.MERCHPAY;
		} else if (Constants.P2P_PROCESSING_CODE.getValue().equals(processingCode) || Constants.P2P_PROCESSING_CODE_ACCOUNT_TYPE1.getValue().equals(processingCode)
				|| Constants.P2P_PROCESSING_CODE_ACCOUNT_TYPE2.getValue().equals(processingCode) || Constants.P2P_PROCESSING_CODE_ACCOUNT_TYPE3.getValue().equals(processingCode)) {
			recTxnType = TransactionTypes.P2P;
		}
		return recTxnType;
	}

	private void updateTransactionStatus(String interopRefId, String status) {
		InteropTransactions interopTransaction = interOpTransactionRepository
				.findInteropTransactionsByTxnId(interopRefId);
		if (Optional.ofNullable(interopTransaction).isPresent()) {
			interOpTransactionRepository.save(TransactionDataPreparationUtil.updateTransactionStatus(interopTransaction,status));
		}
	}

	private void publishResponseEvent(ReceiveTransactionResponse receiveTransactionResponse, String reqId,
			Exception exception) {
		ReceiveTransactionResponseEvent receiveTransactionResponseEvent = new ReceiveTransactionResponseEvent(this,
				receiveTransactionResponse, reqId);
		String message = LoggerUtil.prepareLogDetailForReceiveTxnResponse(
				brokerServiceURLProperties.getUrlCountryIdValue(), reqId,
				LogConstants.OUTGOING_RESPONSE_EVENT_TYPE.getValue(), receiveTransactionResponse, exception);
		LOGGER.info("Receive Transaction Response,{}", message);
		applicationEventPublisher.publishEvent(receiveTransactionResponseEvent);
	}
	
	private void performTxn(Boolean isMPPull, ReceiveTransactionRequest requestBody, Request request, TransactionTypes recTxnType, String authorizationCode
			, String requestType, String responseType, InteropTransactions interopTransaction) throws InterruptedException, ExecutionException {
		if(isMPPull) {
    	    initiateMPPullTransaction(requestBody, request, recTxnType, authorizationCode);
    	}
    	else {
    		doTheTransaction(recTxnType, requestBody, interopTransaction, authorizationCode, requestType, responseType);        		
    	}
	}

	private ReceiveTransactionResponse getReceiveTransactionResponse(ReceiveTransactionRequest request,
			String actionCode, String authorizationCode) {
		ReceiveTransactionResponse response = new ReceiveTransactionResponse();
		response.setPan(request.getPan());
		response.setProcessingCode(request.getProcessingCode());
		response.setTransactionAmount(request.getTransactionAmount());
		response.setConsolidationAmount(request.getConsolidationAmount());
		response.setCardholderBillAmount(request.getCardholderBillAmount());
		response.setDateAndTimeOfTransmission(request.getDateAndTimeOfTransmission());
		response.setCardholderBillingEexchangeRate(request.getCardholderBillingEexchangeRate());
		response.setSystemAuditNumber(request.getSystemAuditNumber());
		response.setDateAndTimeOfTheTransaction(request.getDateAndTimeOfTheTransaction());
		response.setSettlementDate(request.getSettlementDate());
		response.setExchangeDate(request.getExchangeDate());
		response.setBusinessType(request.getBusinessType());
		response.setCountryCodeOfTheAcquiringOrganization(request.getCountryCodeOfTheAcquiringOrganization());
		response.setCountryCodeOftheSenderOrganization(request.getCountryCodeOftheSenderOrganization());
		response.setServicePointDataCode(request.getServicePointDataCode());
		response.setFunctionCode(request.getFunctionCode());
		response.setAuthorizationCode(authorizationCode);
		response.setIdentificationCodeOfTheAcquiringOrganization(
				request.getIdentificationCodeOfTheAcquiringOrganization());
		response.setIdentificationCodeOfTheSendingOrganization(request.getIdentificationCodeOfTheSendingOrganization());
		response.setReferenceNumberOfTheRecovery(request.getReferenceNumberOfTheRecovery());
		response.setCurrencyCodeOfTheTransaction(request.getCurrencyCodeOfTheTransaction());
		response.setCurrencyCodeOfTheCardholderInvoice(request.getCurrencyCodeOfTheCardholderInvoice());
		response.setCurrencyCodeOfTheConsolidation(request.getCurrencyCodeOfTheConsolidation());
		response.setSourceAccountNumber(request.getSourceAccountNumber());
		response.setDestinationAccountNumber(request.getDestinationAccountNumber());
		response.setActionCode(actionCode);
		response.setLengthOfTheAuthorizationCode(authorizationCode != null ? "" + authorizationCode.length() : "0");
		response.setSecurityCheckInfo(request.getSecurityCheckInfo());
		return response;
	}

	private void addSmsDelivery(String msisdn, String serviceType, String languageCode) {
		SmsTemplates smsTemplates = smsTemplatesRepository.findSmsTemplateByTypeAndLang(serviceType, languageCode);
		smsDeliveryRepository.save(SMSUtility.prepareSMSDelivery(smsTemplates.getDescription(), msisdn, languageCode,
				serviceType, resource.getNodeName()));
	}

	private String getAuthorizationCode() {
		return generateSixDigitUniqueId();
	}

	public static String generateSixDigitUniqueId() {
		int idGenLen = 100000;
		String idGenLenStr = "000000";
		SecureRandom generator = new SecureRandom();
		generator.setSeed(System.currentTimeMillis());
		int i = generator.nextInt(idGenLen) % idGenLen;
		java.text.DecimalFormat f = new java.text.DecimalFormat(idGenLenStr);
		return f.format(i);
	}

	private void doTheTransaction(TransactionTypes recTxnType, ReceiveTransactionRequest requestBody,
			InteropTransactions interopTransaction, String authorizationCode, String requestType, String responseType) {
		Response response;
		TxnMode txnMode = new TxnMode(TransactionSubTypes.RECEIVE_TRANSACTION.toString(),
				brokerServiceURLProperties.getUrlCountryIdValue(), brokerServiceURLProperties.getUrlCurrencyValue(),
				brokerServiceURLProperties.getUrlAddonIdValue(), interopTransaction.getInteropTxnId(),
				UnicId.ONE.getVal());
		InteropTransactionDetails interopTransactionDetails = TransactionDataPreparationUtil
				.prepareRequestTransactionDetails(interopTransaction, requestType, thirdPartyProperties,
						Constants.ZERO.getValue(), Constants.ZERO.getValue());
		String txnModeStr = txnMode.toString();
		interopTransactionDetails.setTxnMode(txnModeStr);
		interopTransactionDetailsRepository.save(interopTransactionDetails);
		if (recTxnType.equals(TransactionTypes.MERCHPAY)) {
			response = c2CHandler.execute(requestBody.getSourceAccountNumber(),
					thirdPartyProperties.getChannelUserMsisdn(), requestBody.getConsolidationAmount(),
					interopTransaction.getInteropTxnId(), txnModeStr);
		} else {
			response = p2PCashInHandler.execute(requestBody.getDestinationAccountNumber(),
					requestBody.getConsolidationAmount(), interopTransaction.getInteropTxnId(), txnModeStr);
		}
		interopTransaction.setAuthorizationCode(authorizationCode);
		if (!Optional.ofNullable(response.getMappingResponse()).isPresent()) {
			interOpTransactionRepository.save(TransactionDataPreparationUtil.updateTransactionStatus(interopTransaction,
					TransactionStatus.TRANSACTION_AMBIGUOUS.getStatus()));
			TransactionDataPreparationUtil.prepareTransactionResponse(interopTransaction,
					InteropResponseCodes.READ_TIMEOUT_FROM_BROKER.getStatusCode(),
					getDescriptionForCode.getMappingCode(InteropResponseCodes.READ_TIMEOUT_FROM_BROKER.getStatusCode()),
					getDescriptionForCode.getDescription(Sources.BROKER.toString(),
							InteropResponseCodes.READ_TIMEOUT_FROM_BROKER.getStatusCode(),
							serviceResources.getDefaultLanguage()));
		} else if (ThirdPartyResponseCodes.SUCCESS.getMappedCode()
				.equals(response.getMappingResponse().getMappingCode())) {
			interopTransaction.setMobiquityTransactionId(response.getWalletResponse().getTxnid());
			interOpTransactionRepository.save(TransactionDataPreparationUtil.updateTransactionStatus(interopTransaction,
					TransactionStatus.TRANSACTION_SUCCESS.getStatus()));
			interopTransactionDetailsRepository.save(
					TransactionDataPreparationUtil.prepareResponseTransactionDetails(interopTransaction, responseType,
							response, thirdPartyProperties, Constants.ZERO.getValue(), Constants.ZERO.getValue()));
			addSmsDelivery(requestBody.getDestinationAccountNumber(), SMSNotificationCodes.OFF_US_SUCCESS.toString(),
					serviceResources.getDefaultLanguage());
			this.publishResponseEvent(
					getReceiveTransactionResponse(requestBody,
							InteropResponseCodes.HPS_TXN_SUCCESS_ACTION_CODE.getStatusCode(), authorizationCode),
					interopTransaction.getInteropTxnId(), null);
		} else {
			interOpTransactionRepository.save(TransactionDataPreparationUtil.updateTransactionStatus(interopTransaction,
					TransactionStatus.TRANSACTION_FAIL.getStatus()));
			interopTransactionDetailsRepository.save(
					TransactionDataPreparationUtil.prepareResponseTransactionDetails(interopTransaction, responseType,
							response, thirdPartyProperties, Constants.ZERO.getValue(), Constants.ZERO.getValue()));
			this.publishResponseEvent(
					getReceiveTransactionResponse(requestBody,
							InteropResponseCodes.HPS_TXN_FAILED_ACTION_CODE.getStatusCode(), authorizationCode),
					interopTransaction.getInteropTxnId(), null);
		}
	}

	private void validateRequest(ReceiveTransactionRequest requestBody) {
		RequestValidationResponse requestValidationResponse = requestValidations.validate(requestBody);
		if (!requestValidationResponse.getStatusCode()
				.equals(SuccessStatus.SUCCESSFUL_REQUEST_VALIDATION.getStatusCode())) {
			throw new InteropException(requestValidationResponse.getStatusCode(),
					requestValidationResponse.getEntity());
		}
	}

	private void initiateMPPullTransaction(ReceiveTransactionRequest requestBody, Request request,
			TransactionTypes recTxnType, String authorizationCode) throws InterruptedException, ExecutionException {
		InteropTransactions interopTransaction = interOpTransactionRepository
				.save(TransactionDataPreparationUtil.prepareInteropReceiveTransaction(requestBody, request,
						recTxnType.getTransactionType(), TransactionStatus.TRANSACTION_INITIATED.getStatus(), Boolean.TRUE));
		// send USSD push
		ExecutorService executor = Executors.newSingleThreadExecutor();
		boolean isTxnConfirmationInprogress = false;
		do {
			Future<Boolean> future = executor.submit(new ConfirmTxnVerificationThread(
					interopTransaction.getInteropTxnId(), interOpTransactionRepository));
			isTxnConfirmationInprogress = future.get();
		} while (!isTxnConfirmationInprogress);

		this.publishResponseEvent(
				getReceiveTransactionResponse(requestBody,
						InteropResponseCodes.HPS_TXN_SUCCESS_ACTION_CODE.getStatusCode(), authorizationCode),
				request.getInteropReferenceId(), null);
	}
}
