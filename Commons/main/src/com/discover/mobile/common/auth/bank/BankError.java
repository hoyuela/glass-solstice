package com.discover.mobile.common.auth.bank;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BankError {
	
	@JsonProperty("name")
	public String name;
	
	@JsonProperty("code")
	public String code; 
	
	@JsonProperty("message")
	public String message;

}
