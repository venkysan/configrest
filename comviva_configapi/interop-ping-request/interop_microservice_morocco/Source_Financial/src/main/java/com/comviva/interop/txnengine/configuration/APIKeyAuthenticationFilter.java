package com.comviva.interop.txnengine.configuration;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

/**
 * This filter is for API key authentication This will validate all the requests
 * by using API key authentication
 *
 */
public class APIKeyAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {

    /**
     * private variable principalRequestHeader to hold the requestHeader
     */
    private String principalRequestHeader;

    /**
     * public constructor APIKeyAuthenticationFilter
     * 
     * @param principalRequestHeader
     *            the principalRequestHeader to be used for holding requestHeader
     */
    public APIKeyAuthenticationFilter(String principalRequestHeader) {
        this.principalRequestHeader = principalRequestHeader;
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        return request.getHeader(principalRequestHeader);
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return "N/A";
    }

}
