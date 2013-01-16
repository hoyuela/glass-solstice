package com.discover.mobile.common.auth.bank;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This data class is sent with the Bank login request to build the JSON that is
 * sent with that request.
 * 
 * @author ajleeds
 *
 */
public class BankLoginDetails {

	@JsonProperty("username")
	public String username;
	@JsonProperty("password")
	public String password;
}
