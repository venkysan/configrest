package com.comviva.interop.txnengine.enums;

public enum RequestSource {

	MOBILEAPP("mobileApp","000001A00014"),
	USSD("ussd","000001X00014"),
	;

	private String source;
	private String value;
	private RequestSource(String source,String value){
		this.source= source;
		this.value = value;
	}

	public String getSource() {
		return source;
	}
	
	public String getValue() {
		return value;
	}

	public static boolean isSource(String source) {
		for(RequestSource requestSource:RequestSource.values()) {
			if(requestSource.getSource().equalsIgnoreCase(source)) {
				return true;
			}
		}
		return false;
	}
	
	public static String getValueBySource(String source) {
		for(RequestSource requestSource:RequestSource.values()) {
			if(requestSource.getSource().equals(source)) {
				return requestSource.getValue();
			}
		}
		return "";
	}

}
