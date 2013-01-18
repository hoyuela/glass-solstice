package com.discover.mobile.common.auth.bank;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BankErrorData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6852563410888046737L;
		
	@JsonProperty("lastname")
	public String lastname;
	
	@JsonProperty("username")
	public String username;
}
