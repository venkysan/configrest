package com.comviva.interop.txnengine.services;

import java.net.SocketTimeoutException;
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

import com.comviva.interop.txnengine.configuration.BrokerServiceURLProperties;
import com.comviva.interop.txnengine.entities.InteropTransactions;
import com.comviva.interop.txnengine.enums.InteropResponseCodes;
import com.comviva.interop.txnengine.enums.LogConstants;
import com.comviva.interop.txnengine.enums.Sources;
import com.comviva.interop.txnengine.enums.SuccessStatus;
import com.comviva.interop.txnengine.enums.ThirdPartyResponseCodes;
import com.comviva.interop.txnengine.enums.TransactionStatus;
import com.comviva.interop.txnengine.enums.TransactionSubTypes;
import com.comviva.interop.txnengine.enums.TransactionTypes;
import com.comviva.interop.txnengine.events.CreateTransactionResponseEvent;
import com.comviva.interop.txnengine.exception.InteropException;
import com.comviva.interop.txnengine.mobiquity.service.handlers.RetailerGetLangHandler;
import com.comviva.interop.txnengine.mobiquity.service.handlers.SubscriberGetLangHandler;
import com.comviva.interop.txnengine.model.Request;
import com.comviva.interop.txnengine.model.RequestValidationResponse;
import com.comviva.interop.txnengine.model.Response;
import com.comviva.interop.txnengine.model.TransactionRequest;
import com.comviva.interop.txnengine.model.TransactionResponse;
import com.comviva.interop.txnengine.repositories.InteropTransactionsRepository;
import com.comviva.interop.txnengine.request.validations.GetDescriptionForCode;
import com.comviva.interop.txnengine.request.validations.ValidateCreateTransactionRequestBody;
import com.comviva.interop.txnengine.util.CastUtils;
import com.comviva.interop.txnengine.util.LoggerUtil;
import com.comviva.interop.txnengine.util.TransactionDataPreparationUtil;

@Service("CreateTransactionService")
public class CreateTransactionService implements ExecutableServices {

	private static final Logger LOGGER = LoggerFactory.getLogger(CreateTransactionService.class);

	@Autowired
	private ValidateCreateTransactionRequestBody requestValidations;

	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;

	@Autowired
	private GetDescriptionForCode getDescriptionForCode;

	@Autowired
	private GetDefaultWalletStatusHandler getDefaultWalletStatusHandler;

	@Autowired
	private SubscriberGetLangHandler subscriberGetLangHandler;

	@Autowired
	private P2POnUsTransactionService p2pOnUsTransactionService;

	@Autowired
	private OffUsTransactionService offUsTransactionService;

	@Autowired
	private MPOnUsTransactionService merchantPaymentOnUsTransactionService;

	@Autowired
	private RetailerGetLangHandler retailerGetLangHandler;

	@Autowired
	private BrokerServiceURLProperties brokerServiceURLProperties;

	@Autowired
	private InteropTransactionsRepository interopTransactionsRepository;

	@Autowired
	private OffUsTransactionHPSHandler transactionRequestHPSHandler;

