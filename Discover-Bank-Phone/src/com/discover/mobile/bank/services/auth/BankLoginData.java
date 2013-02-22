package com.discover.mobile.bank.services.auth;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.discover.mobile.common.net.json.bank.ReceivedUrl;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The base class for storing the bank login data that is returned from the auth
 * api call
 * 
 * @author ajleeds
 * 
 */
public class BankLoginData implements Serializable {

	/**
	 * Auto-generated serial UID which is used to serialize and de-serialize BankLoginData objects
	 */
	private static final long serialVersionUID = 9102671046032430931L;

	@JsonProperty("value")
	public String token;

	@JsonProperty("links")
	public Map<String, ReceivedUrl> links = new HashMap<String, ReceivedUrl>();
	
}
