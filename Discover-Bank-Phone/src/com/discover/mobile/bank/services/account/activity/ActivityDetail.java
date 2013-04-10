package com.discover.mobile.bank.services.account.activity;

import java.io.Serializable;
import java.util.HashMap;

import com.discover.mobile.bank.services.json.Money;
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
	@JsonProperty("dates")
	public HashMap<String, String> dates;

	/**Balance of activity*/
	@JsonProperty("balance")
	public Money balance;

	/**Type of activity*/
	@JsonProperty("transactionType")
	public String type;

}
