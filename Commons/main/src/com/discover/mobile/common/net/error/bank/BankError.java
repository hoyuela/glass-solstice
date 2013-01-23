package com.discover.mobile.common.net.error.bank;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This is a class that is used for mapping the JSON that is returned for errors on Bank service calls
 * 
 * @author ajleeds
 *
 */
public class BankError {
	
	@JsonProperty("name")
	public String name;
	
	@JsonProperty("code")
	public String code; 
	
	@JsonProperty("message")
	public String message;

}
