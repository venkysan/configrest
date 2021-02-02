package com.comviva.interop.txnengine.strategies;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.comviva.interop.txnengine.enums.ServiceTypes;
import com.comviva.interop.txnengine.model.Request;

/**
 * Class used to create interopReferenceId and execute the service class based
 * on serviceType execute method of service class is Async
 */
@Component
public class ExecutableServicesMapper {

    
    private ExecutableServicesMap executableServicesMap;
    private static final int INDEX_OF_FIRST_DASH_TO_REMOVE = 13;
    private static final int INDEX_OF_SECOND_DASH_TO_REMOVE = 22;
    private static final int INDEX_OF_DASH_TO_INSERT = 26;

    @Autowired
    public ExecutableServicesMapper(@Qualifier("ExecutableServicesMap") ExecutableServicesMap executableServicesMap) {
        this.executableServicesMap = executableServicesMap;
    }

    public String executeService(Request request, ServiceTypes serviceType, String interOpReferenceId) {
        request.setInteropReferenceId(interOpReferenceId);

        // First getting the service map from executableServicesMap based on service
        // category then getting the service bean from service map based on service type
        // finally calling execute method (Async call)
        executableServicesMap.getServicesMap(serviceType.getServiceCategory()).get(serviceType).execute(request);
        return interOpReferenceId;
    }

    public String generateInteropReferenceId() {
        StringBuilder sb = new StringBuilder(UUID.randomUUID().toString());
        sb.deleteCharAt(INDEX_OF_FIRST_DASH_TO_REMOVE);
        sb.deleteCharAt(INDEX_OF_SECOND_DASH_TO_REMOVE);
        sb.insert(INDEX_OF_DASH_TO_INSERT, '-');
        return sb.toString();

    }

}