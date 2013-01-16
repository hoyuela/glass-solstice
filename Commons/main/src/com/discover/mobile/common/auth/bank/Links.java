package com.discover.mobile.common.auth.bank;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data class for mapping the JSON data for links
 * 
 * @author ajleeds
 * 
 */
public class Links {

	@JsonProperty("logout")
	public ReceivedUrl logout;

	@JsonProperty("ping")
	public ReceivedUrl ping;

}
