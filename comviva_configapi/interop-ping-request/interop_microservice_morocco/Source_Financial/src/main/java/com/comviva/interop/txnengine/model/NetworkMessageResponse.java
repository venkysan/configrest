package com.comviva.interop.txnengine.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class NetworkMessageResponse {

	private String mappedCode;
	private String code;
	private String message;
	
	public NetworkMessageResponse(){
		
	}
	
	public NetworkMessageResponse(String mappedCode, String code, String message) {
        this.mappedCode = mappedCode;
        this.code = code;
        this.message = message;
    }
	
	public String convertToJSON(NetworkMessageResponse networkMessageResponse) throws JsonProcessingException {
        return new ObjectMapper().writer(new DefaultPrettyPrinter()).writeValueAsString(networkMessageResponse);
 }
}
