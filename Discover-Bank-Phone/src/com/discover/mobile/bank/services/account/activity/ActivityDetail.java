package com.discover.mobile.bank.services.account.activity;

import java.io.Serializable;

import com.discover.mobile.bank.services.account.Account;
import com.discover.mobile.bank.services.payee.PayeeDetail;
import com.discover.mobile.common.net.json.bank.Money;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Object holding the details about the activity of the account
 * 
 *  * API Call: /api/accounts/{id}/activity
 * 
 * JSON Example:
 * 
 * {
 * 	"id": "0",
 * 	"description": "INTEREST PAID",
 * 	"amount": {
 * 		"value": 681,
 * 		"formatted": "$6.81"
 * 	},
 * 	"balance": {
 * 		"value": 1002634,
 * 		"formatted": "$10,026.34"
 * 	},
 * 	"status": "POSTED",
 * 	"dates": {
 * 		"posted": "2013-01-31T06:00:00.000+0000"
 * 	}
 * }
 * 
 * @author jthornton
 *
 */
public class ActivityDetail implements Serializable{

	/**Unique identifier*/
	private static final long serialVersionUID = -180698452175670553L;

	/**Key to get posted date*/
	public static final String POSTED = "posted";

	/**Key to signify that the activity is scheduled*/
	public static final String SCHEDULED = "scheduled";

	/**Key to indicate that the type of scheduled activity is a payment*/
	public static final String TYPE_PAYMENT = "payment";

	/**Key to indicate that the type of scheduled activity is a transfer*/
	public static final String TYPE_TRANSFER = "transfer";

	/**Key to indicate that the type of scheduled activity is a deposit*/
	public static final String TYPE_DEPOSIT = "deposit";

	/**Key of the date divider*/
	public static final String DATE_DIVIDER = "T";

	/**Id for the activity*/
	@JsonProperty("id")
	public String id;

	/**Description of the activity*/
	@JsonProperty("description")
	public String description;

	/**Amount of the activity*/
	@JsonProperty("amount")
	public Money amount;

	/**Date associated with the activity*/
	@JsonProperty("postedDate")
	public String postedDate;

	/**Balance of activity*/
	@JsonProperty("balance")
	public Money balance;

	/**Type of activity*/
	@JsonProperty("type")
	public String type;

	/**Status of the activity*/
	@JsonProperty("status")
	public String status;

	/**Activity date string*/
	@JsonProperty("activityDate")
	public String activityDate;

	/**Payment Activity Fields*/

	/**Payee detail*/
	@JsonProperty("payee")
	public PayeeDetail payee;

	/**Account for the payment*/
	@JsonProperty("paymentAccount")
	public Account paymentAccount;

	/**Delivery date object*/
	@JsonProperty("deliverByDate")
	public String deliverByDate;

	/**Payment confirmation number*/
	@JsonProperty("confirmationNumber")
	public String confirmationNumber;

	/**Payment memo*/
	@JsonProperty("memo")
	public String memo;

	/**Transfer Activity Fields*/

	/**From account*/
	@JsonProperty("fromAccount")
	public Account fromAccount;

	/**To account*/
	@JsonProperty("toAccount")
	public Account toAccount;

	/**Send date*/
	@JsonProperty("sendDate")
	public String sendDate;

	/**Delivery by date*/
	@JsonProperty("deliverBy")
	public String deliverBy;

	/**Frequency*/
	@JsonProperty("frequency")
	public String frequency;

	/**Duration type*/
	@JsonProperty("durationType")
	public String durationType;

	/**Duration value*/
	@JsonProperty("durationValue")
	public String durationValue;

	/**Deposit Activity Fields*/

	/**Account for the Deposit*/
	@JsonProperty("account")
	public Account account;

	/**Schedule date*/
	@JsonProperty("scheduledDate")
	public String scheduledDate;

	/**Confirmation string*/
	@JsonProperty("confirmation")
	public String confirmation;

	/**
	 * Return the date that needs to be shown in the table
	 * @return the date that needs to be shown in the table
	 */
	public String getTableDisplayDate() {
		String date = "";
		if(null == status){
			date = postedDate;
		}else if(type.equalsIgnoreCase(ActivityDetail.TYPE_PAYMENT)){
			date = activityDate;
		} else if(type.equalsIgnoreCase(ActivityDetail.TYPE_TRANSFER)){
			date = activityDate;
		}else if(type.equalsIgnoreCase(ActivityDetail.TYPE_DEPOSIT)){
			date = activityDate;
		}else{
			date = postedDate;
		}
		return date;
	}

}
