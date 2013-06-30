package com.discover.mobile.bank.services.account;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.bank.services.account.activity.ListActivityDetail;
import com.discover.mobile.bank.services.customer.Customer;
import com.discover.mobile.bank.services.deposit.AccountLimits;
import com.discover.mobile.bank.services.json.Money;
import com.discover.mobile.bank.services.json.Percentage;
import com.discover.mobile.bank.services.json.ReceivedUrl;
import com.discover.mobile.common.utils.StringUtility;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;

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
	public static final String ACCOUNT_CHECKING = "checking";
	/**
	 * Holds a String used to represent an Online Savings type of account
	 */
	public static final String ACCOUNT_SAVINGS = "savings";
	/**
	 * Holds a String used to represent an Money Market type of account
	 */
	public static final String ACCOUNT_MMA = "money_market";
	/**
	 * Holds a String used to represent an CDs type of account
	 */
	public static final String ACCOUNT_CD = "cd";
	/**
	 * Holds a String used to represent an IRA type of account
	 */
	public static final String ACCOUNT_IRA= "ira";
	/**
	 * Holds a String used to represent an Loan type of account
	 */
	public static final String ACCOUNT_LOAN = "loan";
	/**
	 * Holds a String used to fetch the URL used to downloaded posted activity
	 */
	public static final String LINKS_POSTED_ACTIVITY = "postedActivity";
	/**
	 * Holds a String used to fetch the URL used to downloaded posted activity
	 */
	public static final String LINKS_SCHEDULED_ACTIVITY = "scheduledActivity";
	/**
	 * Holds the formatted string for the loan type of account.
	 */
	public static final String ACCOUNT_LOAN_FORMATTED = "Personal Loan:";
	
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
	
	/**Type of activity*/
	@JsonProperty("typeLabel")
	public String typeLabel;

	/**
	 * The monetary value that is available for withdrawl at this instant,
	 * represented in cents. (e.g. 12345 would be $123.45)
	 */
	@JsonProperty("balance")
	public Money balance;

	/**Current Balance of activity*/
	@JsonProperty("currentBalance")
	public Money currentBalance;
	
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
	@JsonProperty("interestEarnedYearToDate")
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
	@JsonProperty("nextPaymentDueDate")
	public String nextPaymentDueDate;

	/**
	 * The original balance
	 */
	@JsonProperty("originalBalance")
	public Money originalBalance;

	/**
	 * The last payment amount received
	 * */
	@JsonProperty("lastPaymentReceivedAmount")
	public Money lastPaymentReceivedAmount;

	/**
	 * The total payment amount due for a loan
	 * */
	public Money totalAmountDue;

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
	 * Holds a reference to limits for this account
	 */
	public AccountLimits limits;

	/**
	 * Holds list of joint owners for this account
	 */
	@JsonProperty("jointOwners")
	public List<Customer> jointOwners;

	@JsonProperty("serviceAccountId")
	public String serviceAccountId;

	/**
	 * Contains Bank web-service API Resource links for postedActivity and scheduledActivity
	 */
	@JsonProperty("links")
	public Map<String, ReceivedUrl> links = new HashMap<String, ReceivedUrl>();

	/**Posted activity of the account*/
	public ListActivityDetail posted;

	/**Scheduled activity of the account*/
	public ListActivityDetail scheduled;

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

		if (!Strings.isNullOrEmpty(type)) {
			// Group for Checking: Holds only Checking Types
			if (type.equalsIgnoreCase(Account.ACCOUNT_CHECKING)) {
				ret = Account.ACCOUNT_CHECKING;
			}
			// Group for Savings: Holds Online Savings, MMA, CDs
			else if (type.equalsIgnoreCase(Account.ACCOUNT_SAVINGS) || type.equalsIgnoreCase(Account.ACCOUNT_MMA)
					|| type.equalsIgnoreCase(Account.ACCOUNT_CD)) {
				ret = Account.ACCOUNT_SAVINGS;

			}
			// Group for Retirement Plans: Holds IRA, IRA CDs
			else if (type.equalsIgnoreCase(Account.ACCOUNT_IRA)) {
				ret = Account.ACCOUNT_IRA;
			}
			// Group Personal Loans: Personal Loans
			else if (type.equalsIgnoreCase(Account.ACCOUNT_LOAN)) {
				ret = Account.ACCOUNT_LOAN;
			} else {
				ret = type;
			}
		}

		return ret;
	}

	/**
	 * @return Returns true if account is eligible for check deposit, false otherwise.
	 */
	public boolean isDepositEligible() {
		boolean isDepositEligible = false;
		if(type != null) {
			isDepositEligible = type.equalsIgnoreCase(ACCOUNT_CHECKING) ||
					type.equalsIgnoreCase(ACCOUNT_SAVINGS) ||
					type.equalsIgnoreCase(ACCOUNT_MMA);
		}

		return isDepositEligible;
	}

	/**
	 * 
	 * @return a String containing the account nickname and 
	 * 	last 4 digits of the account in the format "{nickname} (...XXXX)" where XXXX is the last 4 digits of 
	 *  the account number.
	 */
	public String getDottedFormattedAccountNumber() {
		return nickname +" (..." +accountNumber.ending +")";
	}
	
	
	/**
	 * 
	 * @return a String containing the last 4 digits of the account in the format "(...XXXX)" where XXXX is the 
     *			last 4 digits of the account number. If there is no account number an empty String is returned.
	 */
	public String getShortDottedFormattedAccountNumber() {
		String dottedFormattedAccountNumber = "";
		
		if(accountNumber != null && !Strings.isNullOrEmpty(accountNumber.ending))
			dottedFormattedAccountNumber = "(..." + accountNumber.ending + ")";
		
		return dottedFormattedAccountNumber;
	}

	/**
	 * @return Returns true if account is eligible for scheduling payment, false otherwise.
	 */
	public boolean canSchedulePayment() {
		return (type.equalsIgnoreCase(ACCOUNT_CHECKING) || type.equalsIgnoreCase(ACCOUNT_MMA));
	}

	/**
	 * 
	 * @return if the current account is eligible for the transfer service.
	 */
	public boolean isTransferEligible() {
		return isExternalAccount() ||
				ACCOUNT_CHECKING.equalsIgnoreCase(type) ||
				ACCOUNT_SAVINGS.equalsIgnoreCase(type) ||
				ACCOUNT_MMA.equalsIgnoreCase(type);

	}

	/**
	 * If the account does not have anything in the type field, it is an external account.
	 * @return
	 */
	public boolean isExternalAccount() {
		return Strings.isNullOrEmpty(type) && !Strings.isNullOrEmpty(id);
	}
	
	/**
	 * @return Returns the type formatted string for the this account.
	 */
	public String getFormattedName() {
		String formattedName = this.typeLabel;

		if (Strings.isNullOrEmpty(formattedName)) {

			if (type.equalsIgnoreCase(ACCOUNT_CD) || type.equalsIgnoreCase(ACCOUNT_IRA)) {
				formattedName = type.toUpperCase(Locale.US) + ":";
			} else if (!type.equalsIgnoreCase(Account.ACCOUNT_LOAN)) {
				formattedName = type.replaceAll("_", " ");
				final StringBuilder result = new StringBuilder(formattedName.length());
				final String[] charArray = formattedName.split("\\s");
				final int l = charArray.length;
				for (int i = 0; i < l; ++i) {
					if (i > 0) {
						result.append(" ");
					}
					result.append(Character.toUpperCase(charArray[i].charAt(0))).append(charArray[i].substring(1));
				}
				result.append(":");
				formattedName = result.toString();
			} else {
				formattedName = ACCOUNT_LOAN_FORMATTED;
			}
		} else {
			formattedName += StringUtility.COLON;
		}
		
		return formattedName;
	}

	@Override
	public String toString() {
		
		if (nickname != null) {
			return nickname;
		} else if (name != null) {
			return name;
		}
		
		// Use default toString implementation
		return getClass().getName() + "@" + Integer.toHexString(hashCode());
	}
	
}
