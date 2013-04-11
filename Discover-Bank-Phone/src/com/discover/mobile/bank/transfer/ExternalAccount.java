package com.discover.mobile.bank.transfer;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExternalAccount {
	
	@JsonProperty("id")
	public int id;
	
	@JsonProperty("type")
	public String type;
	
	@JsonProperty("nickname")
	public String nickname;
	
}
