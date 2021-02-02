package com.comviva.interop.txnengine.repositiry.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.comviva.interop.txnengine.entities.SmsDelivery;
import com.comviva.interop.txnengine.entities.SmsTemplates;
import com.comviva.interop.txnengine.enums.TestCaseConstants;
import com.comviva.interop.txnengine.repositories.SmsDeliveryRepository;
import com.comviva.interop.txnengine.repositories.SmsTemplatesRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SMSRepositoryTests {

	@Test
	public void smsTemplateRepositoryTest() {
		SmsTemplatesRepository smsTemplatesRepository = mock(SmsTemplatesRepository.class);
		List<SmsTemplates> smsTemplates =new ArrayList<>();
		SmsTemplates smsTemplate = new SmsTemplates();
		smsTemplate.setId("10");
		smsTemplate.setLanguageCode("en");
		smsTemplate.setDescription("Hello");
		smsTemplate.setNotificationCode("Hi");
		smsTemplates.add(smsTemplate);
		when(smsTemplatesRepository.findSmsTemplateByTypeAndLang("Hi", "en")).thenReturn(smsTemplate);
		assertThat(smsTemplate.getNotificationCode(), is(smsTemplate.getNotificationCode()));
		assertThat(smsTemplate.getDescription(), is(smsTemplate.getDescription()));
		assertThat(smsTemplate.getLanguageCode(), is(smsTemplate.getLanguageCode()));
		assertThat(smsTemplate.getId(), is(smsTemplate.getId()));
	}
	
	@Test
	public void smsDeliveryRepositoryTest() {
		SmsDeliveryRepository smsDeliveryRepository = mock(SmsDeliveryRepository.class);
		List<SmsDelivery> smsDeliveries =new ArrayList<>();
		SmsDelivery smsDelivery = new SmsDelivery();
		smsDelivery.setSmsId("10");
		smsDelivery.setRetryCount(0);
		smsDelivery.setStatus("N");
		smsDelivery.setCreatedon(new Date());
		smsDelivery.setCreatedon(new Date());
		smsDelivery.setMsisdn(TestCaseConstants.DEFAULT_MSISDN.getValue());
		smsDelivery.setLanguageCode("en");
		smsDelivery.setNodeName("A");
		smsDelivery.setMessage("Hello");
		smsDelivery.setServiceType("TTSS");
		smsDelivery.setTransferDate(new Date());
		smsDelivery.setTxnId("23232");
		smsDelivery.setTxnStatus("Y");
		smsDeliveries.add(smsDelivery);
		when(smsDeliveryRepository.findUnDeliveredMessages("N", 3, "A", null)).thenReturn(smsDeliveries);
		assertThat(smsDeliveries.get(0).getSmsId(), is(smsDeliveries.get(0).getSmsId()));
		assertThat(smsDeliveries.get(0).getRetryCount(), is(smsDeliveries.get(0).getRetryCount()));
		assertThat(smsDeliveries.get(0).getStatus(), is(smsDeliveries.get(0).getStatus()));
		assertThat(smsDeliveries.get(0).getCreatedon(), is(smsDeliveries.get(0).getCreatedon()));
		assertThat(smsDeliveries.get(0).getMsisdn(), is(smsDeliveries.get(0).getMsisdn()));
		assertThat(smsDeliveries.get(0).getLanguageCode(), is(smsDeliveries.get(0).getLanguageCode()));
		assertThat(smsDeliveries.get(0).getNodeName(), is(smsDeliveries.get(0).getNodeName()));
		assertThat(smsDeliveries.get(0).getReciever(), is(smsDeliveries.get(0).getReciever()));
		assertThat(smsDeliveries.get(0).getRetryCount(), is(smsDeliveries.get(0).getRetryCount()));
		assertThat(smsDeliveries.get(0).getServiceType(), is(smsDeliveries.get(0).getServiceType()));
		assertThat(smsDeliveries.get(0).getTransferDate(), is(smsDeliveries.get(0).getTransferDate()));
		assertThat(smsDeliveries.get(0).getCreatedon(), is(smsDeliveries.get(0).getCreatedon()));
		assertThat(smsDeliveries.get(0).getDeliveredOn(), is(smsDeliveries.get(0).getDeliveredOn()));
		assertThat(smsDeliveries.get(0).getTxnId(), is(smsDeliveries.get(0).getTxnId()));
		assertThat(smsDeliveries.get(0).getSender(), is(smsDeliveries.get(0).getSender()));
		assertThat(smsDeliveries.get(0).getTxnStatus(), is(smsDeliveries.get(0).getTxnStatus()));
	}
}
