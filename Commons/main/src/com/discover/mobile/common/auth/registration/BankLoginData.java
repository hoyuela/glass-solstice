package com.discover.mobile.common.auth.registration;

import java.util.List;

import com.discover.mobile.common.bank.auth.ReceivedUrl;
import com.fasterxml.jackson.annotation.JsonProperty;


public class BankLoginData {
	
	@JsonProperty("value")
	public String token;
	
	@JsonProperty("links")
	public List<ReceivedUrl> links;
	
}
