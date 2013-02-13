package com.discover.mobile.bank.services.account;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *  This class is used for storing the timestamp of when the account was opened. 
 *  Follows ISO 8601, represented in UTC, if any, specified as a composite object of a date 
 *  and formattedDate string.
 *  
 * This information is provided as part of every Account in a JSON response to a 
 * Bank Accounts service API invocation via GetCustomerAccountsServerCall.
 * 
 * 
 * "openDate": {
 *		"date": "2007-04-06T16:14:24.134455Z",
 *		"formattedDate": "04/06/2007"
 * }
 * @author henryoyuela
 *
 */
public class OpenDate implements Serializable {

	/**
	 * Auto-generated serial UID which is used to serialize and de-serialize OpenDate objects
	 */
	private static final long serialVersionUID = 1420312469235921895L;

	/**
	 * The the timestamp of when the account was opened in UTC format
	 */
	@JsonProperty("value")
	public String value;
	
	/**
	 * The the timestamp of when the account was opened in formatted String following mm/dd/yyyy
	 */
	@JsonProperty("formattedDate")
	public String formatted;
}
