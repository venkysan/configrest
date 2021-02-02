package com.comviva.interop.txnengine.strategies;

import java.util.EnumMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.comviva.interop.txnengine.enums.ServiceCategories;
import com.comviva.interop.txnengine.enums.ServiceTypes;
import com.comviva.interop.txnengine.services.ExecutableServices;

/**
 * class contains map of key: service categories - value: map of services they
 * contain. add an entry here when implementing an service map for any service
 * category
 */
@Component("ExecutableServicesMap")
public class ExecutableServicesMap {
    private EnumMap<ServiceCategories, Map<ServiceTypes, ExecutableServices>> servicesMap;

    @Autowired
    public ExecutableServicesMap(@Qualifier("FinancialServicesMap") FinancialServicesMap financialServicesMap) {
        servicesMap = new EnumMap<>(ServiceCategories.class);
        servicesMap.put(ServiceCategories.FINANCIAL, financialServicesMap.getServiceMap());
    }

    public Map<ServiceTypes, ExecutableServices> getServicesMap(ServiceCategories serviceCategories) {
        return servicesMap.get(serviceCategories);
    }

}