package com.comviva.interop.txnengine.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import com.comviva.interop.txnengine.model.Response;

@RunWith(SpringRunner.class)
public class StringUtilsTest {

	@Test
	public void testGetFeesParams() {
		String exceptedResult = "amount=100&service_type=CTMMREQ&payer_user_type=channel&payer_account_id=763122222&payer_provider_id=101&payer_pay_id=12&payee_user_type=channel&payee_account_id=763177777&payee_provider_id=101&payee_pay_id=12";
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("amount", "100");
		params.put("service_type", "CTMMREQ");
		params.put("payer_user_type", "channel");
		params.put("payer_account_id", "763122222");
		params.put("payer_provider_id", "101");
		params.put("payer_pay_id", "12");
		params.put("payee_user_type", "channel");
		params.put("payee_account_id", "763177777");
		params.put("payee_provider_id", "101");
		params.put("payee_pay_id", "12");
		String orginalResult = StringUtils.formatMapKeyValues(params);
		assertThat(orginalResult, is(exceptedResult));
	}

	@Test
	public void testP2PInitParams() {
		String exceptedResult = "msisdn=768910909&msisdn2=768979849&pin=2468&amount=10&provider=101&provider2=101&payid=12&payid2=12";
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("msisdn", "768910909");
		params.put("msisdn2", "768979849");
		params.put("pin", "2468");
		params.put("amount", "10");
		params.put("provider", "101");
		params.put("provider2", "101");
		params.put("payid", "12");
		params.put("payid2", "12");
		String orginalResult = StringUtils.formatMapKeyValues(params);
		assertThat(orginalResult, is(exceptedResult));
	}

	@Test
	public void testP2PConfirmParams() {
		String exceptedResult = "msisdn2=768910909&msisdn=768979849&provider=101&txnid=PP190415.1257.R00018&status=1";
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("msisdn2", "768910909");
		params.put("msisdn", "768979849");
		params.put("provider", "101");
		params.put("txnid", "PP190415.1257.R00018");
		params.put("status", "1");
		String orginalResult = StringUtils.formatMapKeyValues(params);
		assertThat(orginalResult, is(exceptedResult));
	}

	@Test
	public void testGetFeeResponse() {
		String xmlString = "<?xml version=\"\"1.0\"\" encoding=\"\"UTF-8\"\"?>" +
				"<response>" +
				"    <broker_response>" +
				"        <broker_code>200</broker_code>" +
				"        <broker_msg>ok</broker_msg>" +
				"        <call_wallet_id>2244</call_wallet_id>" +
				"        <session_id>BROKER-V3_SN_001_20190311121123772_257</session_id>" +
				"    </broker_response>" +
				"    <mapping_response>" +
				"        <mapping_code>SUCCESS</mapping_code>" +
				"    </mapping_response>" +
				"    <wallet_response>" +
				"        <type>GETFEESRESP</type>" +
				"        <service_type>CTMMREQ</service_type>" +
				"        <txnid>GF190311.1311.R00004</txnid>" +
				"        <txnstatus>200</txnstatus>" +
				"        <trid>201903111311R7300</trid>" +
				"        <fees_payer_paid>0.00</fees_payer_paid>" +
				"        <fees_payee_paid>0.00</fees_payee_paid>" +
				"        <comm_payer_paid>0.00</comm_payer_paid>" +
				"        <comm_payer_rec>0.00</comm_payer_rec>" +
				"        <comm_payee_paid>0.00</comm_payee_paid>" +
				"        <comm_payee_rec>0.00</comm_payee_rec>" +
				"        <tax_payer_paid>0.00</tax_payer_paid>" +
				"        <tax_payee_paid>0.00</tax_payee_paid>" +
				"        <message>Success</message>" +
				"    </wallet_response>" +
				"</response>";
		Response response = StringUtils.xmlToModel(xmlString);
		assertThat(response.getBrokerResponse().getBrokerCode(), is("200"));
	}

	@Test
	public void testLeftPadString() {
		assertThat(StringUtils.leftPad("123", 6, '0'), is("000123"));
		assertThat(StringUtils.leftPad("3", 6, '0'), is("000003"));
		assertThat(StringUtils.leftPad("13", 6, '0'), is("000013"));
		assertThat(StringUtils.leftPad("10", 6, '0'), is("000010"));
		assertThat(StringUtils.leftPad("100000", 6, '0'), is("100000"));
	}
	
	@Test
	public void testPrepareSTAN() {
	    assertThat(StringUtils.prepareSTAN("1"), is("000001"));
	    assertThat(StringUtils.prepareSTAN("0"), is("000000"));
	    assertThat(StringUtils.prepareSTAN("10"), is("000010"));
	    assertThat(StringUtils.prepareSTAN("100"), is("000100"));
	    assertThat(StringUtils.prepareSTAN("1000"), is("001000"));
	    assertThat(StringUtils.prepareSTAN("10000"), is("010000"));
	    assertThat(StringUtils.prepareSTAN("100000"), is("100000"));
	}

}