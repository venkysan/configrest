package com.comviva.interop.txnengine.util;

import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comviva.interop.txnengine.configuration.ThreadPoolProperties;
import com.comviva.interop.txnengine.enums.ErrorStatus;
import com.comviva.interop.txnengine.enums.LogConstants;
import com.comviva.interop.txnengine.exception.InteropException;

@Service
public class VelocityTemplateMaker {

    private VelocityEngine velocityEngine;
    private static final Logger LOGGER = LoggerFactory.getLogger(VelocityTemplateMaker.class);
    private ThreadPoolProperties threadPoolProperties;

    @Autowired
    public VelocityTemplateMaker(ThreadPoolProperties threadPoolProperties) {
        this.threadPoolProperties = threadPoolProperties;
        VelocityEngine engine = new VelocityEngine();
        try {
            Properties p = new Properties();
            p.setProperty("resource.loader", "class");
            p.setProperty("class.resource.loader.class",
                    "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            p.setProperty("parser.pool.size", this.threadPoolProperties.getVelocityParserPoolSize());
            engine.init(p);
            this.velocityEngine = engine;
        } catch (Exception e) {
            String message = LoggerUtil.printLog(LogConstants.VELOCITY_TEMPLATE_ENGINE_INITIATION_FAILED_EVENT.getValue(), e);
            LOGGER.info("Error while initiating velocity template engine..{}",message);
        }
    }

    public String getTemplatefromMapInput(String templateName, Map<String, String> mapValues) {
        return makeTemplate(templateName, mapValues);
    }

    public String getTemplatefromStringInput(String templateName, String stringValues) {
        return makeTemplate(templateName, CastUtils.stringToMap(stringValues));
    }

    private String makeTemplate(String templateName, Map<String, String> valuesMap) {
        try {
            Template template = velocityEngine.getTemplate(String.format("VelocityTemplates/%s.vm", templateName));
            StringWriter transformedString = new StringWriter();
            VelocityContext vc = new VelocityContext(valuesMap);
            vc.put("map", valuesMap);
            template.merge(vc, transformedString);
            return transformedString.toString();
        } catch (ResourceNotFoundException resourceNotFoundException) {
            String message = LoggerUtil.printLog(LogConstants.VELOCITY_TEMPLATE_ENGINE_INITIATION_FAILED_EVENT.getValue(), resourceNotFoundException);
            LOGGER.info("Error while initiating velocity template engine due to resource not found ..{}",message);
            throw new InteropException(ErrorStatus.VELOCITY_RESOURCE_NOT_FOUND_EXCEPTION.getStatusCode(),
                    ErrorStatus.VELOCITY_RESOURCE_NOT_FOUND_EXCEPTION.getEntity().toString());
        } catch (ParseErrorException parseException) {
            String message = LoggerUtil.printLog(LogConstants.VELOCITY_TEMPLATE_ENGINE_INITIATION_FAILED_EVENT.getValue(), parseException);
            LOGGER.info("Error while initiating velocity template engine due to parse ..{}",message);
            throw new InteropException(ErrorStatus.VELOCITY_PARSE_ERROR_EXCEPTION.getStatusCode(),
                    ErrorStatus.VELOCITY_PARSE_ERROR_EXCEPTION.getEntity().toString());
        } catch (MethodInvocationException e) {
            String message = LoggerUtil.printLog(LogConstants.VELOCITY_TEMPLATE_ENGINE_INITIATION_FAILED_EVENT.getValue(), e);
            LOGGER.info("Error while initiating velocity template engine due to method invocation ..{}",message);
            throw new InteropException(ErrorStatus.VELOCITY_METHOD_INVOCATION_EXCEPTION.getStatusCode(),
                    ErrorStatus.VELOCITY_METHOD_INVOCATION_EXCEPTION.getEntity().toString());
        }
    }
}
