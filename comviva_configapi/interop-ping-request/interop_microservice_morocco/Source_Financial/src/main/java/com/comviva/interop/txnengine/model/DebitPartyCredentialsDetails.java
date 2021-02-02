package com.comviva.interop.txnengine.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class DebitPartyCredentialsDetails {

	private String pin;
	
	private String em;
}
