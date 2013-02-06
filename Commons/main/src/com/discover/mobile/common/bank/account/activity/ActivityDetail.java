package com.discover.mobile.common.bank.account.activity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Object holding the details about the activity of the account
 * 
 *  * API Call: /api/accounts/{id}/activity
 * 
 * JSON Example:
 * 
 * 		{
 *        "id" : "123182309128",
 *        "description" : "CUSTOMER DEPOSIT",
 *        "amount" : 35000,
 *        "dates": {
 *                         "date" : "20120416T00:00:00Z",
 *                         "dateClassifier" : "POSTED_DATE",
 *                         "formattedDate" : " 04/16/2012"
 *                    },
 *        "balance" : 47000,
 *        "transactionType" : "DEPOSIT"
 *     }
 * 
 * @author jthornton
 *
 */
public class ActivityDetail implements Serializable{

	/**Unique identifier*/
	private static final long serialVersionUID = -180698452175670553L;

	/**Id for the actvity*/
	@JsonProperty("id")
	public String id;

	/**Description of the activity*/
	@JsonProperty("description")
	public String description;

	/**Amount of the activity*/
	@JsonProperty("amount")
	public String amount;

	/**Date associated with the activity*/
	@JsonProperty("dates")
	public ActivityDateDetail dates;

	/**Balance of activity*/
	@JsonProperty("balance")
	public int balance;

	/**Type of activity*/
	@JsonProperty("transactionType")
	public String type;

}
