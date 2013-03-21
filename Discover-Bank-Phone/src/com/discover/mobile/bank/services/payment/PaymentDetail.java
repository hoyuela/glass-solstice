package com.discover.mobile.bank.services.payment;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.discover.mobile.bank.services.account.Account;
import com.discover.mobile.bank.services.payee.PayeeDetail;
import com.discover.mobile.common.net.json.bank.Date;
import com.discover.mobile.common.net.json.bank.Money;
import com.discover.mobile.common.net.json.bank.ReceivedUrl;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This is used for storing payment detailed information provided in a JSON response to a
 * Bank web-service API invocation
 * 
 * The GetPayeeServiceCall class uses this object to store the Payee information.
 * 
 * API call: /api/payments
 * 
 * JSON Example:
 * 
  *	 {
 *	        "id": "20130308124154972478",
 *	        "description": "San4",
 *	        "status": "SCHEDULED",
 *	        "amount": {
 *	            "value": 7600,
 *	            "formatted": "$76.00"
 *	        },
 *	        "payee": {
 *	            "id": "00000000006",
 *	            "nickName": "San4"
 *	        },
 *	        "paymentMethod": {
 *	            "id": 10,
 *	            "accountNumber": {
 *	                "ending": "1569",
 *	                "formatted": "****1569",
 *	                "bankFormatted": "523-874156-9",
 *	                "unmaskedAccountNumber": "5238741569"
 *	            },
 *	            "type": "MONEY_MARKET",
 *	            "nickName": "DP MONEY MARKET",
 *	            "jointOwners": [
 *	                {
 *	                    "id": "0001655227",
 *	                    "name": {
 *	                        "givenName": "CLINTON CRAFORD",
 *	                        "formatted": "CLINTON CRAFORD null"
 *	                    },
 *	                    "phoneNumbers": [],
 *	                    "addresses": []
 *	                },
 *	                {
 *	                    "id": "0001656216",
 *	                    "name": {
 *	                        "givenName": "ROBERT DUFFY",
 *	                        "formatted": "ROBERT DUFFY null"
 *	                    },
 *	                    "phoneNumbers": [],
 *	                    "addresses": []
 *	                }
 *	            ]
 *	        },
 *	        "deliverBy": "2013-03-13T04:00:00.000+0000",
 *	        "confirmationNumber": "FLKRZ-2HKHX",
 *	        "jointPayment": true,
 *	        "jointOwnerName": "ROBERT DUFFY null",
 *	        "links": {
 *	            "self": {
 *	                "ref": "/api/payments/20130308124154972478",
 *	                "allowed": [
 *	                    "GET",
 *	                    "POST",
 *	                    "DELETE"
 *	                ]
 *	            }
 *       }
 *    }
 * 
 * @author jthornton
 *
 */
public class PaymentDetail implements Serializable{

	/**Unique identifier for the class*/
	private static final long serialVersionUID = -1968961896410192306L;

	/**Key of the date divider*/
	public static final String DATE_DIVIDER = "T";

	/**Id of the payment*/
	@JsonProperty("id")
	public String id;

	/**Description of the payment*/
	@JsonProperty("description")
	public String description;

	/**
	 * Numeric value representing the monetary change for the activity, represented in cents.
	 * A positive value denotes a credit, while a negative value denotes a debit.
	 */
	@JsonProperty("amount")
	public Money amount;

	/**Status of the payment*/
	@JsonProperty("status")
	public String status;

	/**List of dates associated with the payment*/
	@JsonProperty("dates")
	public Map<String, Date> dates;

	/**Deliver by date*/
	@JsonProperty("deliverBy")
	public String deliverBy;

	/**Deliver by date*/
	@JsonProperty("deliveredOn")
	public String deliveredOn;

	/**Details about the payee*/
	@JsonProperty("payee")
	public PayeeDetail payee;

	/**Payment account*/
	@JsonProperty("paymentMethod")
	public Account paymentAccount;

	/**Payment confirmation number*/
	@JsonProperty("confirmationNumber")
	public String confirmationNumber;

	/**payment memo*/
	@JsonProperty("memo")
	public String memo;

	/**Set to true if payment is for a jointed account*/
	@JsonProperty("jointPayment")
	public boolean isJointPayment;
	
	/**Specifies the joint owner's name*/
	@JsonProperty("jointOwnerName")
	public String jointOwnerName;
	
	/**
	 * Contains Bank web-service API Resource links
	 */
	@JsonProperty("links")
	public Map<String, ReceivedUrl> links = new HashMap<String, ReceivedUrl>();

}