	@Override
	@Async
	public void execute(Request request) {
		TransactionRequest requestBody = CastUtils.toTransactionRequest(request.getRequestAttr());
		try {
			validateRequest(requestBody);
			requestProcess(requestBody,request);
		} catch (InteropException e) {
			updateTxnFailStatus(request);
			String description = null;
			if(e.getMessage()!=null && !e.getMessage().isEmpty()){
				description = e.getMessage();
			}else {
				description = getDescriptionForCode.getDescription(e.getEntity(), e.getStatusCode(),
						requestBody.getLang());
			}
			TransactionResponse transactionResponse = new TransactionResponse(description,
					CastUtils.joinStatusCode(e.getEntity(), e.getStatusCode()),
					Optional.ofNullable(e.getMappedCode()).isPresent() ? e.getMappedCode()
							: getDescriptionForCode.getMappingCode(e.getStatusCode()));
			this.publishResponseEvent(transactionResponse, request.getInteropReferenceId(), requestBody, e);
		} catch (RestClientException restException) {
			TransactionResponse transactionResponse = null;
			updateTxnFailStatus(request);
			if (restException.getCause() instanceof SocketTimeoutException) {
				String description = getDescriptionForCode.getDescription(
						InteropResponseCodes.READ_TIMEOUT_FROM_BROKER.getEntity().toString(),
						InteropResponseCodes.READ_TIMEOUT_FROM_BROKER.getStatusCode(), requestBody.getLang());
				transactionResponse = new TransactionResponse(description,
						CastUtils.joinStatusCode(InteropResponseCodes.READ_TIMEOUT_FROM_BROKER.getEntity().toString(),
								InteropResponseCodes.READ_TIMEOUT_FROM_BROKER.getStatusCode()),
						getDescriptionForCode
								.getMappingCode(InteropResponseCodes.READ_TIMEOUT_FROM_BROKER.getStatusCode()));
			} else {
				String description = getDescriptionForCode.getDescription(
						InteropResponseCodes.NOT_ABLE_TO_CONNECT_THIRD_PARTY.getEntity().toString(),
						InteropResponseCodes.NOT_ABLE_TO_CONNECT_THIRD_PARTY.getStatusCode(), requestBody.getLang());
				transactionResponse = new TransactionResponse(description,
						CastUtils.joinStatusCode(
								InteropResponseCodes.NOT_ABLE_TO_CONNECT_THIRD_PARTY.getEntity().toString(),
								InteropResponseCodes.NOT_ABLE_TO_CONNECT_THIRD_PARTY.getStatusCode()),
						getDescriptionForCode
								.getMappingCode(InteropResponseCodes.NOT_ABLE_TO_CONNECT_THIRD_PARTY.getStatusCode()));
			}
			this.publishResponseEvent(transactionResponse, request.getInteropReferenceId(), requestBody, restException);
		} catch (Exception ex) {
			updateTxnFailStatus(request);
			String description = getDescriptionForCode.getDescription(
					InteropResponseCodes.INTERNAL_ERROR.getEntity().toString(),
					InteropResponseCodes.INTERNAL_ERROR.getStatusCode(), requestBody.getLang());
			TransactionResponse transactionResponse = new TransactionResponse(description,
					CastUtils.joinStatusCode(InteropResponseCodes.INTERNAL_ERROR.getEntity().toString(),
							InteropResponseCodes.INTERNAL_ERROR.getStatusCode()),
					getDescriptionForCode.getMappingCode(InteropResponseCodes.INTERNAL_ERROR.getStatusCode()));
 			this.publishResponseEvent(transactionResponse, request.getInteropReferenceId(), requestBody, ex);
		}
	}
	
	private void requestProcess(TransactionRequest requestBody,Request request) throws InterruptedException, ExecutionException {
		boolean isUserRegisteredWithSameMFS = false;
		String txnStatus = null;
		if (TransactionTypes.MERCHPAY_PULL.getTransactionType().equals(requestBody.getTransactionType())) {
			txnStatus = TransactionStatus.TRANSACTION_INITIATED.getStatus();
		} else {
			txnStatus = TransactionStatus.TRANSACTION_IN_PROGRESS.getStatus();
		}
		InteropTransactions interopTransaction = interopTransactionsRepository.save(
				TransactionDataPreparationUtil.prepareInteropTransaction(requestBody, request, null, txnStatus));
		isUserRegisteredWithSameMFS = getDefaultWalletStatusHandler.isUserRegisteredWithSameMFS(
				requestBody.getDebitParty().get(0).getValue(), requestBody.getCreditParty().get(0).getValue(),
				request.getInteropReferenceId(), requestBody.getTransactionType());
		if (isUserRegisteredWithSameMFS) {
			verifyReceiverLangugae(request, requestBody, interopTransaction);
		} else {
			doOffUsTransaction(interopTransaction, requestBody, request);
		}
	}

