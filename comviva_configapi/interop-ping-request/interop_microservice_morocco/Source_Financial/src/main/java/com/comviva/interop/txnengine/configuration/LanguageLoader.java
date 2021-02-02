package com.comviva.interop.txnengine.configuration;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comviva.interop.txnengine.entities.Languages;
import com.comviva.interop.txnengine.enums.LogConstants;
import com.comviva.interop.txnengine.repositories.LanguagesRepository;
import com.comviva.interop.txnengine.util.LoggerUtil;

@Component
public class LanguageLoader {

    private LanguagesRepository languagesRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(LanguageLoader.class);

    @Autowired
    public LanguageLoader(LanguagesRepository languagesRepository) {
        this.languagesRepository = languagesRepository;
    }

    public Set<String> loadLanguageCodes() {
        String message = LoggerUtil.printLog(LogConstants.LOADING_LANGUAGE_CODES_EVENT.getValue(), null, LogConstants.LOADING_LANGUAGE_CODES_START_USE_CASE.getValue());
        LOGGER.info("Loading language codes...{}" ,message);
        return languagesRepository.getAllLangCodes();
    }

    public List<Languages> getAllLanguages() {
        String message = LoggerUtil.printLog(LogConstants.LOADING_LANGUAGE_CODES_EVENT.getValue(), null, LogConstants.LOADING_LANGUAGE_CODES_START_USE_CASE.getValue());
        LOGGER.info("Loading language codes...{}" ,message);
        return languagesRepository.getAllLanguages();
    }

}