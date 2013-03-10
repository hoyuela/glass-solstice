package com.discover.mobile.common.net.error.bank;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This is a class that is used for mapping the JSON that is returned for errors on Bank service calls
 * 
 * @author ajleeds
 *
 */
public class BankError implements Serializable{
	
	/**
	 * Auto-generated serial UID which is used to serialize and de-serialize BankError objects
	 */
	private static final long serialVersionUID = -3685256863066024211L;

	@JsonProperty("name")
	public String name;
	
	@JsonProperty("code")
	public String code; 
	
	@JsonProperty("message")
	public String message;

}
