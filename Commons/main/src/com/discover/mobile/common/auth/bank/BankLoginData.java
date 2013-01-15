package com.discover.mobile.common.auth.bank;

import com.fasterxml.jackson.annotation.JsonProperty;


public class BankLoginData {
	
	@JsonProperty("value")
	public String token;
	
	
	@JsonProperty("links")
	public Links links;
	
}
