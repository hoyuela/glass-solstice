package com.discover.mobile.common.auth.bank;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The base class for storing the bank login data that is returned. 
 * 
 * @author ajleeds
 * 
 */
public class BankLoginData {

	@JsonProperty("value")
	public String token;

	@JsonProperty("links")
	public Links links;

}
