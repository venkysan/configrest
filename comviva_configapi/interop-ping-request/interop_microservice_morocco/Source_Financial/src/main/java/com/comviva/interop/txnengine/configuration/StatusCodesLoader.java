package com.comviva.interop.txnengine.configuration;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comviva.interop.txnengine.entities.StatusCodes;
import com.comviva.interop.txnengine.enums.LogConstants;
import com.comviva.interop.txnengine.model.CodeDescriptionMap;
import com.comviva.interop.txnengine.model.EntityCodeMap;
import com.comviva.interop.txnengine.model.StatusCodeMap;
import com.comviva.interop.txnengine.repositories.StatusCodesRepository;
import com.comviva.interop.txnengine.util.LoggerUtil;

@Service
public class StatusCodesLoader {

    private StatusCodesRepository statusCodesRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(StatusCodesLoader.class);

    @Autowired
    public StatusCodesLoader(StatusCodesRepository statusCodesRepository) {
        this.statusCodesRepository = statusCodesRepository;
    }

    public EntityCodeMap loadStatusCodes() {
        List<StatusCodes> list = statusCodesRepository.getAllCodes();
        EntityCodeMap codeMap = new EntityCodeMap();
        for (StatusCodes statusCode : list) {
            if (!codeMap.getEntityMap().containsKey(statusCode.getEntity())) {
                codeMap.getEntityMap().put(statusCode.getEntity(), new StatusCodeMap());
            }
            if (!codeMap.getEntityMap().get(statusCode.getEntity()).getStatusCodeMap()
                    .containsKey(statusCode.getStatuscode())) {
                codeMap.getEntityMap().get(statusCode.getEntity()).getStatusCodeMap().put(statusCode.getStatuscode(),
                        new CodeDescriptionMap());
            }

            codeMap.getEntityMap().get(statusCode.getEntity()).getStatusCodeMap().get(statusCode.getStatuscode())
                    .getDescriptionMap().put(statusCode.getLangCode(), statusCode.getDescription());
            codeMap.getMappingCodes().put(statusCode.getStatuscode(), statusCode.getMappingCode());
        }
        String message = LoggerUtil.printLog(LogConstants.LOADING_STATUS_CODES_EVENT.getValue(), null, LogConstants.LOADING_STATUS_CODES_COMPLETED_USE_CASE.getValue());
        LOGGER.info("Done loading data from StatusCodes table....{}" ,message);
        return codeMap;
    }

}