package com.discover.mobile.common.bank.auth;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;


public class ReceivedUrl {
	
	@JsonProperty("ref")
	public String url;
	
	@JsonProperty("allowed")
	public List requestType;

}
