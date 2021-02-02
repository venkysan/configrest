package com.comviva.interop.txnengine.model;

import java.util.HashMap;
import java.util.Map;

/**
 * EntityCodeMap contains the map of entity and corresponding codes and mapped
 * code map
 *
 */
public class EntityCodeMap {

    private Map<String, StatusCodeMap> entityMap;

    private Map<String, String> mappingCodes;

    /**
     * This method returns the entity map which contains the entity and
     * corresponding status codes
     * 
     * @return
     */
    public Map<String, StatusCodeMap> getEntityMap() {
        return entityMap;
    }


    /**
     * EntityCodeMap constructor to initialize the entity map and mapping codes
     */
    public EntityCodeMap() {
        this.entityMap = new HashMap<>();
        this.mappingCodes = new HashMap<>();
    }

    public StatusCodeMap getMap(String s) {
        return entityMap.get(s);
    }

    public Map<String, String> getMappingCodes() {
        return mappingCodes;
    }

}