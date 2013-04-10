package com.discover.mobile.bank.services.deposit;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.discover.mobile.bank.services.json.Limit;
import com.discover.mobile.bank.services.json.ReceivedUrl;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * The customer's limits for depositing checks into a particular account.
 * {
 *		"index" : 2,
 *		"monthlyDepositCount": {
 *			"limit": 50,
 *			"remaining": 45,
 *			"error" : {
 *				"code" : "Deposits.Limits.MontlyCount",
 *				"message" : "We're sorry. This deposit will exceed the limit on this account of {amount} deposit items per rolling-30-days."
 *			}
 *		},
 *		"monthlyDepositAmount": {
 *			"limit": 2500000,
 *			"remaining": 2388000,
 *			"error" : {
 *				"code" : "Deposits.Limits.MonthlyAmount",
 *				"message" : "We're sorry. This deposit amount will exceed the total limit on this account of {amount} per rolling-30-days."		
 *			}
 *		},
 *		"dailyDepositCount": {
 *			"limit": 15,
 *			"remaining": 15,
 *			"error" : {
 *				"code" : "Deposits.Limits.DailyCount",
 *				"message" : "We're sorry. This deposit will exceed the limit on this account of {amount} deposit items per day."
 *			}
 *		},
 *		"dailyDepositAmount": {
 *			"limit": 500000,
 *			"remaining": 500000,
 *			"error" : {
 *				"code" : "Deposits.Limits.DailyAmount",
 *				"message" : "We're sorry. This deposit amount will exceed the total limit on this account of {amount} per day."
 *			}
 *		},
 *		"depositAmount": {
 *			"limit": 500000,
 *			"remaining": 500000,
 *			"error" : {
 *				"code" : "Deposits.Limits.SingleAmount",
 *				"message" : "We're sorry. This deposit amount exceeds the limit on this account of {amount} per deposit."
 *			}
 *		},
 *		"links" : {
 *			"self" : {
 *				"ref" : "https://www.discoverbank.com/api/deposits/limits/2",
 *				"allowed" : [ "GET" ]
 *			}
 *		}
 *	} 
 * @author henryoyuela
 *
 */
public class AccountLimits implements Serializable {
	
	/**
	 *  Auto-generated serial UID which is used to serialize and de-serialize AccountLimits objects
	 */
	private static final long serialVersionUID = 4147587313865804681L;

	/**
	 * The account index associated with this set of limits.
	 */
	@JsonProperty("index")
	public int index;
	/**
	 * Number of deposits allowed on this account per month.
	 */
	@JsonProperty("MonthlyDepositCount")
	public Limit monthlyDepositCount;
	/**
	 * Number of deposits allowed on this account per day.
	 */
	@JsonProperty("MonthlyDepositAmount")
	public Limit monthlyDepositAmount;
	/**
	 * Total amount allowed to be deposited in this account per month.
	 */
	@JsonProperty("DailyDepositCount")
	public Limit dailyDepositCount;
	/**
	 * Total amount allowed to be deposited in this account per day.
	 */
	@JsonProperty("DailyDepositAmount")
	public Limit dailyDepositAmount;
	/**
	 * Maximum amount allowed to be deposited in this account per transaction.
	 */
	@JsonProperty("DepositAmount")
	public Limit depositAmount;
	/**
	 * Contains Bank web-service API Resource links
	 */
	@JsonProperty("links")
	public Map<String, ReceivedUrl> links = new HashMap<String, ReceivedUrl>();
	
	/**
	 * A static method that returns if the amount given is valid for the provided account limit.
	 * @param amount an amount to check if it is within certain limits
	 * @param limits the limits used to check against the amount.
	 * @return if the amount is within the limits.
	 */
	public boolean isAmountValid(final double amount) {
		
			return !(monthlyDepositAmount == null || !monthlyDepositAmount.isValidAmount(amount) ||
			/** Verify Number of deposits allowed on this account per month has not been exceeded*/
			monthlyDepositCount == null || monthlyDepositCount.remaining <= 0 ||
			/**Verify Total amount allowed to be deposited in this account per day has not been exceeded*/
			dailyDepositAmount == null || !dailyDepositAmount.isValidAmount(amount) ||					
			
			/**Verify Maximum amount allowed to be deposited in this account per transaction has not been exceeded.*/
			depositAmount == null || !depositAmount.isValidAmount(amount) ||
			
			/**Verify Number of deposits allowed on this account per day has not been exceeded*/
			dailyDepositCount == null || dailyDepositCount.remaining <= 0);				
	}
}

