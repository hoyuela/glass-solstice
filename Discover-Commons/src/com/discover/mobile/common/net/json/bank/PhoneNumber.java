package com.discover.mobile.common.net.json.bank;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This is used for storing Phone provided in a JSON response to a 
 * Bank web-service API invocation. 
 * 
 * The following is an example of the JSON:
 * 
 *
 *	"phoneNumbers" : {
 *		"type" : "work",
 *		"number" : "224.405.5446" 
 *	}
 * 
 * @author henryoyuela
 *
 */
public class PhoneNumber implements Serializable {
	/**
	 * Auto-generated serial UID which is used to serialize and de-serialize PhoneNumber objects
	 */
	private static final long serialVersionUID = -2484330274267058158L;
	/**
	 * Contains the type of phone number i.e. work, home etc.
	 */
	@JsonProperty("type")
	public String type;
	/**
	 * Contains the phone number
	 */
	@JsonProperty("number")
	public String number;
	/**
	 * Formatted version of the phone number
	 */
	@JsonProperty("formatted")
	public String formatted;
}
