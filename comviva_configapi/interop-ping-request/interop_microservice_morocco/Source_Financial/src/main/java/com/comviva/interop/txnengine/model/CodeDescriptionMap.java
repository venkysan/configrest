package com.comviva.interop.txnengine.model;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

/**
 * CodeDescriptionMap holds the code and description map
 *
 */
@Getter
public class CodeDescriptionMap {


    /**
     * descriptionMap is used to hold the code and description
     */
    private Map<String, String> descriptionMap;

    /**
     * CodeDescriptionMap constructor to initialize the map
     */
    public CodeDescriptionMap() {
        descriptionMap = new HashMap<>();
    }

    /**
     * @param s is the status code
     * @return description of given code
     */
    public String getDescription(String s) {
        return descriptionMap.get(s);
    }

}