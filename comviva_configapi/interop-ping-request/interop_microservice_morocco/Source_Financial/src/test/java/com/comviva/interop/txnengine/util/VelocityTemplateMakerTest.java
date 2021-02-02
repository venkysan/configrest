package com.comviva.interop.txnengine.util;

import static org.mockito.Mockito.when;

import java.util.HashMap;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

import com.comviva.interop.txnengine.configuration.ThreadPoolProperties;
import com.comviva.interop.txnengine.exception.InteropException;

@RunWith(SpringRunner.class)
public class VelocityTemplateMakerTest {

	@Mock
	private VelocityEngine velocityEngine;

	@InjectMocks
	private VelocityTemplateMaker velocityTemplateMaker = new VelocityTemplateMaker(new ThreadPoolProperties());

	@Test(expected = InteropException.class)
	public void testMakeTemplateResourceNotFoundException() {
		when(velocityEngine.getTemplate(Mockito.anyString())).thenThrow(new ResourceNotFoundException("exception"));
		velocityTemplateMaker.getTemplatefromMapInput("deRegTemplate", new HashMap<>());
	}

	@Test(expected = InteropException.class)
	public void testMakeTemplateParseErrorException() {
		when(velocityEngine.getTemplate(Mockito.anyString())).thenThrow(new ParseErrorException("exception"));
		velocityTemplateMaker.getTemplatefromMapInput("deRegTemplate", new HashMap<>());
	}

	@Test(expected = InteropException.class)
	public void testMakeTemplateMethodInvocationException() {
		when(velocityEngine.getTemplate(Mockito.anyString()))
				.thenThrow(new MethodInvocationException("", new RuntimeException(), "", "", 1, 1));
		velocityTemplateMaker.getTemplatefromMapInput("deRegTemplate", new HashMap<>());
	}

}
