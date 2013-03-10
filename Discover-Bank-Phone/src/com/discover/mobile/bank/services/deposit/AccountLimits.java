package com.discover.mobile.bank.services.deposit;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.discover.mobile.common.net.json.bank.Limit;
import com.discover.mobile.common.net.json.bank.ReceivedUrl;
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
}
