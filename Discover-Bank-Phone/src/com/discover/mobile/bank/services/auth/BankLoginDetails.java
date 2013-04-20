package com.discover.mobile.bank.services.auth;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This data class is sent with the Bank login request to build the JSON that is
 * sent with that request. Currently we are only sending username and password in 
 * order to login.
 * 
 * @author ajleeds
 *
 */
public class BankLoginDetails implements Serializable {

	/**
	 * Auto-generated serial UID which is used to serialize and de-serialize BankLoginDetails objects
	 */
	private static final long serialVersionUID = 3482967163857273037L;
	@JsonProperty("username")
	public String username;

	@JsonProperty("password")
	public String password;
}
