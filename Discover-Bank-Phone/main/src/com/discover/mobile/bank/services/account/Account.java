package com.discover.mobile.bank.services.account;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.discover.mobile.common.net.json.bank.ReceivedUrl;
import com.discover.mobile.common.urlmanager.BankUrlManager;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class is used for storing Account information provided in a JSON response to a 
 * Bank Accounts service API invocation via GetCustomerAccountsServerCall.
 * 
 * {
 *   "id": 1,
 *   "accountNumber" : {
 *		"ending" : "1111",
 *		"formatted" : "*****1111"
 *	 },
 *   "name": "Discover Cashback Checking",
 *   "nickname": "My Rewards Checking",
 *   "type": "CHECKING",
 *   "balance": {
 *		"value" : 123456,
 *		"formatted" : "$1234.56"
 *	 },
 *   "interestRate": {
 *         "numerator": 6,
 *         "denominator":  100,
 *         "formatted" : "0.06%"
 *   }
 *  "interestEarnedLastStatement": {
 *		"value" : 123,
 *		"formatted" : "$1.23"
 *	},
 *	"interestYearToDate": {
 *		"value" : 4321,
 *		"formatted" : "$43.21"
 *	},
 *	"openDate": {
 *		"date": "2007-04-06T16:14:24.134455Z",
 *		"formattedDate": "04/06/2007"
 *	},
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
	 * Holds a String sued to represent a Checking Account
	 */
	public static final String ACCOUNT_CHECKING = "Checking";
	/**
	 * Holds a String used to represent an Online Savings type of account
	 */
	public static final String ACCOUNT_SAVINGS = "Savings";
	/**
	 * Holds a String used to represent an Money Market type of account
	 */
	public static final String ACCOUNT_MMA = "Money Market";
	/**
	 * Holds a String used to represent an CDs type of account
	 */
	public static final String ACCOUNT_CDS = "CD";
	/**
	 * Holds a String used to represent an IRA type of account
	 */
	public static final String ACCOUNT_IRA= "IRA";
	/**
	 * Holds a String used to represent an Loan type of account
	 */
	public static final String ACCOUNT_LOANS = "Loan";
	/**
	 * Holds a String used to fetch the URL used to downloaded posted activity
	 */
	public static final String LINKS_POSTED_ACTIVITY = "postedActivity";
	/**
	  * Holds a String used to fetch the URL used to downloaded posted activity
	 */
	public static final String LINKS_SCHEDULED_ACTIVITY = "scheduledActivity";
	
	/**
	 * The last four digits of the account number (e.g. 'ending in 1111).
	 */
	@JsonProperty("accountNumber")
	public AccountNumber accountNumber;
	
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
	public Money balance;
	
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
	public Money interestEarnedLastStatement;
	
	/**
	 * The amount of interest that was compounded and added to the account balance 'Year to Date', 
	 * represented in cents. (e.g. 12345 would be $123.45)
	 */
	@JsonProperty("interestYearToDate")
	public Money interestYearToDate;

	/**
	 * The timestamp of when the account was opened. Follows ISO 8601, represented in UTC
	 */
	@JsonProperty("openDate")
	public OpenDate openDate;
	
	/**
	 * The status of the account (e.g. 'OPEN' or 'CLOSED')
	 */
	@JsonProperty("status")
	public String status;
	

	/**
	 * Contains Bank web-service API Resource links for postedActivity and scheduledActivity
	 */
	@JsonProperty("links")
	public Map<String, ReceivedUrl> links = new HashMap<String, ReceivedUrl>();
	
	/**
	 * Read URL from hash-map of links stored in links using a Key.
	 * 
	 * @param Key Can be either LINKS_POSTED_ACTIVITY and LINKS_SCHEDULED_ACTIVITY
	 * @return Returns a ReceivedUrl object which holds the URL link
	 */
	public String getLink(final String Key) {
		
		return BankUrlManager.getUrl(links, Key);
	}
	
}
