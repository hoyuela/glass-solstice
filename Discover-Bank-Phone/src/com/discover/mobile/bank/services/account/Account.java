package com.discover.mobile.bank.services.account;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.bank.services.customer.Customer;
import com.discover.mobile.common.net.json.bank.Money;
import com.discover.mobile.common.net.json.bank.Percentage;
import com.discover.mobile.common.net.json.bank.ReceivedUrl;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class is used for storing Account information provided in a JSON response to a 
 * Bank Accounts service API invocation via GetCustomerAccountsServerCall.
 * 
 * {
 * 	"accountNumber": {
 * 		"ending": "5125",
 * 		"formatted": "****5125",
 * 	},
 * 	"id": 2,
 * 	"nickName": "Discover Online Savings Acct",
 * 	"type": "Savings",
 * 	"balance": {
 * 		"value": 20046958,
 * 		"formatted": "$200,469.58"
 * 	},
 * 	"interestRate": {
 * 		"value": 0.008,
 * 		"formatted": "0.80%"
 * 	},
 * 	"apy": { 
 * 		"value": 0.008,
 * 		"formatted": "0.80%"
 * 	},
 * 	"interestEarnedLastStatement": {
 * 		"value": 13616,
 * 		"formatted": "$136.16"
 * 	},
 * 	"interestEarnedYearToDate": {
 * 		"value": 13616,
 * 		"formatted": "$136.16"
 * 	},
 * 	"openDate": "2012-10-17T05:00:00.000+0000",
 * 	"status": "Open",
 *  "jointOwners": [
 *       {
 *           "id": "0001655227",
 *            "name": {
 *               "givenName": "CLINTON CRAFORD",
 *                "formatted": "CLINTON CRAFORD null"
 *            },
 *            "phoneNumbers": [],
 *            "addresses": []
 *        },
 *        {
 *            "id": "0001656216",
 *            "name": {
 *                "givenName": "ROBERT DUFFY",
 *                "formatted": "ROBERT DUFFY null"
 *            },
 *            "phoneNumbers": [],
 *            "addresses": []
 *        }
 *    ]
 *
 * 	"links": {
 * 		"postedActivity": {
 * 			"ref": "/api/accounts/2/activity?status=posted",
 * 			"allowed": [
 * 				"GET"
 * 			]
 * 		},
 * 		"self": {
 * 			"ref": "/api/accounts/2",
 * 			"allowed": [
 * 				"GET"
 * 			]
 * 		}
 * 	}
 * }
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
	public static final String ACCOUNT_CD = "CD";
	/**
	 * Holds a String used to represent an IRA type of account
	 */
	public static final String ACCOUNT_IRA= "IRA";
	/**
	 * Holds a String used to represent an Loan type of account
	 */
	public static final String ACCOUNT_LOAN = "Loan";
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
	@JsonProperty("nickName")
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
	public Percentage interestRate;


	@JsonProperty("apy")
	public Percentage apy;

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
	public String openDate;

	/**
	 * The status of the account (e.g. 'OPEN' or 'CLOSED')
	 */
	@JsonProperty("status")
	public String status;

	/**
	 * The current amount that is due
	 */
	@JsonProperty("currentAmountDue")
	public Money currentAmontDue;

	/**
	 * The date that the next payment is due
	 */
	public String nextPaymentDueDate;

	/**
	 * The original balance
	 */
	public Money originalBalance;

	/**
	 * The last payment amount received
	 * */
	@JsonProperty("lastPaymentReceivedAmount")
	public Money lastPaymentReceivedAmount;

	/**
	 * The length of the CD in months (eg. "24" for 2 year CD)
	 * */
	@JsonProperty("accountTerm")
	public String accountTerm;

	/**
	 * The initial deposit to fund this CD
	 * */
	@JsonProperty("initialDeposit")
	public Money initialDeposit;

	/**
	 * The date at which this CD will fully mature
	 * */
	@JsonProperty("matureDate")
	public String matureDatae;
	
	/**
	 * Holds list of joint owners for this account
	 */
	@JsonProperty("jointOwners")
	public List<Customer> jointOwners;
	
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
	
	/**
	 * Method used to determine the group the Account belongs in
	 * 
	 * @return Returns a string that specifies the group to use for the account.
	 */
	public String getGroupCategory() {
		String ret = "";
		
		//Group for Checking: Holds only Checking Types
		if( this.type.equals(Account.ACCOUNT_CHECKING)) {		
			ret = Account.ACCOUNT_CHECKING;	
		}
		//Group for Savings: Holds Online Savings, MMA, CDs
		else if( this.type.equals(Account.ACCOUNT_SAVINGS) || 
				 this.type.equals(Account.ACCOUNT_MMA) ||
				 this.type.equals(Account.ACCOUNT_CD)) {
			ret = Account.ACCOUNT_SAVINGS;
			
		}
		//Group for Retirement Plans: Holds IRA, IRA CDs
		else if( this.type.equals(Account.ACCOUNT_IRA)) {
			ret = Account.ACCOUNT_IRA;
		}
		//Group Personal Loans: Personal Loans
		else if( this.type.equals(Account.ACCOUNT_LOAN)) {
			ret = Account.ACCOUNT_LOAN;
		} else {
			ret = this.type;
		}
		
		return ret;
	}
	
	/**
	 * @return Returns true if account is eligible for check deposit, false otherwise.
	 */
	public boolean isDepositEligible() {
		return ( type.equals(ACCOUNT_CHECKING) || type.equals(ACCOUNT_SAVINGS) || type.equals(ACCOUNT_MMA));
	}
	
	public String getDottedFormattedAccountNumber() {
		return this.nickname +" (..." +this.accountNumber.ending +")";
	}
	
	/**
	 * @return Returns true if account is eligible for scheduling payment, false otherwise.
	 */
	public boolean canSchedulePayment() {
		return (type.equals(ACCOUNT_CHECKING) || type.equals(ACCOUNT_MMA));
	}

}
