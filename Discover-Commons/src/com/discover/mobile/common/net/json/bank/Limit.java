package com.discover.mobile.common.net.json.bank;

import java.io.Serializable;

import com.discover.mobile.common.net.error.bank.BankError;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The maximum and remaining values for a particular type of limit.
 * 
 * Example of JSON:
 * "dailyDepositAmount": {
 *		"limit": 500000,
 *		"remaining": 500000,
 *		"error" : {
 *			"code" : "Deposits.Limits.DailyAmount",
 *			"message" : "We're sorry. This deposit amount will exceed the total limit on this account of {amount} per day."
 *		}
 *	}
 *
 * @author henryoyuela
 *
 */
public class Limit implements Serializable {
	/**
	 *  Auto-generated serial UID which is used to serialize and de-serialize Limit objects
	 */
	private static final long serialVersionUID = -5136394491521377914L;
	/**
	 * The maximum amount allowed.
	 */
	@JsonProperty("limit")
	public int limit;
	/**
	 * The amount remaining in cents.
	 */
	@JsonProperty("remaining")
	public int remaining;
	/**
	 * The message to be displayed if the limit is exceeded.
	 */
	@JsonProperty("error")
	public BankError error;
	
	/**
	 * Method used to check if the limit has been reached.
	 * 
	 * @return True if limit has been exceeded or met, false otherwise.
	 */
	public boolean hasReachedLimit() {
		return (remaining <= 0 );
	}
}
