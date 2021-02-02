package com.comviva.interop.txnengine.request.validations;

import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comviva.interop.txnengine.configuration.ServiceResources;
import com.comviva.interop.txnengine.configuration.StatusCodesLoader;
import com.comviva.interop.txnengine.enums.ValidationErrors;
import com.comviva.interop.txnengine.model.CodeDescriptionMap;
import com.comviva.interop.txnengine.model.EntityCodeMap;

@Service
public class GetDescriptionForCode {
    private EntityCodeMap entityCodeMap;
    private ServiceResources serviceResources;
    private StatusCodesLoader statusCodesLoader;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    public void setEntityCodeMap(EntityCodeMap entityCodeMap) {
        writeLock.lock();
        try {
            this.entityCodeMap = entityCodeMap;
        } finally {
            writeLock.unlock();
        }
    }

    @Autowired
    public GetDescriptionForCode(ServiceResources serviceResources, StatusCodesLoader statusCodesLoader) {
        this.serviceResources = serviceResources;
        this.statusCodesLoader = statusCodesLoader;
        this.entityCodeMap = statusCodesLoader.loadStatusCodes();
    }

    public String getDescription(String entity, String statusCode, String language) {
        readLock.lock();
        try {

            if (ValidationErrors.INVALID_LANGUAGE.getStatusCode().equals(statusCode)) {
                return serviceResources.getInvalidLanguageMessage();
            }

            if (null == entity || "".equals(entity) || null == entityCodeMap.getMap(entity)) {
                return serviceResources.getNoDescriptionForGivenStatusCode();
            }

            CodeDescriptionMap codeDescriptionMap = entityCodeMap.getMap(entity).getMap(statusCode);

            if (null == codeDescriptionMap) {
                return serviceResources.getNoDescriptionForGivenStatusCode();
            }

            String description = codeDescriptionMap
                    .getDescription("".equals(language) ? serviceResources.getDefaultLanguage() : language);

            if (null == description) {
                return serviceResources.getNoDescriptionInGivenLanguage();
            }

            return description;
        } finally {
            readLock.unlock();
        }
    }

    public void updateStatusCodes() {
        this.setEntityCodeMap(statusCodesLoader.loadStatusCodes());

    }

    public String getMappingCode(String statusCode) {
        readLock.lock();
        try {
            String mappedCode = entityCodeMap.getMappingCodes().get(statusCode);
            if (Optional.ofNullable(mappedCode).isPresent()) {
                return mappedCode;
            } else {
                return serviceResources.getNoMappingCodeForGivenStatusCode();
            }
        } finally {
            readLock.unlock();
        }
    }
}
