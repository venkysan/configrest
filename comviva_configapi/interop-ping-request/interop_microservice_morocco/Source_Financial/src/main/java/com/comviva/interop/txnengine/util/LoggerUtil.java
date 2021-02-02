package com.comviva.interop.txnengine.util;


import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.comviva.interop.txnengine.enums.LogConstants;
import com.comviva.interop.txnengine.enums.ServiceTypes;
import com.comviva.interop.txnengine.model.ConfirmTransactionRequest;
import com.comviva.interop.txnengine.model.ConfirmTransactionResponse;
import com.comviva.interop.txnengine.model.LogDetail;
import com.comviva.interop.txnengine.model.NetworkMessageRequest;
import com.comviva.interop.txnengine.model.NetworkMessageResponse;
import com.comviva.interop.txnengine.model.OrangeMoneyOperations;
import com.comviva.interop.txnengine.model.OrangeMoneyTechnicalAccounts;
import com.comviva.interop.txnengine.model.PendingTransactionRequest;
import com.comviva.interop.txnengine.model.PendingTransactionsResponse;
import com.comviva.interop.txnengine.model.QuotationRequest;
import com.comviva.interop.txnengine.model.QuotationResponse;
import com.comviva.interop.txnengine.model.ReceiveTransactionRequest;
import com.comviva.interop.txnengine.model.ReceiveTransactionResponse;
import com.comviva.interop.txnengine.model.Response;
import com.comviva.interop.txnengine.model.TransactionCorrectionResponse;
import com.comviva.interop.txnengine.model.TransactionRequest;
import com.comviva.interop.txnengine.model.TransactionResponse;
import com.comviva.interop.txnengine.model.TransactionStatusRequest;
import com.comviva.interop.txnengine.model.TransactionStatusResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class LoggerUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LoggerUtil.class);
	
	private LoggerUtil() {
	}
	
	
	/**
	 * @param request
	 * @param country
	 * @param interOpRefId
	 * @param eventType
	 * @param useCase
	 * @return
	 */
	public static String prepareLogDetailForTxnStatusRequest(TransactionStatusRequest request, String country, String interOpRefId, String eventType, String useCase) {
	    String jsonString = LogConstants.EMPTY_STRING.getValue();
	    try {
	        LogDetail logDetail = prepareRequest(country, interOpRefId, eventType);
	        logDetail.setLang(request.getLang());
	        prepareLogDetailDynamicValues(logDetail, null, LogConstants.EMPTY_STRING.getValue(), LogConstants.EMPTY_STRING.getValue(), null, null, null);
            logDetail.setParameters(request.convertToJSON(request));
            logDetail.setUseCase(useCase);
            jsonString = logDetail.convertToJSON(logDetail); 
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
        }
	    return jsonString;
	}
	
	private static LogDetail prepareRequest(String country, String interOpRefId, String eventType) {
	    LogDetail logDetail = prepareLogConstants(country, eventType);
	    logDetail.setSessionId(interOpRefId);
	    return logDetail;
	}
	
	/**
	 * @param request
	 * @param country
	 * @param interOpRefId
	 * @param eventType
	 * @return
	 */
	public static String prepareLogDetailForCreateTxnRequest(TransactionRequest request, String country, String interOpRefId, String eventType, String useCase, int pinLength) {
        String jsonString = LogConstants.EMPTY_STRING.getValue();
        try {
            String  pin = request.getDebitPartyCredentials().getPin();
            request.getDebitPartyCredentials().setPin(StringUtils.getMaskedData(pinLength, "*"));

            LogDetail logDetail = prepareRequest(country, interOpRefId, eventType);
            logDetail.setParameters(request.convertToJSON(request));
            logDetail.setLang(request.getLang());
            prepareLogDetailDynamicValues(logDetail, request.getCurrency(), (null!= request.getDebitParty() && !request.getDebitParty().isEmpty() 
                                    ?request.getDebitParty().get(0).getValue() : LogConstants.EMPTY_STRING.getValue()),(null!= request.getCreditParty() && !request.getCreditParty().isEmpty() 
                                    ? request.getCreditParty().get(0).getValue() : LogConstants.EMPTY_STRING.getValue()),
                    request.getAmount(), request.getExtOrgRefId(), request.getRequestSource());
            logDetail.setUseCase(useCase);
            jsonString = logDetail.convertToJSON(logDetail);
            request.getDebitPartyCredentials().setPin(pin);
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return jsonString;
    }
	
	/**
	 * @param logDetail
	 * @param currency
	 * @param payerMsisdn
	 * @param payeeMsisdn
	 * @param amount
	 * @param extOrgRefId
	 * @param requestSource
	 * @return
	 */
	private static LogDetail prepareLogDetailDynamicValues(LogDetail logDetail, String currency, String payerMsisdn, String payeeMsisdn, String amount, String extOrgRefId, String requestSource) {
	    logDetail.setCurrency(currency);
        logDetail.setMsisdn(payerMsisdn);
        logDetail.setMsisdn1(payeeMsisdn);
        logDetail.setAmount(amount);
        logDetail.setRequestId(extOrgRefId);
        logDetail.setPeer(requestSource);
        return logDetail;
	}
	
	private static LogDetail prepareLogResponse(String country, String eventType, String interOpRefId, 
	        String serviceType, String language, String extOrgRefId, String requestSource) {
	    LogDetail logDetail = prepareLogConstants(country, eventType);
        logDetail.setSessionId(interOpRefId);
        logDetail.setUseCase(serviceType);
        logDetail.setLang(language);            
        logDetail.setRequestId(extOrgRefId);
        logDetail.setPeer(requestSource);
        return logDetail;
	}
	
	private static void setResponse(Exception exception, LogDetail logDetail, String status) {
	    logDetail.setErrorDetails(exception != null ? covertStackTraceToString(exception) : "");
	    logDetail.setReturnedStatus(status);
	}
	private static LogDetail prepareLogConstants(String country, String eventType) {
	    LogDetail logDetail = new LogDetail();
	    logDetail.setDateTime(getTimeStamp());
        logDetail.setLevel(LogConstants.INFO.getValue());
        logDetail.setCountry(country);
        logDetail.setEventType(eventType);
	    return logDetail;
	}
	
	/**
	 * @param eventType
	 * @param exception
	 * @return
	 */
	public static String printLog(String eventType, Exception exception) {
        String jsonString = LogConstants.EMPTY_STRING.getValue();
        try {
            LogDetail logDetail = prepareLogConstants(LogConstants.EMPTY_STRING.getValue(), eventType);
            logDetail.setErrorDetails(exception != null ? covertStackTraceToString(exception) : LogConstants.EMPTY_STRING.getValue());
            jsonString = logDetail.convertToJSON(logDetail); 
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return jsonString;
    }
	
	public static String printLogForChannelUserByMSISDN(String eventType, Exception exception, String msisdn) {
        String jsonString = LogConstants.EMPTY_STRING.getValue();
        try {
            LogDetail logDetail = prepareLogConstants(LogConstants.EMPTY_STRING.getValue(), eventType);
            logDetail.setErrorDetails(exception != null ? covertStackTraceToString(exception) : LogConstants.EMPTY_STRING.getValue());
            logDetail.setMsisdn(msisdn);
            jsonString = logDetail.convertToJSON(logDetail); 
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return jsonString;
    }
	
	 public static String printLog(String eventType, Exception exception, String useCase) {
	        String jsonString = LogConstants.EMPTY_STRING.getValue();
	        try {
	            LogDetail logDetail = prepareLogConstants(LogConstants.EMPTY_STRING.getValue(), eventType);
	            logDetail.setErrorDetails(exception != null ? covertStackTraceToString(exception) : LogConstants.EMPTY_STRING.getValue());
	            logDetail.setUseCase(useCase);
	            jsonString = logDetail.convertToJSON(logDetail); 
	        } catch (JsonProcessingException e) {
	            LOGGER.error(e.getMessage(), e);
	        }
	        return jsonString;
	    }
	 
	 public static String printLogForTxnCorrectionRequest(String eventType, Exception exception, String useCase, String transactionId, String rrn) {
	        String jsonString = LogConstants.EMPTY_STRING.getValue();
	        try {
	            LogDetail logDetail = prepareLogConstants(LogConstants.EMPTY_STRING.getValue(), eventType);
	            if(null == transactionId) {
	            	logDetail.setSessionId(rrn);	
	            }
	            else {
	            	logDetail.setSessionId(transactionId);
	            }
	            logDetail.setErrorDetails(exception != null ? covertStackTraceToString(exception) : LogConstants.EMPTY_STRING.getValue());
	            logDetail.setUseCase(useCase);
	            jsonString = logDetail.convertToJSON(logDetail); 
	        } catch (JsonProcessingException e) {
	            LOGGER.error(e.getMessage(), e);
	        }
	        return jsonString;
	    }
	 
	 public static String prepareLogDetailForTxnCorrectionResponse(String country, String interOpRefId, 
		        String eventType, TransactionCorrectionResponse response, Exception exception) {
	        String jsonString = "";
	        try {
	            LogDetail logDetail =  prepareLogResponse(country, eventType, interOpRefId,LogConstants.TXN_CORRECTION.getValue(),
	                    null, null, null);
	            setResponse(exception, logDetail, "");
	            logDetail.setParameters(response.convertToJSON(response));
	            jsonString = logDetail.convertToJSON(logDetail); 
	        } catch (JsonProcessingException e) {
	            LOGGER.error(e.getMessage(), e);
	        }
	        return jsonString;
	    }
	
	public static String prepareLogDetailForQuotationResponse(QuotationRequest request, String country, String interOpRefId, 
	        String eventType, QuotationResponse response, Exception exception) {
        String jsonString = "";
        try {
            LogDetail logDetail =  prepareLogResponse(country, eventType, interOpRefId,ServiceTypes.TRANSACTION_QUOTATION.toString(),
                    request.getLang(), request.getExtOrgRefId(), request.getRequestSource());
            setResponse(exception, logDetail, "");
            logDetail.setParameters(response.convertToJSON(response));
            jsonString = logDetail.convertToJSON(logDetail); 
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return jsonString;
    }
	
	/**
	 * @param request
	 * @param country
	 * @param interOpRefId
	 * @param eventType
	 * @param response
	 * @param exception
	 * @return
	 */
	public static String prepareLogDetailForCreateTransactionResponse(TransactionRequest request, String country, String interOpRefId, String eventType,
	        TransactionResponse response, Exception exception) {
        String jsonString = LogConstants.EMPTY_STRING.getValue();
        try {
            if(null == response) {
                response = new TransactionResponse();
            }
            LogDetail logDetail =  prepareLogResponse(country, eventType, interOpRefId,ServiceTypes.CREATE_TRANSACTION.toString(),
                    request.getLang(), request.getExtOrgRefId(), request.getRequestSource());
            setResponse(exception, logDetail,response.getStatus());
            logDetail.setParameters(response.convertToJSON(response));
            jsonString = logDetail.convertToJSON(logDetail); 
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return jsonString;
    }
	
	/**
	 * @param request
	 * @param country
	 * @param interOpRefId
	 * @param eventType
	 * @param response
	 * @return
	 */
	public static String prepareLogDetailForGetLangResponse(TransactionRequest request, String country, String interOpRefId, String eventType, Response response) {
        String jsonString = LogConstants.EMPTY_STRING.getValue();
        try {
            LogDetail logDetail =  prepareLogResponse(country, eventType, interOpRefId,LogConstants.GET_LANG.getValue(),
                    request.getLang(), request.getExtOrgRefId(), request.getRequestSource());
            setResponse(null, logDetail,LogConstants.EMPTY_STRING.getValue());
            logDetail.setParameters(response.toString());
            jsonString = logDetail.convertToJSON(logDetail); 
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return jsonString;
    }
	
    /**
     * @param request
     * @param country
     * @param interOpRefId
     * @param eventType
     * @param response
     * @return
     */
    public static String prepareLogDetailForGetLangRequest(TransactionRequest request, String country, String interOpRefId, String eventType, String response) {
        String jsonString = LogConstants.EMPTY_STRING.getValue();
        try {
            LogDetail logDetail = prepareRequest(country, interOpRefId, eventType);
            prepareLogDetailDynamicValues(logDetail, request.getCurrency(), request.getDebitParty().get(0).getValue() , request.getCreditParty().get(0).getValue(),
                    request.getAmount(), request.getExtOrgRefId(), request.getRequestSource());
            logDetail.setParameters(response);        
            logDetail.setLang(request.getLang());
            jsonString = logDetail.convertToJSON(logDetail); 
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return jsonString;
    }

    /**
     * @param eventType
     * @param countryId
     * @param from
     * @param limit
     * @return
     */
    public static String prepareLogDetailForOMOperationsRequest(String eventType, String countryId, String from, int limit) {
        LogDetail logDetail = new LogDetail();
        String jsonString = LogConstants.EMPTY_STRING.getValue();
        try {
            
            logDetail.setDateTime(getTimeStamp());
            logDetail.setLevel(LogConstants.INFO.getValue());
            logDetail.setCountry(countryId);
            logDetail.setEventType(eventType);
            logDetail.setParameters(prepareOMOperationRequestString(countryId,from,limit));
            jsonString = logDetail.convertToJSON(logDetail); 
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return jsonString;
    }
    
    /**
     * @param eventType
     * @param response
     * @param countryId
     * @param exception
     * @return
     */
    public static String prepareLogDetailForOMOperationResponse(String eventType, OrangeMoneyOperations response, String countryId, Exception exception) {
        String jsonString = LogConstants.EMPTY_STRING.getValue();
        try {
            LogDetail logDetail = new LogDetail();
            logDetail.setDateTime(getTimeStamp());
            logDetail.setLevel(LogConstants.INFO.getValue());
            logDetail.setCountry(countryId);
            logDetail.setUseCase(LogConstants.OM_OPERATIONS.getValue());
            logDetail.setEventType(eventType);
            logDetail.setParameters(response.toString());
            logDetail.setErrorDetails(exception != null ? covertStackTraceToString(exception) : "");
            jsonString = logDetail.convertToJSON(logDetail); 
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return jsonString;
    }
    
    /**
     * @param eventType
     * @param countryId
     * @return
     */
    public static String prepareLogDetailForTechnicalWalletsRequest(String eventType, String countryId) {
        LogDetail logDetail = new LogDetail();
        String jsonString = LogConstants.EMPTY_STRING.getValue();
        try {
            logDetail.setDateTime(getTimeStamp());
            logDetail.setLevel(LogConstants.INFO.getValue());
            logDetail.setCountry(countryId);
            logDetail.setEventType(eventType);
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode objectNode1 = mapper.createObjectNode();
            objectNode1.put("countryId", countryId);
            logDetail.setParameters(objectNode1.toString());
            jsonString = logDetail.convertToJSON(logDetail); 
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return jsonString;
    }
    
    /**
     * @param eventType
     * @param response
     * @param countryId
     * @param exception
     * @return
     */
    public static String prepareLogDetailForTechnicalWalletsResponse(String eventType, OrangeMoneyTechnicalAccounts response, String countryId, Exception exception) {
        String jsonString = LogConstants.EMPTY_STRING.getValue();
        try {
            LogDetail logDetail = new LogDetail();
            logDetail.setDateTime(getTimeStamp());
            logDetail.setLevel(LogConstants.INFO.getValue());
            logDetail.setCountry(countryId);
            logDetail.setUseCase(LogConstants.TECHNICAL_WALLETS.getValue());
            logDetail.setEventType(eventType);
            logDetail.setParameters(response.toString());
            logDetail.setErrorDetails(exception != null ? covertStackTraceToString(exception) : "");
            jsonString = logDetail.convertToJSON(logDetail); 
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return jsonString;
    }
    
    private static String prepareOMOperationRequestString(String countryId, String from, int limit) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode1 = mapper.createObjectNode();
        objectNode1.put("countryId", countryId);
        objectNode1.put("from", from);
        objectNode1.put("limit", limit);
        return objectNode1.toString();
    }
    
    private static String prepareListOfTxnRequestString(String lang, String extOrgRefId, String startDate, String endDate,
            int offset, int limit) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode1 = mapper.createObjectNode();
        objectNode1.put("lang", lang);
        objectNode1.put("extOrgRefId", extOrgRefId);
        objectNode1.put("startDate", startDate);
        objectNode1.put("endDate", endDate);
        objectNode1.put("offset", offset);
        objectNode1.put("limit", limit);
        return objectNode1.toString();
    }
    
	private static Timestamp getTimeStamp() {
        return new Timestamp(Calendar.getInstance().getTimeInMillis());
    }
    
    /**
     * @param ex -- the exception to print stacktrace
     * @return stack trace as string
     */
    public static String covertStackTraceToString(Exception ex) {
        StringBuilder result = new StringBuilder(ex.toString() + "\n");
        StackTraceElement[] trace = ex.getStackTrace();
        for (int i=0;i<trace.length;i++) {
            result.append(trace[i].toString() + "\n");
        }
        return result.toString();
    }
    
    /**
     * @param eventType
     * @param msisdn
     * @param exception
     * @param message
     * @param useCase
     * @return
     */
    public static String printLogForSMS(String eventType, String msisdn, Exception exception, String message, String useCase) {
        String jsonString = LogConstants.EMPTY_STRING.getValue();
        try {
            LogDetail logDetail = prepareLogConstants(LogConstants.EMPTY_STRING.getValue(), eventType);
            logDetail.setMsisdn(msisdn);
            logDetail.setErrorDetails(exception != null ? covertStackTraceToString(exception) : LogConstants.EMPTY_STRING.getValue());
            logDetail.setParameters(message);
            logDetail.setUseCase(useCase);
            jsonString = logDetail.convertToJSON(logDetail); 
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return jsonString;
    }
    
    public static String prepareLogDetailForBrokerRequest(String country, String currency, String interOpRefId, String eventType, Map<String, String> request, String useCase, int length) {
        String jsonString = LogConstants.EMPTY_STRING.getValue();
        try {
            String pin = request.get(MobiquityConst.PIN.getValue());
            if(null != pin) {
                request.put(MobiquityConst.PIN.getValue(), StringUtils.getMaskedData(length, "*"));    
            }
            LogDetail logDetail = prepareRequest(country, interOpRefId, eventType);
            prepareLogDetailDynamicValues(logDetail, currency, request.get(MobiquityConst.MSISDN.getValue()) , request.get(MobiquityConst.MSISDN2.getValue()),
                    request.get(MobiquityConst.AMOUNT.getValue()), null, null);
            logDetail.setParameters(request.toString());
            logDetail.setUseCase(useCase);
            jsonString = logDetail.convertToJSON(logDetail); 
            if(null != pin) {
                request.put(MobiquityConst.PIN.getValue(), pin);    
            }
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return jsonString;
    }
    
    public static String prepareLogDetailForBrokerResponse(Map<String, String> request,String country, String interOpRefId, String eventType, Response response, String useCase) {
        String jsonString = LogConstants.EMPTY_STRING.getValue();
        try {
            LogDetail logDetail =  prepareLogResponse(country, eventType, interOpRefId,useCase, null, null, null);
            setResponse(null, logDetail,LogConstants.EMPTY_STRING.getValue());
            logDetail.setParameters(response.toString());
            jsonString = logDetail.convertToJSON(logDetail); 
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return jsonString;
    }
    
    public static String prepareLogDetailForHPSRequest(String msisdn, String country, 
            String interOpRefId, String eventType, String request, String lang) {
        LogDetail logDetail = new LogDetail();
        String jsonString = LogConstants.EMPTY_STRING.getValue();
        try {
            logDetail.setDateTime(getTimeStamp());
            logDetail.setLevel(LogConstants.INFO.getValue());
            logDetail.setSessionId(interOpRefId);
            logDetail.setCountry(country);
            logDetail.setMsisdn(msisdn);
            logDetail.setEventType(eventType);
            logDetail.setLang(lang);
            logDetail.setParameters(request);
            jsonString = logDetail.convertToJSON(logDetail); 
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return jsonString;
    }
    
    public static String prepareLogDetailForHPSResponse(String lang, String country, String interOpRefId, String eventType, String response, String useCase) {
        String jsonString = LogConstants.EMPTY_STRING.getValue();
        try {
            LogDetail logDetail = new LogDetail();
            logDetail.setDateTime(getTimeStamp());
            logDetail.setLevel(LogConstants.INFO.getValue());
            logDetail.setSessionId(interOpRefId);
            logDetail.setCountry(country);
            logDetail.setReturnedStatus(LogConstants.EMPTY_STRING.getValue());
            logDetail.setLang(lang);
            logDetail.setEventType(eventType);
            logDetail.setUseCase(useCase);
            logDetail.setParameters(response);
            logDetail.setErrorDetails(LogConstants.EMPTY_STRING.getValue());
            jsonString = logDetail.convertToJSON(logDetail); 
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return jsonString;
    }
    
    public static String prepareLogDetailForTxnQuotationRequest(QuotationRequest request, String country, String interOpRefId, String eventType, String useCase) {
        String jsonString = LogConstants.EMPTY_STRING.getValue();
        try {
            LogDetail logDetail = prepareRequest(country, interOpRefId, eventType);
            logDetail.setLang(request.getLang());
             prepareLogDetailDynamicValues(logDetail, request.getCurrency(), (null != request.getDebitParty() && !request.getDebitParty().isEmpty() 
                        ?request.getDebitParty().get(0).getValue() : LogConstants.EMPTY_STRING.getValue()),
                        (null != request.getCreditParty() && !request.getCreditParty().isEmpty()
                        ? request.getCreditParty().get(0).getValue() : LogConstants.EMPTY_STRING.getValue()),
                        request.getAmount(), request.getExtOrgRefId(), request.getRequestSource());
            logDetail.setParameters(request.convertToJSON(request));
            logDetail.setUseCase(useCase);
            jsonString = logDetail.convertToJSON(logDetail); 
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return jsonString;
    }
    
    public static String prepareLogDetailForTxnStatusResponse(TransactionStatusRequest request, String country, String interOpRefId, 
            String eventType, TransactionStatusResponse response, Exception exception) {
        String jsonString = "";
        try {
            LogDetail logDetail =  prepareLogResponse(country, eventType, interOpRefId,ServiceTypes.GET_TRANSACTION_STATUS.toString(),
                    request.getLang(), null, null);
            setResponse(exception, logDetail, "");
            logDetail.setParameters(response.convertToJSON(response));
            jsonString = logDetail.convertToJSON(logDetail); 
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return jsonString;
    }
    
    public static String prepareLogDetailForListOfTxnStatusRequest(String country, String interOpRefId, String eventType, String useCase,String lang, String extOrgRefId, String startDate, String endDate,
            Integer offset, Integer limit) {
        String jsonString = LogConstants.EMPTY_STRING.getValue();
        try {
            LogDetail logDetail = prepareRequest(country, interOpRefId, eventType);
            logDetail.setLang(lang);
             prepareLogDetailDynamicValues(logDetail, null, LogConstants.EMPTY_STRING.getValue(),
                        LogConstants.EMPTY_STRING.getValue(),
                        null, extOrgRefId, null);
            logDetail.setParameters(prepareListOfTxnRequestString(lang, extOrgRefId, startDate, endDate, offset, limit));
            logDetail.setUseCase(useCase);
            jsonString = logDetail.convertToJSON(logDetail); 
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return jsonString;
    }
    
    public static String prepareLogDetailForListOfTxnStatusResponse(
            String eventType, TransactionStatusResponse response, Exception exception) {
        String jsonString = "";
        try {
            LogDetail logDetail =  prepareLogResponse(null, eventType, null,ServiceTypes.GET_TRANSACTIONS.toString(),
                    null, null, null);
            setResponse(exception, logDetail, "");
            logDetail.setParameters(response.convertToJSON(response));
            jsonString = logDetail.convertToJSON(logDetail); 
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return jsonString;
    }
    
    public static String prepareLogDetailForReceiveTxnRequest(ReceiveTransactionRequest request, String country, String interOpRefId, String eventType, String useCase) {
        String jsonString = LogConstants.EMPTY_STRING.getValue();
        try {
            LogDetail logDetail = prepareRequest(country, interOpRefId, eventType);
            logDetail.setParameters(request.convertToJSON(request));
            logDetail.setUseCase(useCase);
            jsonString = logDetail.convertToJSON(logDetail); 
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return jsonString;
    }
    
    public static String prepareLogDetailForReceiveTxnResponse(String country, String interOpRefId, 
            String eventType, ReceiveTransactionResponse response, Exception exception) {
        String jsonString = "";
        try {
            LogDetail logDetail =  prepareLogResponse(country, eventType, interOpRefId,ServiceTypes.RECEIVE_TRANSACTION.toString(),
                    null, null, null);
            setResponse(exception, logDetail, "");
            logDetail.setParameters(response.convertToJSON(response));
            jsonString = logDetail.convertToJSON(logDetail); 
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return jsonString;
    }
    
    public static String prepareLogDetailForPendingTxnRequest(PendingTransactionRequest request, String country, String interOpRefId, String eventType, String useCase) {
        String jsonString = LogConstants.EMPTY_STRING.getValue();
        try {
            LogDetail logDetail = prepareRequest(country, interOpRefId, eventType);
            logDetail.setLang(request.getLang());
             prepareLogDetailDynamicValues(logDetail, LogConstants.EMPTY_STRING.getValue(), request.getMsisdn(), LogConstants.EMPTY_STRING.getValue(), LogConstants.EMPTY_STRING.getValue(), LogConstants.EMPTY_STRING.getValue(), LogConstants.EMPTY_STRING.getValue());
             logDetail.setLang(request.getLang());
            logDetail.setParameters(request.convertToJSON(request));
            logDetail.setUseCase(useCase);
            jsonString = logDetail.convertToJSON(logDetail); 
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return jsonString;
    }
    
    public static String prepareLogDetailForPendingTransactionsResponse(PendingTransactionRequest request, String country, String interOpRefId, String eventType,
	        PendingTransactionsResponse response, Exception exception) {
        String jsonString = LogConstants.EMPTY_STRING.getValue();
        try {
            if(null == response) {
                response = new PendingTransactionsResponse();
            }
            LogDetail logDetail =  prepareLogResponse(country, eventType, interOpRefId,ServiceTypes.PENDING_TRANSACTIONS.toString(),
                    request.getLang(), LogConstants.EMPTY_STRING.getValue(), LogConstants.EMPTY_STRING.getValue());
            setResponse(exception, logDetail,LogConstants.EMPTY_STRING.getValue());
            logDetail.setParameters(response.convertToJSON(response));
            jsonString = logDetail.convertToJSON(logDetail); 
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return jsonString;
    }
    
    public static String prepareLogDetailForConfirmTxnRequest(ConfirmTransactionRequest request, String country, String interOpRefId, String eventType, String useCase) {
        String jsonString = LogConstants.EMPTY_STRING.getValue();
        try {
            LogDetail logDetail = prepareRequest(country, interOpRefId, eventType);
            logDetail.setLang(request.getLang());
             prepareLogDetailDynamicValues(logDetail, LogConstants.EMPTY_STRING.getValue(), LogConstants.EMPTY_STRING.getValue(), 
            		 LogConstants.EMPTY_STRING.getValue(), LogConstants.EMPTY_STRING.getValue(), LogConstants.EMPTY_STRING.getValue(), LogConstants.EMPTY_STRING.getValue());
             logDetail.setLang(request.getLang());
            logDetail.setParameters(request.convertToJSON(request));
            logDetail.setUseCase(useCase);
            jsonString = logDetail.convertToJSON(logDetail); 
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return jsonString;
    }
    
    public static String prepareLogDetailForConfirmTxnResponse(ConfirmTransactionRequest request, String country, String interOpRefId, String eventType,
	        ConfirmTransactionResponse response, Exception exception) {
        String jsonString = LogConstants.EMPTY_STRING.getValue();
        try {
            if(null == response) {
                response = new ConfirmTransactionResponse();
            }
            LogDetail logDetail =  prepareLogResponse(country, eventType, interOpRefId,ServiceTypes.ACTION_ON_TRANSACTION_CONFIRMATION.toString(),
                    request.getLang(), LogConstants.EMPTY_STRING.getValue(), LogConstants.EMPTY_STRING.getValue());
            setResponse(exception, logDetail,LogConstants.EMPTY_STRING.getValue());
            logDetail.setParameters(response.convertToJSON(response));
            jsonString = logDetail.convertToJSON(logDetail); 
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return jsonString;
    }
    
    public static String prepareLogDetailForNetworkMessageRequest(NetworkMessageRequest networkMessageRequest, String country, String interOpRefId, String eventType) {
        String jsonString = LogConstants.EMPTY_STRING.getValue();
        try {
            LogDetail logDetail = prepareRequest(country, interOpRefId, eventType);
            logDetail.setParameters(networkMessageRequest.convertToJSON(networkMessageRequest));
            jsonString = logDetail.convertToJSON(logDetail); 
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return jsonString;
    }
    
    public static String prepareLogDetailForNetworkMessageResponse(String language,String country, String userId, 
            String eventType, NetworkMessageResponse response, Exception exception) {
        String jsonString = "";
        try {
            LogDetail logDetail =  prepareLogResponse(country, eventType, userId,ServiceTypes.NETWORK_MESSAGE.toString(),
            		language, null, null);
            setResponse(exception, logDetail, "");
            logDetail.setParameters(response.convertToJSON(response));
            jsonString = logDetail.convertToJSON(logDetail); 
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return jsonString;
    }
}
