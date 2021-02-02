package com.comviva.interop.txnengine.enums;

public enum UnicId {

	ONE("01"), //
	TWO("02"), //
	THREE("03"),//
	FOUR("04"),//
	FIVE("05"),//
	;

	private String val;

	UnicId(String val) {
		this.val = val;
	}
	
	public String getVal() {
		return val;
	}
}
