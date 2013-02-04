package com.discover.mobile.common.bank.payment;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.discover.mobile.common.bank.payee.PayeeDetail;
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
 *{
 *   "id": "20121006121122",
 *   "description": "Payment to Comcast",
 *    "amount": 8649,
 *    "status": "SCHEDULED",
 *    "dates": {
 *        "deliverBy": {
 *            "date": "2012-10-06T00:00:00Z",
 *           "formattedDate": "10/06/2012"
 *       }
 *   },
 *   "payee": {
 *		"id": "000001",
 *		"name": "Comcast",
 *		"nickName": "Mom's Comcast",
 *		"accountNumber": "******1114",
 *		"earliestPaymentDate": "2013-01-30T05:00:00.000+0000",
 *		"isVerified": true,
 *		"phone": "800.841.3000",
 *		"links": [
 *			"self" : {
 *				"ref": "https://beta.discoverbank.com/api/payees/000001",
 *				"allowed": ["GET"]
 *			},
 *			"update" : {
 *				"ref" : "https://beta.discoverbank.com/api/payees/000001/put",
 *				"allowed" : ["POST"]
 *			},
 *			"delete" : {
 *				"ref" : "https://beta.discoverbank.com/api/payees/000001/delete",
 *				"allowed" : ["POST"]
 *			}
 *		]
 *	},
 *    "paymentAccount": {
 *        "ending": "1111",
 *        "id": 1,
 *        "name": "Discover Cashback Checking",
 *        "nickname": "My Rewards Checking",
 *        "type": "CHECKING",
 *        "balance": 123456,
 *        "interestRate": {
 *            "numerator": 6,
 *            "denominator": 100,
 *            "formatted": "0.06%"
 *        },
 *        "interestEarnedLastStatement": 123,
 *        "interestYearToDate": 4321,
 *        "openDate": "2007-04-06T16: 14: 24.134455Z",
 *        "status": "OPEN",
 *        "links": {
 *            "self": {
 *                "ref": "https://www.discoverbank.com/api/accounts/1",
 *                "allowed": ["GET"]
 *            }
 *        }
 *    },
 *    "confirmationNumber": "F123-7H2Z",
 *    "memo": "service upgrade",
 *	
 *	"links": [
 *			"self" : {
 *				"ref": "https://beta.discoverbank.com/api/payment/20121006121122",
 *				"allowed": ["GET"]
 *			},
 *			"update" : {
 *				"ref" : "https://beta.discoverbank.com/api/payment/20121006121122/put",
 *				"allowed" : ["POST"]
 *			},
 *			"delete" : {
 *				"ref" : "https://beta.discoverbank.com/api/payment/20121006121122/delete",
 *				"allowed" : ["POST"]
 *			}
 *		]
 *}
 * 
 * @author jthornton
 *
 */
public class PaymentDetail implements Serializable{

	private static final long serialVersionUID = -1968961896410192306L;

	@JsonProperty("id")
	public String id;

	@JsonProperty("description")
	public String description;

	/**
	 * Numeric value representing the monetary change for the activity, represented in cents.
	 * A positive value denotes a credit, while a negative value denotes a debit.
	 */
	@JsonProperty("amount")
	public int amount;

	@JsonProperty("status")
	public String status;

	@JsonProperty("dates")
	public List<PaymentDateDetail> dates;

	@JsonProperty("payee")
	public PayeeDetail payee;

	@JsonProperty("paymentAccount")
	public PaymentAccountDetail paymentAccount;

	@JsonProperty("confirmationNumber")
	public String confirmationNumber;

	@JsonProperty("memo")
	public String memo;

	/**
	 * Contains Bank web-service API Resource links
	 */
	@JsonProperty("links")
	public Map<String, ReceivedUrl> links = new HashMap<String, ReceivedUrl>();

}
