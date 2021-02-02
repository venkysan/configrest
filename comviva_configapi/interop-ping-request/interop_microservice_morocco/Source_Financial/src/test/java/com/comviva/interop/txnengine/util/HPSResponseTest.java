package com.comviva.interop.txnengine.util;


import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.comviva.interop.txnengine.model.BrokerResponse;
import com.comviva.interop.txnengine.model.MappingResponse;
import com.comviva.interop.txnengine.model.Response;
import com.comviva.interop.txnengine.model.WalletData;
import com.comviva.interop.txnengine.model.WalletResponse;
import com.comviva.interop.txnengine.model.WalletType;

public class HPSResponseTest {

	@Test
	public void mappedResonseCheck() {
		Response response = getHPSResponse();
		Response actualResponse = getXMLResponse();
		assertEquals(response.getMappingResponse().getMappingCode(), actualResponse.getMappingResponse().getMappingCode());
		
		assertEquals(response.getBrokerResponse().getBrokerCode(), actualResponse.getBrokerResponse().getBrokerCode());
		assertEquals(response.getBrokerResponse().getCallWalletId(), actualResponse.getBrokerResponse().getCallWalletId());
		assertEquals(response.getBrokerResponse().getSessionId(), actualResponse.getBrokerResponse().getSessionId());
		assertEquals(response.getBrokerResponse().getBrokerMsg(), actualResponse.getBrokerResponse().getBrokerMsg());
		
		assertEquals(response.getWalletResponse().getBalance(), actualResponse.getWalletResponse().getBalance());
		assertEquals(response.getWalletResponse().getBarred(), actualResponse.getWalletResponse().getBarred());
		assertEquals(response.getWalletResponse().getBarredType(), actualResponse.getWalletResponse().getBarredType());
		assertEquals(response.getWalletResponse().getCategory(), actualResponse.getWalletResponse().getCategory());
		assertEquals(response.getWalletResponse().getCommPayeePaid(), actualResponse.getWalletResponse().getCommPayeePaid());
		assertEquals(response.getWalletResponse().getCommPayeeRec(), actualResponse.getWalletResponse().getCommPayeeRec());
		assertEquals(response.getWalletResponse().getCommPayerPaid(), actualResponse.getWalletResponse().getCommPayerPaid());
		assertEquals(response.getWalletResponse().getCommPayerRec(), actualResponse.getWalletResponse().getCommPayerRec());
		assertEquals(response.getWalletResponse().getDob(), actualResponse.getWalletResponse().getDob());
		assertEquals(response.getWalletResponse().getDomain(), actualResponse.getWalletResponse().getDomain());
		assertEquals(response.getWalletResponse().getFeesPayeePaid(), actualResponse.getWalletResponse().getFeesPayeePaid());
		assertEquals(response.getWalletResponse().getFeesPayerPaid(), actualResponse.getWalletResponse().getFeesPayerPaid());
		assertEquals(response.getWalletResponse().getFname(), actualResponse.getWalletResponse().getFname());
		assertEquals(response.getWalletResponse().getFrbalance(), actualResponse.getWalletResponse().getFrbalance());
		assertEquals(response.getWalletResponse().getFrozenbal(), actualResponse.getWalletResponse().getFrozenbal());
		assertEquals(response.getWalletResponse().getIdno(), actualResponse.getWalletResponse().getIdno());
		assertEquals(response.getWalletResponse().getLang(), actualResponse.getWalletResponse().getLang());
		assertEquals(response.getWalletResponse().getLname(), actualResponse.getWalletResponse().getLname());
		assertEquals(response.getWalletResponse().getMessage(), actualResponse.getWalletResponse().getMessage());
		assertEquals(response.getWalletResponse().getMsisdn(), actualResponse.getWalletResponse().getMsisdn());
		assertEquals(response.getWalletResponse().getNoofdata(), actualResponse.getWalletResponse().getNoofdata());
		assertEquals(response.getWalletResponse().getNooftxn(), actualResponse.getWalletResponse().getNooftxn());
		assertEquals(response.getWalletResponse().getReqStatus(), actualResponse.getWalletResponse().getReqStatus());
		assertEquals(response.getWalletResponse().getServiceType(), actualResponse.getWalletResponse().getServiceType());
		assertEquals(response.getWalletResponse().getSuspendstatus(), actualResponse.getWalletResponse().getSuspendstatus());
		assertEquals(response.getWalletResponse().getTaxPayeePaid(), actualResponse.getWalletResponse().getTaxPayeePaid());
		assertEquals(response.getWalletResponse().getTaxPayerPaid(), actualResponse.getWalletResponse().getTaxPayerPaid());
		assertEquals(response.getWalletResponse().getToken(), actualResponse.getWalletResponse().getToken());
		assertEquals(response.getWalletResponse().getTrid(), actualResponse.getWalletResponse().getTrid());
		assertEquals(response.getWalletResponse().getTxnid(), actualResponse.getWalletResponse().getTxnid());
		assertEquals(response.getWalletResponse().getTxnmode(), actualResponse.getWalletResponse().getTxnmode());
		assertEquals(response.getWalletResponse().getTxnstatus(), actualResponse.getWalletResponse().getTxnstatus());
		assertEquals(response.getWalletResponse().getType(), actualResponse.getWalletResponse().getType());
		assertEquals(response.getWalletResponse().getUserId(), actualResponse.getWalletResponse().getUserId());
		assertEquals(response.getWalletResponse().getUserStatus(), actualResponse.getWalletResponse().getUserStatus());
		
		assertEquals(response.getWalletResponse().getData().get(0).getTxnId().get(0), actualResponse.getWalletResponse().getData().get(0).getTxnId().get(0));
		assertEquals(response.getWalletResponse().getData().get(0).getTxnAmt().get(0), actualResponse.getWalletResponse().getData().get(0).getTxnAmt().get(0));
		assertEquals(response.getWalletResponse().getData().get(0).getFrom().get(0), actualResponse.getWalletResponse().getData().get(0).getFrom().get(0));
		assertEquals(response.getWalletResponse().getData().get(0).getTxnDt().get(0), actualResponse.getWalletResponse().getData().get(0).getTxnDt().get(0));
		assertEquals(response.getWalletResponse().getData().get(0).getService().get(0), actualResponse.getWalletResponse().getData().get(0).getService().get(0));
		assertEquals(response.getWalletResponse().getData().get(0).getFromTo().get(0), actualResponse.getWalletResponse().getData().get(0).getFromTo().get(0));
		assertEquals(response.getWalletResponse().getData().get(0).getTxnType().get(0), actualResponse.getWalletResponse().getData().get(0).getTxnType().get(0));
		assertEquals(response.getWalletResponse().getData().get(0).getTxnStatus().get(0), actualResponse.getWalletResponse().getData().get(0).getTxnStatus().get(0));
		assertEquals(response.getWalletResponse().getData().get(0).getPayId().get(0), actualResponse.getWalletResponse().getData().get(0).getPayId().get(0));
		assertEquals(response.getWalletResponse().getData().get(0).getTxnMode().get(0), actualResponse.getWalletResponse().getData().get(0).getTxnMode().get(0));
		
		
		
	}
	
