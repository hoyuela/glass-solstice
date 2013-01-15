package com.discover.mobile.common.auth.bank;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Links {
	
	@JsonProperty("logout")
	public ReceivedUrl logout;
	
	@JsonProperty("ping")
	public ReceivedUrl ping;

}
