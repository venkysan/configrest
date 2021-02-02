package com.comviva.interop.txnengine.service.test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import com.comviva.interop.txnengine.configuration.SMSServerProperties;
import com.comviva.interop.txnengine.entities.SmsDelivery;
import com.comviva.interop.txnengine.enums.SMSDeliveryStatus;
import com.comviva.interop.txnengine.enums.TestCaseConstants;
import com.comviva.interop.txnengine.exception.InteropException;
import com.comviva.interop.txnengine.repositories.SmsDeliveryRepository;
import com.comviva.interop.txnengine.sms.SMSDeliveryThread;
import com.comviva.interop.txnengine.sms.SMSFetchingThread;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SMSDeliveryFunctionalityTests {

	@Test(expected=InteropException.class)
	public void verifySMSDeliveryThreadThrowException() {
		List<SmsDelivery> smsDeliveries = new ArrayList<>();
		smsDeliveries.add(prepareSMSDelivery());
		SMSServerProperties resource = mock(SMSServerProperties.class);
		SmsDeliveryRepository smsDeliveryRepository = mock(SmsDeliveryRepository.class);
		when(smsDeliveryRepository.save(smsDeliveries.get(0))).thenReturn(smsDeliveries.get(0));
		SMSDeliveryThread smsDeliveryThread = new SMSDeliveryThread(smsDeliveries, resource, smsDeliveryRepository);
		smsDeliveryThread.run();
		Mockito.verify(smsDeliveryRepository, Mockito.times(1)).save(smsDeliveries.get(0));
	}
	
	@Test
	public void verifySMSDeliveryThreadWithTestEnvironment() {
		List<SmsDelivery> smsDeliveries = new ArrayList<>();
		smsDeliveries.add(prepareSMSDelivery());
		SMSServerProperties resource = mock(SMSServerProperties.class);
		when(resource.isTestEnvironment()).thenReturn(Boolean.TRUE);
		
		
		SmsDeliveryRepository smsDeliveryRepository = mock(SmsDeliveryRepository.class);
		when(smsDeliveryRepository.save(smsDeliveries.get(0))).thenReturn(smsDeliveries.get(0));
		
		SMSDeliveryThread smsDeliveryThread = new SMSDeliveryThread(smsDeliveries, resource, smsDeliveryRepository);
		smsDeliveryThread.run();
		Mockito.verify(resource, Mockito.times(1)).isTestEnvironment();
	}
	
	@Test
	public void verifySMSFetchingThreadNegativeTest() {
	    SMSServerProperties resource = mock(SMSServerProperties.class);
		when(resource.getNoOfSmsRetries()).thenReturn(3);
		when(resource.getNodeName()).thenReturn(TestCaseConstants.INTER_OP_TXN_ENGIN_NAME.getValue());
		when(resource.getSmsRecordsLimit()).thenReturn(3);
		when(resource.getNoOfThreadsToDeliverSMS()).thenReturn(3);
		SmsDeliveryRepository smsDeliveryRepository = mock(SmsDeliveryRepository.class);
		when(smsDeliveryRepository.findUnDeliveredMessages(SMSDeliveryStatus.N.toString(), 3, TestCaseConstants.INTER_OP_TXN_ENGIN_NAME.getValue(), PageRequest.of(0, 3, Sort.by(Sort.Direction.ASC, TestCaseConstants.SMS_ID.getValue())))).thenThrow(InteropException.class);
		
		SMSFetchingThread smsFetchingThread = new SMSFetchingThread(resource, smsDeliveryRepository);
		smsFetchingThread.run();
		Mockito.verify(smsDeliveryRepository, Mockito.times(1)).findUnDeliveredMessages(SMSDeliveryStatus.N.toString(), 3, TestCaseConstants.INTER_OP_TXN_ENGIN_NAME.getValue(), PageRequest.of(0, 3, Sort.by(Sort.Direction.ASC, TestCaseConstants.SMS_ID.getValue())));
	}
	
	@Test
	public void verifySMSFetchingThreadWithNullListTest() {
	    SMSServerProperties resource = mock(SMSServerProperties.class);
		when(resource.getNoOfSmsRetries()).thenReturn(3);
		when(resource.getNodeName()).thenReturn(TestCaseConstants.INTER_OP_TXN_ENGIN_NAME.getValue());
		when(resource.getSmsRecordsLimit()).thenReturn(3);
		when(resource.getNoOfThreadsToDeliverSMS()).thenReturn(3);
		SmsDeliveryRepository smsDeliveryRepository = mock(SmsDeliveryRepository.class);
		when(smsDeliveryRepository.findUnDeliveredMessages(SMSDeliveryStatus.N.toString(), 3, TestCaseConstants.INTER_OP_TXN_ENGIN_NAME.getValue(), PageRequest.of(0, 3, Sort.by(Sort.Direction.ASC, TestCaseConstants.SMS_ID.getValue())))).thenReturn(null);
		
		SMSFetchingThread smsFetchingThread = new SMSFetchingThread(resource, smsDeliveryRepository);
		smsFetchingThread.run();
		Mockito.verify(smsDeliveryRepository, Mockito.times(1)).findUnDeliveredMessages(SMSDeliveryStatus.N.toString(), 3, TestCaseConstants.INTER_OP_TXN_ENGIN_NAME.getValue(), PageRequest.of(0, 3, Sort.by(Sort.Direction.ASC, TestCaseConstants.SMS_ID.getValue())));
	}
	
	@Test
	public void verifySMSFetchingThreadWithEmptyListTest() {
	    SMSServerProperties resource = mock(SMSServerProperties.class);
		when(resource.getNoOfSmsRetries()).thenReturn(3);
		when(resource.getNodeName()).thenReturn(TestCaseConstants.INTER_OP_TXN_ENGIN_NAME.getValue());
		when(resource.getSmsRecordsLimit()).thenReturn(3);
		when(resource.getNoOfThreadsToDeliverSMS()).thenReturn(3);
		SmsDeliveryRepository smsDeliveryRepository = mock(SmsDeliveryRepository.class);
		when(smsDeliveryRepository.findUnDeliveredMessages(SMSDeliveryStatus.N.toString(), 3, TestCaseConstants.INTER_OP_TXN_ENGIN_NAME.getValue(), PageRequest.of(0, 3, Sort.by(Sort.Direction.ASC, TestCaseConstants.SMS_ID.getValue())))).thenReturn(new ArrayList<>());
		
		SMSFetchingThread smsFetchingThread = new SMSFetchingThread(resource, smsDeliveryRepository);
		smsFetchingThread.run();
		Mockito.verify(smsDeliveryRepository, Mockito.times(1)).findUnDeliveredMessages(SMSDeliveryStatus.N.toString(), 3, TestCaseConstants.INTER_OP_TXN_ENGIN_NAME.getValue(), PageRequest.of(0, 3, Sort.by(Sort.Direction.ASC, TestCaseConstants.SMS_ID.getValue())));
	}
	
	private SmsDelivery prepareSMSDelivery() {
		SmsDelivery smsDelivery = new SmsDelivery();
		smsDelivery.setCreatedon(new Date());
		smsDelivery.setLanguageCode(TestCaseConstants.LANGUAGE_CODE.getValue());
		smsDelivery.setMessage("Transaction Success");
		smsDelivery.setMsisdn(TestCaseConstants.DEFAULT_MSISDN.getValue());
		smsDelivery.setNodeName(TestCaseConstants.INTER_OP_TXN_ENGIN_NAME.getValue());
		smsDelivery.setReciever("1234567891");
		return smsDelivery;
	}
}
