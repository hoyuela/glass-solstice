package com.discover.mobile.common.auth.registration;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BankLoginDetails {

	@JsonProperty("username")
	public String username;
	@JsonProperty("password")
	public String password;
}
