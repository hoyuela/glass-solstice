package com.discover.mobile.bank.services.auth;

import java.io.Serializable;

import com.discover.mobile.common.net.json.bank.ReceivedUrl;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data class for mapping the JSON data for links
 * 
 * @author ajleeds, hoyuela
 * 
 */
public class Links implements Serializable {
	/**
	 * Auto-generated serial UID which is used to serialize and de-serialize PhoneNumber objects
	 */
	private static final long serialVersionUID = 2316492273394931939L;
	/**
	 * Holds Resource Link for logging out from a Bank account
	 */
	@JsonProperty("logout")
	public ReceivedUrl logout;
	/**
	 * Holds Resource Link to ping the server for keeping the session alive at the server
	 */
	@JsonProperty("ping")
	public ReceivedUrl ping;

}
