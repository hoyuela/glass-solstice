package com.discover.mobile.bank.services.account;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class is used for storing the current interest rate for an account, if any, specified 
 * as a composite object of a numerator, denominator, and a preferred display string.
 *  
 * This information is provided as part of every Account in a JSON response to a 
 * Bank Accounts service API invocation via GetCustomerAccountsServerCall.
 * 
 * "interestRate": {
 *         "numerator": 6,
 *         "denominator":  100,
 *         "formatted" : "0.06%"
 *   }
 *   
 * @author henryoyuela
 *
 */
public class InterestRate implements Serializable {
	/**
	 * Auto-generated serial UID which is used to serialize and de-serialize Customer objects
	 */
	private static final long serialVersionUID = 2436034484547418609L;

	@JsonProperty("numerator")
	public String numerator;
	
	@JsonProperty("denominator")
	public String denominator;
	
	@JsonProperty("formatted")
	public String formatted;
}