	private void publishResponseEvent(TransactionResponse transactionResponse, String reqId, TransactionRequest req,
			Exception ex) {
		CreateTransactionResponseEvent createTransactionResponseEvent = new CreateTransactionResponseEvent(this,
				transactionResponse, reqId);
		String message = LoggerUtil.prepareLogDetailForCreateTransactionResponse(req,
				brokerServiceURLProperties.getUrlCountryIdValue(), reqId,
				LogConstants.OUTGOING_RESPONSE_EVENT_TYPE.getValue(), transactionResponse, ex);
		LOGGER.info("Create Transaction  service response: {}", message);
		applicationEventPublisher.publishEvent(createTransactionResponseEvent);
	}

	private Response getReceiverLanguage(String interOpRefId, String payeeMSISDN, String transactionType,
			String payerMSISDN) {
		Response getLangResponse = null;
		if (TransactionTypes.P2P.getTransactionType().equals(transactionType)
				|| TransactionTypes.MERCHPAY_PULL.getTransactionType().equals(transactionType)) {
			getLangResponse = subscriberGetLangHandler.execute(payerMSISDN, payeeMSISDN, interOpRefId, getTxnType(transactionType));
		} else if (TransactionTypes.MERCHPAY.getTransactionType().equals(transactionType)) {
			getLangResponse = retailerGetLangHandler.callBrokerForRGetLangService(payerMSISDN, payeeMSISDN,
					interOpRefId);
		}
		if (null == getLangResponse) {
			throw new InteropException(InteropResponseCodes.INTERNAL_ERROR.getStatusCode(),
					InteropResponseCodes.INTERNAL_ERROR.getEntity().toString(),
					getDescriptionForCode.getMappingCode(InteropResponseCodes.INTERNAL_ERROR.getStatusCode()));
		}
		return getLangResponse;
	}
	
	private TransactionTypes getTxnType(String txnType) {
		if(TransactionTypes.P2P.getTransactionType().equals(txnType)) {
			return TransactionTypes.P2P;
		}
		else {
			return TransactionTypes.MERCHPAY_PULL;
		}
	}

	private void doOnUsTransaction(String type, Request request, InteropTransactions interopTransaction,
			TransactionRequest requestBody) throws InterruptedException, ExecutionException {
		if (TransactionTypes.P2P.getTransactionType().equals(type)) {
			p2pOnUsTransactionService.doP2POnUsTransaction(request, interopTransaction);
		} else if (TransactionTypes.MERCHPAY.getTransactionType().equals(type)) {
			merchantPaymentOnUsTransactionService.doMPOnUsTransaction(request, interopTransaction);
		} else if (TransactionTypes.MERCHPAY_PULL.getTransactionType().equals(type)) {
			// send USSD push
			ExecutorService executor = Executors.newSingleThreadExecutor();
			boolean isTxnConfirmationInprogress = false;
			do {
				Future<Boolean> future = executor.submit(new ConfirmTxnVerificationThread(
						interopTransaction.getInteropTxnId(), interopTransactionsRepository));
				isTxnConfirmationInprogress = future.get();
			} while (!isTxnConfirmationInprogress);

			TransactionResponse transactionResponse = TransactionDataPreparationUtil.prepareTransactionResponse(
					interopTransaction, "",
					getDescriptionForCode.getMappingCode(InteropResponseCodes.SUCCESS.getStatusCode()),
					getDescriptionForCode.getDescription(InteropResponseCodes.SUCCESS.getEntity().toString(),
							InteropResponseCodes.SUCCESS.getStatusCode(), requestBody.getLang()));
			transactionResponse.setCode(CastUtils.joinStatusCode(InteropResponseCodes.SUCCESS.getEntity().toString(),
					InteropResponseCodes.SUCCESS.getStatusCode()));
			this.publishResponseEvent(transactionResponse, request.getInteropReferenceId(), requestBody, null);
		}
	}

