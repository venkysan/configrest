package com.comviva.interop.txnengine.strategies;

import java.util.EnumMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.comviva.interop.txnengine.enums.ServiceTypes;
import com.comviva.interop.txnengine.services.ExecutableServices;

/**
 * <pre>
 * This Class contains map of key: serviceType - value: service bean for a specific
 * service category.
 * e.g. financial, non-financial, etc. add the service bean along with service type in
 * the map for Non Financial service class autowired in ExecutableServicesMap.java for use.
 * For other service category create a new class similar to this one and autowire it in ExecutableServicesMap.java.
 * </pre>
 */
@Component("FinancialServicesMap")
public class FinancialServicesMap {

    private EnumMap<ServiceTypes, ExecutableServices> serviceMap;

    @Autowired
    public FinancialServicesMap(@Qualifier("CreateTransactionService") ExecutableServices createTransactionService,
            @Qualifier("ReceiveTransactionService") ExecutableServices receiveTransactionService,
            @Qualifier("TransactionQuotationService") ExecutableServices transactionQuotationService,
            @Qualifier("PendingTransactionService") ExecutableServices pendingTransactionService,
            @Qualifier("ConfirmTransactionService") ExecutableServices confirmTransactionService) {
        this.serviceMap = new EnumMap<>(ServiceTypes.class);
        serviceMap.put(ServiceTypes.CREATE_TRANSACTION, createTransactionService);
        serviceMap.put(ServiceTypes.RECEIVE_TRANSACTION, receiveTransactionService);
        serviceMap.put(ServiceTypes.TRANSACTION_QUOTATION, transactionQuotationService);
        serviceMap.put(ServiceTypes.PENDING_TRANSACTIONS, pendingTransactionService);
        serviceMap.put(ServiceTypes.ACTION_ON_TRANSACTION_CONFIRMATION, confirmTransactionService);
    }

    public Map<ServiceTypes, ExecutableServices> getServiceMap() {
        return serviceMap;
    }

}