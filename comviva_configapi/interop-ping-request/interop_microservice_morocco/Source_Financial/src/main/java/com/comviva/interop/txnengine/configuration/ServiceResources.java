package com.comviva.interop.txnengine.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties
public class ServiceResources {

    @Value("${no.description.for.given.status.code:No description present}")
    private String noDescriptionForGivenStatusCode;

    @Value("${no.description.in.given.language:No description present in given language}")
    private String noDescriptionInGivenLanguage;

    @Value("${invalid.language.message:Invalid Language}")
    private String invalidLanguageMessage;

    @Value("${default.language:en}")
    private String defaultLanguage;

    @Value("${no.mapping.code.found.for.given.status.code:No mapping code present}")
    private String noMappingCodeForGivenStatusCode;

    public String getNoDescriptionForGivenStatusCode() {
        return noDescriptionForGivenStatusCode;
    }

    public String getNoDescriptionInGivenLanguage() {
        return noDescriptionInGivenLanguage;
    }

    public String getInvalidLanguageMessage() {
        return invalidLanguageMessage;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public String getNoMappingCodeForGivenStatusCode() {
        return noMappingCodeForGivenStatusCode;
    }
}