	private void verifyReceiverLangugae(Request request, TransactionRequest requestBody,
			InteropTransactions interopTransaction) throws InterruptedException, ExecutionException {
		Response getLangResponse = getReceiverLanguage(request.getInteropReferenceId(),
				requestBody.getCreditParty().get(0).getValue(), requestBody.getTransactionType(),
				requestBody.getDebitParty().get(0).getValue());
		if (!Optional.ofNullable(getLangResponse.getMappingResponse()).isPresent()) {
			throw new InteropException(InteropResponseCodes.READ_TIMEOUT_FROM_BROKER.getStatusCode(),
					Sources.BROKER.toString());
		} else if ((ThirdPartyResponseCodes.SUCCESS.getMappedCode()
                .equals(getLangResponse.getMappingResponse().getMappingCode())) ||
                (!ThirdPartyResponseCodes.USER_INVALID.getMappedCode().equals(getLangResponse.getMappingResponse().getMappingCode()) &&
                        brokerServiceURLProperties.getBrokerSuccessCode().equals(getLangResponse.getBrokerResponse().getBrokerCode()))) {
			doOnUsTransaction(requestBody.getTransactionType(), request, interopTransaction, requestBody);
		} else {
			throw new InteropException(getLangResponse.getBrokerResponse().getBrokerCode(), Sources.BROKER.toString(),
					getLangResponse.getMappingResponse().getMappingCode(),getLangResponse.getWalletResponse().getMessage());
		}
	}

	private void updateTxnFailStatus(Request request) {
		InteropTransactions interopTransaction = interopTransactionsRepository
				.findInteropTransactionsByTxnId(request.getInteropReferenceId());
		if (Optional.ofNullable(interopTransaction).isPresent()) {
			interopTransactionsRepository.save(TransactionDataPreparationUtil
					.updateTransactionStatus(interopTransaction, TransactionStatus.TRANSACTION_FAIL.getStatus()));
		}
	}
	
	private void doOffUsTransaction(InteropTransactions interopTransaction, TransactionRequest requestBody, Request request) {
		if (TransactionTypes.MERCHPAY_PULL.getTransactionType().equals(requestBody.getTransactionType())) {
			interopTransaction.setTransactionSubType(TransactionSubTypes.MP_PULL_OFF_US.toString());
			transactionRequestHPSHandler.execute(request, null, null, interopTransaction);
			TransactionResponse transactionResponse = TransactionDataPreparationUtil.prepareTransactionResponse(
					interopTransaction, "",
					getDescriptionForCode
							.getMappingCode(InteropResponseCodes.TXN_INITIATED_SUCCESSFULLY.getStatusCode()),
					getDescriptionForCode.getDescription(
							InteropResponseCodes.TXN_INITIATED_SUCCESSFULLY.getEntity().toString(),
							InteropResponseCodes.TXN_INITIATED_SUCCESSFULLY.getStatusCode(),
							requestBody.getLang()));
			transactionResponse.setCode(CastUtils.joinStatusCode(
					InteropResponseCodes.TXN_INITIATED_SUCCESSFULLY.getEntity().toString(),
					InteropResponseCodes.TXN_INITIATED_SUCCESSFULLY.getStatusCode()));
			this.publishResponseEvent(transactionResponse, request.getInteropReferenceId(), requestBody, null);
		} else {
			offUsTransactionService.doOffUsTransaction(request, interopTransaction);
		}
	}
	
	private void validateRequest(TransactionRequest requestBody) {
		RequestValidationResponse requestValidationResponse = requestValidations.validate(requestBody);
		if (!requestValidationResponse.getStatusCode()
				.equals(SuccessStatus.SUCCESSFUL_REQUEST_VALIDATION.getStatusCode())) {
			throw new InteropException(requestValidationResponse.getStatusCode(),
					requestValidationResponse.getEntity());
		}
	}
}