	private Response getHPSResponse() {
		Response response = new Response();
		BrokerResponse brokerResponse = new BrokerResponse();
		brokerResponse.setBrokerCode("200");
		brokerResponse.setBrokerMsg("ok");
		brokerResponse.setCallWalletId("2244");
		brokerResponse.setSessionId("BROKER-V3_SN_001_20190311121123772_257");
		response.setBrokerResponse(brokerResponse);
		MappingResponse mappingResponse = new MappingResponse();
		mappingResponse.setMappingCode("SUCCESS");
		response.setMappingResponse(mappingResponse);
		WalletData walletData= new WalletData();
		 walletData.setFrom(Arrays.asList("WALLET"));
		 walletData.setFromTo(Arrays.asList("WALLET"));
		 walletData.setPayId(Arrays.asList("12"));
		 walletData.setService(Arrays.asList("COMM"));
		 walletData.setTxnId(Arrays.asList("123.13.13.13"));
		 walletData.setTxnStatus(Arrays.asList("SUCCESS"));
		 walletData.setTxnAmt(Arrays.asList("0.00"));
		 walletData.setTxnDt(Arrays.asList("14052019"));
		 walletData.setTxnType(Arrays.asList("TS"));
		 walletData.setTxnMode(Arrays.asList("INTEROP"));
		 WalletType walletType = new WalletType();
		 walletType.setGrade("UI");
		 walletType.setWId("13213213213");
		 walletType.setWtName("INTEROP");
		 walletData.setWalletType(Arrays.asList(walletType));
		 List<WalletData> listWalletData = new ArrayList<>();
		 listWalletData.add(walletData);
		
		 WalletResponse walletResponse = new WalletResponse();
		 walletResponse.setBalance("0.00");
		 walletResponse.setBarred("COMVIVA");
		 walletResponse.setBarredType("TCP");
		 walletResponse.setCategory("COMM");
		 walletResponse.setCommPayeePaid("0.00");
		 walletResponse.setCommPayeeRec("0.00");
		 walletResponse.setCommPayerPaid("0.00");
		 walletResponse.setCommPayerRec("0.00");
		 walletResponse.setData(listWalletData);
		 walletResponse.setDob("14052019");
		 walletResponse.setDomain("INTEROP");
		 walletResponse.setFeesPayeePaid("0.00");
		 walletResponse.setFeesPayerPaid("0.00");
		 walletResponse.setFname("INTEROP");
		 walletResponse.setFrbalance("0.00");
		 walletResponse.setFrozenbal("0.00");
		 walletResponse.setIdno("12345235423");
		 walletResponse.setLang("en");
		 walletResponse.setLname("INTEROP");
		 walletResponse.setMessage("Success");
		 walletResponse.setMsisdn("1231213230");
		 walletResponse.setNoofdata("1");
		 walletResponse.setNooftxn("1");
		 walletResponse.setReqStatus("OK");
		 walletResponse.setServiceType("CTMMREQ");
		 walletResponse.setSuspendstatus("OK");
		 walletResponse.setTaxPayeePaid("0.00");
		 walletResponse.setTaxPayerPaid("0.00");
		 walletResponse.setToken("xyzabcxyzabe");
		 walletResponse.setTrid("201903111311R7300");
		 walletResponse.setTxnid("GF190311.1311.R00004");
		 walletResponse.setTxnmode("COMM");
		 walletResponse.setTxnstatus("200");
		 walletResponse.setType("GETFEESRESP");
		 walletResponse.setUserId("INTEROP");
		 walletResponse.setUserStatus("SUCCESS");
		 response.setWalletResponse(walletResponse);
		return response;
	}
	
	
	private Response getXMLResponse() {
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
				"        <balance>0.00</balance>" +
				"        <type>GETFEESRESP</type>" +
				"        <msisdn>1231213230</msisdn>" +
				"        <lang>en</lang>" +
				"        <reqstatus>OK</reqstatus>" +
				"        <userid>INTEROP</userid>" +
				"        <userstatus>SUCCESS</userstatus>" +
				"        <barredtype>TCP</barredtype>" +
				"        <token>xyzabcxyzabe</token>" +
				"        <frozenbal>0.00</frozenbal>" +
				"        <domain>INTEROP</domain>" +
				"        <category>COMM</category>" +
				"        <fname>INTEROP</fname>" +
				"        <lname>INTEROP</lname>" +
				"        <idno>12345235423</idno>" +
				"        <frbalance>0.00</frbalance>" +
				"        <dob>14052019</dob>" +
				"        <suspendstatus>OK</suspendstatus>" +
				"        <barred>COMVIVA</barred>" +
				"        <nooftxn>1</nooftxn>" +
				"        <noofdata>1</noofdata>" +
				"        <data>" +
				"        <WALLETTYPE>" +
				"        <WTNAME>INTEROP</WTNAME>" +
				"        <WID>13213213213</WID>" +
				"        <GRADE>UI</GRADE>" +
				"        </WALLETTYPE>" +
				"        <TXN_ID>123.13.13.13</TXN_ID>" +
				"        <TXNAMT>0.00</TXNAMT>" +
				"        <FROM>WALLET</FROM>" +
				"        <TXNDT>14052019</TXNDT>" +
				"        <SERVICE>COMM</SERVICE>" +
				"        <FROMTO>WALLET</FROMTO>" +
				"        <TXNTYPE>TS</TXNTYPE>" +
				"        <TXN_STATUS>SUCCESS</TXN_STATUS>" +
				"        <PAYID>12</PAYID>" +
				"        <TXNMODE>INTEROP</TXNMODE>" +
				"        </data>" +
				"        <service_type>CTMMREQ</service_type>" +
				"        <txnid>GF190311.1311.R00004</txnid>" +
				"        <txnmode>COMM</txnmode>" +
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
		return StringUtils.xmlToModel(xmlString);
	}
	
}
