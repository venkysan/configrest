package com.comviva.interop.txnengine.exception;

public class InteropException extends RuntimeException {

    private static final long serialVersionUID = 1769770938629825208L;

    private final String statusCode;
    private final String entity;
    private final String mappedCode;
    private final String message;

    public String getMappedCode() {
        return mappedCode;
    }

    public String getEntity() {
        return entity;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public String getMessage(){ return message; }

    public InteropException(String statusCode, String entity) {
        super();
        this.statusCode = statusCode;
        this.entity = entity;
        this.mappedCode = null;
        this.message = null;
    }

    public InteropException(String statusCode, String entity, String mappedCode) {
        super();
        this.statusCode = statusCode;
        this.entity = entity;
        this.mappedCode = mappedCode;
        this.message = null;
    }

    public InteropException(String statusCode, String entity, String mappedCode, String message) {
        super();
        this.statusCode = statusCode;
        this.entity = entity;
        this.mappedCode = mappedCode;
        this.message = message;
    }
}