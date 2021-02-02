package com.comviva.interop.txnengine.controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import com.comviva.interop.txnengine.api.ReportsApi;
import com.comviva.interop.txnengine.configuration.BrokerServiceURLProperties;
import com.comviva.interop.txnengine.configuration.Resource;

import com.comviva.interop.txnengine.entities.ChannelUserDetails;
import com.comviva.interop.txnengine.entities.InteropTransactionDetails;
import com.comviva.interop.txnengine.enums.LogConstants;
import com.comviva.interop.txnengine.exception.InteropException;
import com.comviva.interop.txnengine.enums.Constants;
import com.comviva.interop.txnengine.model.OrangeMoneyOperationDetail;
import com.comviva.interop.txnengine.model.OrangeMoneyOperations;
import com.comviva.interop.txnengine.model.OrangeMoneyTechnicalAccountDetail;
import com.comviva.interop.txnengine.model.OrangeMoneyTechnicalAccounts;
import com.comviva.interop.txnengine.repositories.ChannelUserDetailsRepository;
import com.comviva.interop.txnengine.util.LoggerUtil;
import com.comviva.interop.txnengine.util.StringUtils;

@Controller
public class ReportsApiController implements ReportsApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportsApiController.class);
    
    @Autowired
    private ChannelUserDetailsRepository channelUserDetailsRepository;
    
    @Autowired
    private Resource resource;
    
    @PersistenceContext
    @Autowired
    private EntityManager entityManager;
    
    @Autowired
    private BrokerServiceURLProperties brokerServiceURLProperties;
    
    
    @Override
    public ResponseEntity<OrangeMoneyOperations> getOrangeMoneyOperations(String countryId, String from, int limit) {
        
        String message = LoggerUtil.prepareLogDetailForOMOperationsRequest(LogConstants.INCOMING_REQUEST_EVENT_TYPE.getValue(), countryId, from, limit);
        LOGGER.info("OM operations request: {}", message);
        OrangeMoneyOperations orangeMoneyOperations = new OrangeMoneyOperations();
        List<OrangeMoneyOperationDetail> orangeMoneyOperationDetailsList = new ArrayList<>();
        try {
            if(brokerServiceURLProperties.getUrlCountryIdValue().equals(countryId)) {
                TypedQuery<InteropTransactionDetails> typedQuery = entityManager.createQuery(getDynamicQuery(), InteropTransactionDetails.class);
                typedQuery.setParameter("from", StringUtils.stringToDateFormat(from, resource.getInputDateFormat()));
                typedQuery.setMaxResults(limit);
                prepareTxnDetails(orangeMoneyOperationDetailsList, typedQuery.getResultList());
        }
        orangeMoneyOperations.setData(orangeMoneyOperationDetailsList);    
        String responseMessage = LoggerUtil.prepareLogDetailForOMOperationResponse(LogConstants.OUTGOING_RESPONSE_EVENT_TYPE.getValue(), orangeMoneyOperations,countryId, null);
        LOGGER.info("OM operations response: {}", responseMessage);
        return new ResponseEntity<>(orangeMoneyOperations, HttpStatus.OK);
        }catch(InteropException ex) {
            String exceptionMessage = LoggerUtil.prepareLogDetailForOMOperationResponse(LogConstants.OUTGOING_RESPONSE_EVENT_TYPE.getValue(), orangeMoneyOperations, countryId, ex);
            LOGGER.info("OM OPERATIONS response: {}", exceptionMessage);
            return new ResponseEntity<>(orangeMoneyOperations, HttpStatus.OK);
        }
        catch(Exception e) {
            String exceptionMessage = LoggerUtil.prepareLogDetailForOMOperationResponse(LogConstants.OUTGOING_RESPONSE_EVENT_TYPE.getValue(), orangeMoneyOperations, countryId, e);
            LOGGER.info("OM OPERATIONS response: {}", exceptionMessage);
            return new ResponseEntity<>(orangeMoneyOperations, HttpStatus.OK);
        }


    }

    private  List<OrangeMoneyOperationDetail> prepareTxnDetails(List<OrangeMoneyOperationDetail> orangeMoneyOperationDetailsList, List<InteropTransactionDetails> interopTransactions) {
    	 if(null != interopTransactions && !interopTransactions.isEmpty()) {
             for(InteropTransactionDetails interopTransactionDetails: interopTransactions) {
                 OrangeMoneyOperationDetail orangeMoneyOperationDetail = new OrangeMoneyOperationDetail();
                 orangeMoneyOperationDetail.setAddonStatus(interopTransactionDetails.getTxnStatus());
                 orangeMoneyOperationDetail.setAmount(interopTransactionDetails.getAmount().doubleValue());
                 orangeMoneyOperationDetail.setCreationDate(interopTransactionDetails.getCreatedDate().toString());
                 orangeMoneyOperationDetail.setCurrency(interopTransactionDetails.getCurrency());
                 orangeMoneyOperationDetail.setPayeeMsisdn(interopTransactionDetails.getThirdPartyPayee());
                 orangeMoneyOperationDetail.setPayeePayId(interopTransactionDetails.getPayeePayId());
                 orangeMoneyOperationDetail.setPayeeProvider(interopTransactionDetails.getPayeeProvider());
                 orangeMoneyOperationDetail.setPayeeTechnical(interopTransactionDetails.getIsPayeeTechnical().equals(Constants.ZERO.getValue()) ? Boolean.FALSE : Boolean.TRUE);
                 orangeMoneyOperationDetail.setPayerChannelUserCode(interopTransactionDetails.getPayerChannelUserCode());
                 orangeMoneyOperationDetail.setPayerMsisdn(interopTransactionDetails.getThirdPartyPayer());
                 orangeMoneyOperationDetail.setPayerPayId(interopTransactionDetails.getPayerPayId());
                 orangeMoneyOperationDetail.setPayerProvider(interopTransactionDetails.getPayerProvider());
                 orangeMoneyOperationDetail.setPayerTechnical(interopTransactionDetails.getIsPayerTechnical().equals(Constants.ZERO.getValue() ) ? Boolean.FALSE: Boolean.TRUE);
                 orangeMoneyOperationDetail.setServiceType(interopTransactionDetails.getThirdPartyTxnType());
                 orangeMoneyOperationDetail.setTxnDate(interopTransactionDetails.getCreatedDate().toString());
                 orangeMoneyOperationDetail.setTxnId(interopTransactionDetails.getThirdPartyRefId());
                 orangeMoneyOperationDetail.setTxnMode(interopTransactionDetails.getTxnMode());
                 orangeMoneyOperationDetail.setTxnStatus(interopTransactionDetails.getTxnStatus());
                 orangeMoneyOperationDetailsList.add(orangeMoneyOperationDetail);    
             }
         }
    	 return orangeMoneyOperationDetailsList;
    }
    @Override
    public ResponseEntity<OrangeMoneyTechnicalAccounts> getTechnicalWallets(String countryId) {
        String message = LoggerUtil.prepareLogDetailForTechnicalWalletsRequest(LogConstants.INCOMING_REQUEST_EVENT_TYPE.getValue(), countryId);
        LOGGER.info("Technical wallet request: {}", message);
        OrangeMoneyTechnicalAccounts orangeMoneyTechnicalAccounts = new OrangeMoneyTechnicalAccounts();
        List<OrangeMoneyTechnicalAccountDetail> orangeMoneyTechnicalAccountDetailsList = new ArrayList<>();
        try {
            if(brokerServiceURLProperties.getUrlCountryIdValue().equals(countryId)) {
                Iterator<ChannelUserDetails> channelUserDetailsIterable = channelUserDetailsRepository.findAll().iterator();
                while (channelUserDetailsIterable.hasNext()) {
                    ChannelUserDetails chUserDetails = channelUserDetailsIterable.next();
                    OrangeMoneyTechnicalAccountDetail orangeMoneyTechnicalAccountDetail = new OrangeMoneyTechnicalAccountDetail();
                    orangeMoneyTechnicalAccountDetail.setChannelUserCode(chUserDetails.getChannelUserCode());
                    orangeMoneyTechnicalAccountDetail.setCreationDate(chUserDetails.getCreatedDate().toString());
                    orangeMoneyTechnicalAccountDetail.setMsisdn(chUserDetails.getMsisdn());
                    orangeMoneyTechnicalAccountDetail.setDescription(chUserDetails.getDescription());
                    orangeMoneyTechnicalAccountDetail.setOptional(chUserDetails.getOptional());
                    orangeMoneyTechnicalAccountDetail.setType(chUserDetails.getType());
                    orangeMoneyTechnicalAccountDetailsList.add(orangeMoneyTechnicalAccountDetail);
                }   
            }
            orangeMoneyTechnicalAccounts.setData(orangeMoneyTechnicalAccountDetailsList);
            String responseMessage = LoggerUtil.prepareLogDetailForTechnicalWalletsResponse(LogConstants.OUTGOING_RESPONSE_EVENT_TYPE.getValue(), orangeMoneyTechnicalAccounts, countryId, null);
            LOGGER.info("Technical wallets response: {}", responseMessage);
            return new ResponseEntity<>(orangeMoneyTechnicalAccounts, HttpStatus.OK);
        }
        catch(Exception e) {
            String responseMessage = LoggerUtil.prepareLogDetailForTechnicalWalletsResponse(LogConstants.OUTGOING_RESPONSE_EVENT_TYPE.getValue(), orangeMoneyTechnicalAccounts, countryId, e);
            LOGGER.info("Technical wallets response: {}", responseMessage);
            return new ResponseEntity<>(orangeMoneyTechnicalAccounts, HttpStatus.OK);
        }
    }
    
    private String getDynamicQuery() {
        StringBuilder dynamicQuery = new StringBuilder();
        dynamicQuery.append("SELECT t FROM InteropTransactionDetails t where t.createdDate >= :from order by createdDate");       
        return dynamicQuery.toString();
    }
    

}
