package com.comviva.interop.txnengine.model;

import java.sql.Timestamp;
import java.util.Optional;

import com.comviva.interop.txnengine.enums.LogConstants;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonPropertyOrder({ "dateTime", "level", "sessionId", "country", "currency", "apiKey", "msisdn",
        "msisdn1", "amount", "requestId", "eventType", "peer", "parameters",
        "returnedStatus", "useCase", "lang", "errorDetails" })
public class LogDetail {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy@HH:mm:ss")
    private Timestamp dateTime;
    
    private String level;
    
    private String sessionId;
    
    private String country;
    
    private String currency;
    
    private String apiKey;
    
    private String msisdn;
    
    private String msisdn1;
    
    private String amount;
    
    private String requestId;
    
    private String eventType;
    
    private String peer;
    
    private String parameters;
    
    private String returnedStatus;
    
    private String useCase;
    
    private String lang;
    
    private String errorDetails;

    public Timestamp getDateTime() {
        return new Timestamp(dateTime.getTime());
    }



    public void setDateTime(Timestamp dateTime) {
        this.dateTime = new Timestamp(dateTime.getTime());
    }



    public String getLevel() {
        return Optional.ofNullable(level).isPresent() ? level : LogConstants.EMPTY_STRING.getValue();
    }



    public void setLevel(String level) {
        this.level = level;
    }



    public String getSessionId() {
        return Optional.ofNullable(sessionId).isPresent() ? sessionId : LogConstants.EMPTY_STRING.getValue();
    }



    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }



    public String getCountry() {
        return Optional.ofNullable(country).isPresent() ? country : LogConstants.EMPTY_STRING.getValue();
    }



    public void setCountry(String country) {
        this.country = country;
    }



    public String getCurrency() {
        return Optional.ofNullable(currency).isPresent() ? currency : LogConstants.EMPTY_STRING.getValue();
    }



    public void setCurrency(String currency) {
        this.currency = currency;
    }



    public String getApiKey() {
        return Optional.ofNullable(apiKey).isPresent() ? apiKey : LogConstants.EMPTY_STRING.getValue();
    }



    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }



    public String getMsisdn() {
        return Optional.ofNullable(msisdn).isPresent() ? msisdn : LogConstants.EMPTY_STRING.getValue();
    }



    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }



    public String getMsisdn1() {
        return Optional.ofNullable(msisdn1).isPresent() ? msisdn1 : LogConstants.EMPTY_STRING.getValue();
    }



    public void setMsisdn1(String msisdn1) {
        this.msisdn1 = msisdn1;
    }



    public String getAmount() {
        return Optional.ofNullable(amount).isPresent() ? amount : LogConstants.EMPTY_STRING.getValue();
    }



    public void setAmount(String amount) {
        this.amount = amount;
    }



    public String getRequestId() {
        return Optional.ofNullable(requestId).isPresent() ? requestId : LogConstants.EMPTY_STRING.getValue();
    }



    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }



    public String getEventType() {
        return Optional.ofNullable(eventType).isPresent() ? eventType : LogConstants.EMPTY_STRING.getValue();
    }



    public void setEventType(String eventType) {
        this.eventType = eventType;
    }



    public String getPeer() {
        return Optional.ofNullable(peer).isPresent() ? peer : LogConstants.EMPTY_STRING.getValue();
    }



    public void setPeer(String peer) {
        this.peer = peer;
    }



    public String getParameters() {
        return Optional.ofNullable(parameters).isPresent() ? parameters : LogConstants.EMPTY_STRING.getValue();
    }



    public void setParameters(String parameters) {
        this.parameters = parameters;
    }



    public String getReturnedStatus() {
        return Optional.ofNullable(returnedStatus).isPresent() ? returnedStatus : LogConstants.EMPTY_STRING.getValue();
    }



    public void setReturnedStatus(String returnedStatus) {
        this.returnedStatus = returnedStatus;
    }



    public String getUseCase() {
        return Optional.ofNullable(useCase).isPresent() ? useCase : LogConstants.EMPTY_STRING.getValue();
    }



    public void setUseCase(String useCase) {
        this.useCase = useCase;
    }



    public String getLang() {
        return Optional.ofNullable(lang).isPresent() ? lang : LogConstants.EMPTY_STRING.getValue();
    }



    public void setLang(String lang) {
        this.lang = lang;
    }



    public String getErrorDetails() {
        return Optional.ofNullable(errorDetails).isPresent() ? errorDetails : LogConstants.EMPTY_STRING.getValue();
    }



    public void setErrorDetails(String errorDetails) {
        this.errorDetails = errorDetails;
    }



    public String convertToJSON(LogDetail auditDetail) throws JsonProcessingException {
        return new ObjectMapper().writer(new DefaultPrettyPrinter()).writeValueAsString(auditDetail);
    }
}