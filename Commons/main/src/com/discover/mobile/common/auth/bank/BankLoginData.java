package com.discover.mobile.common.auth.bank;

import java.io.Serializable;

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
	public Links links;

}
