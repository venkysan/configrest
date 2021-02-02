package com.comviva.interop.txnengine.model;

import java.util.HashMap;
import java.util.Map;

public class StatusCodeMap {

    private Map<String, CodeDescriptionMap> codeMap;

    public Map<String, CodeDescriptionMap> getStatusCodeMap() {
        return codeMap;
    }

    public StatusCodeMap() {
        this.codeMap = new HashMap<>();
    }

    public CodeDescriptionMap getMap(String s) {
        return codeMap.get(s);
    }

}