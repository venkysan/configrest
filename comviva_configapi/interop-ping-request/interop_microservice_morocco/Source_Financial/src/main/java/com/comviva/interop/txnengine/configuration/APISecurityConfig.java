package com.comviva.interop.txnengine.configuration;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import com.comviva.interop.txnengine.model.CustomAuthenticationExceptionHandler;

@Configuration
@EnableWebSecurity
@Order(1)
public class APISecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${api.key.header}")
    private String principalRequestHeader;

    @Value("${api.key.value}")
    private String principalRequestValue;

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        APIKeyAuthenticationFilter filter = new APIKeyAuthenticationFilter(principalRequestHeader);
        filter.setAuthenticationManager(authentication -> {

            String principal = (String) authentication.getPrincipal();
            if (!principalRequestValue.equals(decodeAndHash(principal))) {
                throw new BadCredentialsException("API key not found or is not the expected value.");
            }
            authentication.setAuthenticated(true);
            return authentication;

        });
        httpSecurity.antMatcher("/v1/**").csrf().disable().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().addFilter(filter).authorizeRequests()
                .anyRequest().authenticated().and().exceptionHandling()
                .authenticationEntryPoint(customAuthenticationExceptionHandler());
    }

    private String decodeAndHash(String value) {
        return DigestUtils.sha256Hex(value);
    }

    @Bean
    public CustomAuthenticationExceptionHandler customAuthenticationExceptionHandler() {
        return new com.comviva.interop.txnengine.model.CustomAuthenticationExceptionHandler();
    }

}