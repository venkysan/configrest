package com.comviva.interop.txnengine.repositiry.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.comviva.interop.txnengine.entities.ChannelUserDetails;
import com.comviva.interop.txnengine.enums.TestCaseConstants;
import com.comviva.interop.txnengine.repositories.ChannelUserDetailsRepository;

@RunWith(SpringRunner.class)
public class ChannelUserDetailsRepositoryTests {
	
	@MockBean
	ChannelUserDetailsRepository channelUserDetailsRepository;
	
	@Test
	public void findAllChannelUserDetailsTest() {
		
		ChannelUserDetails channelUserDetails = new ChannelUserDetails();
		channelUserDetails.setChannelUserCode(TestCaseConstants.CHANNEL_USER_CODE.getValue());
		channelUserDetails.setCountryId(TestCaseConstants.COUNTRY_ID.getValue());
		channelUserDetails.setCreatedDate(new Date());
		channelUserDetails.setDescription(TestCaseConstants.CHANNEL_USER.getValue());
		channelUserDetails.setMsisdn(TestCaseConstants.DEFAULT_MSISDN.getValue());
		channelUserDetails.setOptional(TestCaseConstants.OPTIONAL_MSG.getValue());
		channelUserDetails.setType(TestCaseConstants.CHANNEL_USER.getValue());
		channelUserDetails.setUpdatedDate(new Date());
		channelUserDetails.setUserId(TestCaseConstants.CHANNEL_USER_ID.getValue());
		List<ChannelUserDetails> list = new ArrayList<>();
		list.add(channelUserDetails);
		when(channelUserDetailsRepository.findAll()).thenReturn(list);
		Iterator<ChannelUserDetails> channelUserDetailsIterable = channelUserDetailsRepository.findAll().iterator();
		while (channelUserDetailsIterable.hasNext()) {
			ChannelUserDetails chUserDetails = channelUserDetailsIterable.next();
			assertThat(chUserDetails.getChannelUserCode(), is(TestCaseConstants.CHANNEL_USER_CODE.getValue()));	
			assertThat(chUserDetails.getCountryId(), is(TestCaseConstants.COUNTRY_ID.getValue()));
			assertThat(chUserDetails.getDescription(), is(TestCaseConstants.CHANNEL_USER.getValue()));
			assertThat(chUserDetails.getMsisdn(), is(TestCaseConstants.DEFAULT_MSISDN.getValue()));
			assertThat(chUserDetails.getOptional(), is(TestCaseConstants.OPTIONAL_MSG.getValue()));
			assertThat(chUserDetails.getType(), is(TestCaseConstants.CHANNEL_USER.getValue()));
			assertThat(chUserDetails.getUserId(), is(TestCaseConstants.CHANNEL_USER_ID.getValue()));
		}
	}
	
	@Test
	public void channelUserDetailsByMsisdnTest() {
		when(channelUserDetailsRepository.findChannelUserDetailsByMsisdn(Mockito.anyString())).thenReturn(getChannelUserDetails());
		ChannelUserDetails chUserDetails=channelUserDetailsRepository.findChannelUserDetailsByMsisdn(TestCaseConstants.DEFAULT_MSISDN.getValue());
		assertThat(chUserDetails.getChannelUserCode(), is(TestCaseConstants.CHANNEL_USER_CODE.getValue()));	
		assertThat(chUserDetails.getCountryId(), is(TestCaseConstants.COUNTRY_ID.getValue()));
		assertThat(chUserDetails.getDescription(), is(TestCaseConstants.CHANNEL_USER.getValue()));
		assertThat(chUserDetails.getMsisdn(), is(TestCaseConstants.DEFAULT_MSISDN.getValue()));
		assertThat(chUserDetails.getOptional(), is(TestCaseConstants.OPTIONAL_MSG.getValue()));
		assertThat(chUserDetails.getType(), is(TestCaseConstants.CHANNEL_USER.getValue()));
		assertThat(chUserDetails.getUserId(), is(TestCaseConstants.CHANNEL_USER_ID.getValue()));
	}
	
	@Test
	public void channelUserDetailsByUserIdTest() {
		when(channelUserDetailsRepository.findChannelUserDetailsByUserId(Mockito.anyString())).thenReturn(getChannelUserDetails());
		ChannelUserDetails chUserDetails=channelUserDetailsRepository.findChannelUserDetailsByUserId(TestCaseConstants.CHANNEL_USER.getValue());
		assertThat(chUserDetails.getChannelUserCode(), is(TestCaseConstants.CHANNEL_USER_CODE.getValue()));	
		assertThat(chUserDetails.getCountryId(), is(TestCaseConstants.COUNTRY_ID.getValue()));
		assertThat(chUserDetails.getDescription(), is(TestCaseConstants.CHANNEL_USER.getValue()));
		assertThat(chUserDetails.getMsisdn(), is(TestCaseConstants.DEFAULT_MSISDN.getValue()));
		assertThat(chUserDetails.getOptional(), is(TestCaseConstants.OPTIONAL_MSG.getValue()));
		assertThat(chUserDetails.getType(), is(TestCaseConstants.CHANNEL_USER.getValue()));
		assertThat(chUserDetails.getUserId(), is(TestCaseConstants.CHANNEL_USER_ID.getValue()));
	}
	
	private ChannelUserDetails getChannelUserDetails() {
		ChannelUserDetails channelUserDetails = new ChannelUserDetails();
		channelUserDetails.setChannelUserCode(TestCaseConstants.CHANNEL_USER_CODE.getValue());
		channelUserDetails.setCountryId(TestCaseConstants.COUNTRY_ID.getValue());
		channelUserDetails.setCreatedDate(new Date());
		channelUserDetails.setDescription(TestCaseConstants.CHANNEL_USER.getValue());
		channelUserDetails.setMsisdn(TestCaseConstants.DEFAULT_MSISDN.getValue());
		channelUserDetails.setOptional(TestCaseConstants.OPTIONAL_MSG.getValue());
		channelUserDetails.setType(TestCaseConstants.CHANNEL_USER.getValue());
		channelUserDetails.setUpdatedDate(new Date());
		channelUserDetails.setUserId(TestCaseConstants.CHANNEL_USER_ID.getValue());
		return channelUserDetails;
	}
}
