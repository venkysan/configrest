package com.comviva.interop.txnengine.mobiquity.service.handlers;

import java.util.Optional;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import com.comviva.interop.txnengine.configuration.BrokerServiceURLProperties;
import com.comviva.interop.txnengine.enums.InteropResponseCodes;
import com.comviva.interop.txnengine.enums.LogConstants;
import com.comviva.interop.txnengine.enums.Sources;
import com.comviva.interop.txnengine.enums.ThirdPartyResponseCodes;
import com.comviva.interop.txnengine.events.TransactionQuotationResponseEvent;
import com.comviva.interop.txnengine.exception.InteropException;
import com.comviva.interop.txnengine.model.QuotationRequest;
import com.comviva.interop.txnengine.model.QuotationResponse;
import com.comviva.interop.txnengine.model.Response;
import com.comviva.interop.txnengine.model.WalletResponse;
import com.comviva.interop.txnengine.request.validations.GetDescriptionForCode;
import com.comviva.interop.txnengine.util.CastUtils;
import com.comviva.interop.txnengine.util.LoggerUtil;

@Service
public class GetFeeHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetFeeHandler.class);
    
    @Autowired
    private BrokerServiceURLProperties brokerServiceURLProperties;

    @Autowired
    private GetDescriptionForCode getDescriptionForCode;
    
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private GetFeeService mobiquityGetFee;

    @Autowired
    public GetFeeHandler(GetFeeService mobiquityGetFee, ApplicationEventPublisher applicationEventPublisher,
            GetDescriptionForCode getDescriptionForCode, BrokerServiceURLProperties brokerServiceURLProperties) {
        super();
        this.mobiquityGetFee = mobiquityGetFee;
        this.applicationEventPublisher = applicationEventPublisher;
        this.getDescriptionForCode = getDescriptionForCode;
        this.brokerServiceURLProperties = brokerServiceURLProperties;
    }

    @Async("ThirdPartyCallsAsyncPool")
    public void execute(boolean isP2POnUs, QuotationRequest req, String interopReferenceId) {
        try {
            Response feeResponse = mobiquityGetFee.execute(isP2POnUs, req, interopReferenceId);
            if (!Optional.ofNullable(feeResponse.getMappingResponse()).isPresent()) {
                throw new InteropException(InteropResponseCodes.READ_TIMEOUT_FROM_BROKER.getStatusCode(),
                        Sources.BROKER.toString());
            } else if (ThirdPartyResponseCodes.SUCCESS.getMappedCode()
                    .equals(feeResponse.getMappingResponse().getMappingCode())) {
                QuotationResponse quotationResponse = getQuotationResponse(interopReferenceId, req.getExtOrgRefId(),
                        feeResponse);
                this.publishResponseEvent(quotationResponse, interopReferenceId, req, null);
            } else {
                throw new InteropException(feeResponse.getBrokerResponse().getBrokerCode(), Sources.BROKER.toString(),
                        feeResponse.getMappingResponse().getMappingCode(),feeResponse.getWalletResponse().getMessage());
            }
        } catch (InteropException e) {
            String description = null;
            if(e.getMessage()!=null && !e.getMessage().isEmpty()){
                description = e.getMessage();
            }else {
                description = getDescriptionForCode.getDescription(e.getEntity(), e.getStatusCode(),
                        req.getLang());
            }

            QuotationResponse quotationResponse = new QuotationResponse(description,
                    CastUtils.joinStatusCode(e.getEntity(), e.getStatusCode()),
                    Optional.ofNullable(e.getMappedCode()).isPresent() ? e.getMappedCode()
                            : getDescriptionForCode.getMappingCode(e.getStatusCode()));
            this.publishResponseEvent(quotationResponse, interopReferenceId, req, e);
        } catch (RestClientException restException) {
            String description = getDescriptionForCode.getDescription(
                    InteropResponseCodes.NOT_ABLE_TO_CONNECT_THIRD_PARTY.getEntity().toString(),
                    InteropResponseCodes.NOT_ABLE_TO_CONNECT_THIRD_PARTY.getStatusCode(), req.getLang());
            QuotationResponse quotationResponse = new QuotationResponse(description,
                    CastUtils.joinStatusCode(
                            InteropResponseCodes.NOT_ABLE_TO_CONNECT_THIRD_PARTY.getEntity().toString(),
                            InteropResponseCodes.NOT_ABLE_TO_CONNECT_THIRD_PARTY.getStatusCode()),
                    getDescriptionForCode
                            .getMappingCode(InteropResponseCodes.NOT_ABLE_TO_CONNECT_THIRD_PARTY.getStatusCode()));
            this.publishResponseEvent(quotationResponse, interopReferenceId, req, restException);
        } catch (Exception e) {
            String description = getDescriptionForCode.getDescription(
                    InteropResponseCodes.INTERNAL_ERROR.getEntity().toString(),
                    InteropResponseCodes.INTERNAL_ERROR.getStatusCode(), req.getLang());
            QuotationResponse quotationResponse = new QuotationResponse(description,
                    CastUtils.joinStatusCode(InteropResponseCodes.INTERNAL_ERROR.getEntity().toString(),
                            InteropResponseCodes.INTERNAL_ERROR.getStatusCode()),
                    getDescriptionForCode.getMappingCode(InteropResponseCodes.INTERNAL_ERROR.getStatusCode()));
            this.publishResponseEvent(quotationResponse, interopReferenceId, req, e);
        }
    }

    private void publishResponseEvent(QuotationResponse quotationResponse, String reqId, QuotationRequest req, Exception exception) {
        TransactionQuotationResponseEvent initiateEnrolmentResponseEvent = new TransactionQuotationResponseEvent(this,
                quotationResponse, reqId);
        String message = LoggerUtil.prepareLogDetailForQuotationResponse(req, brokerServiceURLProperties.getUrlCountryIdValue(), reqId,
                LogConstants.OUTGOING_RESPONSE_EVENT_TYPE.getValue(),  quotationResponse, exception);
        LOGGER.info("Transaction quotation  service response: {}", message);
        applicationEventPublisher.publishEvent(initiateEnrolmentResponseEvent);
    }

    private QuotationResponse getQuotationResponse(String interopReferenceId, String extOrgRefId, Response fee) {
        QuotationResponse response = new QuotationResponse();
        response.setExtOrgRefId(extOrgRefId);
        response.setInteropRefId(interopReferenceId);
        WalletResponse walletRes = fee.getWalletResponse();
        response.setFeesPayerPaid(walletRes.getFeesPayerPaid());
        response.setFeesPayeePaid(walletRes.getFeesPayeePaid());
        response.setCommissionPayerPaid(walletRes.getCommPayerPaid());
        response.setCommissionPayerReceived(walletRes.getCommPayerRec());
        response.setCommissionPayeePaid(walletRes.getCommPayeePaid());
        response.setCommissionPayeeReceived(walletRes.getCommPayeeRec());
        response.setTaxPayerPaid(walletRes.getTaxPayerPaid());
        response.setTaxPayeePaid(walletRes.getTaxPayeePaid());
        response.setMessage(walletRes.getMessage());
        response.setMappedCode(fee.getMappingResponse().getMappingCode());
        response.setCode(CastUtils.joinStatusCode(InteropResponseCodes.SUCCESS.getEntity().toString(), InteropResponseCodes.SUCCESS.getStatusCode()));
        response.setTransactionSubmitTime(new DateTime());
        return response;
    }
}
