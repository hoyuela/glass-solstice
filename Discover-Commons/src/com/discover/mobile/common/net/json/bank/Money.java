package com.discover.mobile.common.net.json.bank;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 *
 * This class is used for storing a monetary value for Bank API Services, if any, specified as a
 * composite object of a value and 
 * formatted string.
 *  
 * This information is provided as part of every Account in a JSON response to a 
 * Bank Accounts service API invocation via GetCustomerAccountsServerCall.
 * 
 * "balance": {
 *	"value" : 123456,
 *	"formatted" : "$1234.56"
 * }
 * @author henryoyuela
 *
 */
public class Money implements Serializable {

	/**
	 * Auto-generated serial UID which is used to serialize and de-serialize Money objects
	 */
	private static final long serialVersionUID = -4760489756918568654L;

	/**
	 * The money value in its floating point representation
	 */
	@JsonProperty("value")
	public int value;
	
	/**
	 * The money value in its formatted form with a $ as a prefix
	 */
	@JsonProperty("formatted")
	public String formatted;
	
}
