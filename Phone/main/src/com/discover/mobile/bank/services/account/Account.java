package com.discover.mobile.bank.services.account;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.discover.mobile.common.net.json.bank.ReceivedUrl;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class is used for storing Account information provided in a JSON response to a 
 * Bank Accounts service API invocation via GetCustomerAccountsServerCall.
 * 
 * {
 *   "ending": "1111",
 *   "id": 1,
 *   "name": "Discover Cashback Checking",
 *   "nickname": "My Rewards Checking",
 *   "type": "CHECKING",
 *   "balance": 123456,
 *   "interestRate": {
 *         "numerator": 6,
 *         "denominator":  100,
 *         "formatted" : "0.06%"
 *   }
 *   "interestEarnedLastStatement": 123,
 *   "interestYearToDate": 4321,
 *   "openDate": 2007-04-06T16:14:24.134455Z
 *   "status" : "OPEN"
 *   "links": {
 *       "self": {
 *           "ref": "https://www.discoverbank.com/api/accounts/1",
 *            "allowed": [ "GET", "POST" ] 
 *       }
 *
 *        "postedActivity": {
 *            "ref": "https://www.discoverbank.com/api/accounts/1/activity?status=posted",
 *            "allowed": [ "GET" ]
 *        },
 *        "scheduledActivity": {
 *            "ref": "https://www.discoverbank.com/api/accounts/1/activity?status=scheduled",
 *            "allowed": [ "GET" ]
 *        }
 *
 *    }
 *}
 * @author henryoyuela
 *
 */
public class Account implements Serializable {
	/**
	 * Auto-generated serial UID which is used to serialize and de-serialize Account objects
	 */
	private static final long serialVersionUID = 2673259114583039084L;

	/**
	 * The last four digits of the account number (e.g. 'ending in 1111).
	 */
	@JsonProperty("ending")
	public String ending;
	
	/**
	 * The account id in the list of all accounts for this Banking Customer.
	 */
	@JsonProperty("id")
	public String id;
	
	/**
	 * The name of the account. This generally derived from its account type.
	 */
	@JsonProperty("name")
	public String name;
	
	/**
	 * The nick name of the account, given by the Banking Customer.
	 */
	@JsonProperty("nickname")
	public String nickname;
	
	/**
	 * The account type code which signifies which type of account this is.
	 * 
	 */
	@JsonProperty("type")
	public String type;
	
	/**
	 * The monetary value that is available for withdrawl at this instant, 
	 * represented in cents. (e.g. 12345 would be $123.45)
	 */
	@JsonProperty("balance")
	public String balance;
	
	/**
	 * The current interest rate for the account, if any, specified as a 
	 * composite object of a numerator, denomenator, and a preferred display string.
	 */
	@JsonProperty("interestRate")
	public InterestRate interestRate;
	
	/**
	 * The amount of interest that was compounded and added to the account balance 
	 * from last the last statement, represented in cents.(e.g. 12345 would be $123.45)
	 */
	@JsonProperty("interestEarnedLastStatement")
	public String interestEarnedLastStatement;
	
	/**
	 * The amount of interest that was compounded and added to the account balance 'Year to Date', 
	 * represented in cents. (e.g. 12345 would be $123.45)
	 */
	@JsonProperty("interestYearToDate")
	public String interestYearToDate;

	/**
	 * The timestamp of when the account was opened. Follows ISO 8601, represented in UTC
	 */
	@JsonProperty("openDate")
	public String openDate;
	
	/**
	 * The status of the account (e.g. 'OPEN' or 'CLOSED')
	 */
	@JsonProperty("status")
	public String status;
	
	public static final String ACCOUNT_IRA= "IRA";
	public static final String ACCOUNT_MONEYMARKET= "MONEYMARKET";
	public static final String ACCOUNT_CHECKING = "CHECKING";
	/**
	 * Contains Bank web-service API Resource links for postedActivity and scheduledActivity
	 */
	@JsonProperty("links")
	public Map<String, ReceivedUrl> links = new HashMap<String, ReceivedUrl>();

}
