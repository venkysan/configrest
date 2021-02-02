package com.comviva.interop.txnengine.model;

import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.util.HtmlUtils;

import com.comviva.interop.txnengine.enums.LogConstants;
import com.comviva.interop.txnengine.util.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CustomAuthenticationExceptionHandler implements AuthenticationEntryPoint, Serializable {

    private static final long serialVersionUID = 2835572850470069370L;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        String path = StringUtils.checkIsNullOrEmpty(request.getRequestURI()) ? LogConstants.EMPTY_STRING.getValue() : HtmlUtils.htmlEscape(request.getRequestURI());
        String remoteAddress = StringUtils.checkIsNullOrEmpty(request.getRemoteAddr()) ? LogConstants.EMPTY_STRING.getValue() : request.getRemoteAddr();
        String remoteHost = StringUtils.checkIsNullOrEmpty(request.getRemoteHost()) ? LogConstants.EMPTY_STRING.getValue() : request.getRemoteHost();
        int remotePort = request.getRemotePort();
        String remoteUser = StringUtils.checkIsNullOrEmpty(request.getRemoteUser()) ? LogConstants.EMPTY_STRING.getValue() : HtmlUtils.htmlEscape(request.getRemoteUser());
        
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setHeader("WWW-Authenticate", "Authorization realm=\"Access to Interop APIs\"");
        Map<String, Object> data = new HashMap<>();
        data.put("timestamp", Calendar.getInstance().getTime().toString());
        data.put("exception", authException.getMessage());
        data.put("status", HttpStatus.UNAUTHORIZED.value());
        data.put("error", "Unauthorized");
        data.put("message", "Access Denied");
        data.put("path", path);
        data.put("Remote Address", remoteAddress);
        data.put("Remote Host", remoteHost);
        data.put("Remote Port", remotePort);
        data.put("Remote User", remoteUser);
        response.getWriter().write(objectMapper.writeValueAsString(data));
    }

}