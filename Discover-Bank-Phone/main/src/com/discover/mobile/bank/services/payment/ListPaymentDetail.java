package com.discover.mobile.bank.services.payment;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Json object that that holds a list of payment details that the user can review.
 * 
 * JSON response example:
 * 
 * {
 *    "id": "20121006121122",
 *    "description": "Payment to Comcast",
 *    "amount": 8649,
 *    "status": "SCHEDULED",
 *    "dates": {
 *        "deliverBy": {
 *            "date": "2012-10-06T00:00:00Z",
 *            "formattedDate": "10/06/2012"
 *       }
 *    },
 *    "payee": {
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
 *   "paymentAccount": {
 *      "ending": "1111",
 *       "id": 1,
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
}
 * @author jthornton
 *
 */
public class ListPaymentDetail implements Serializable{

	/**Unique identifier for the class*/
	private static final long serialVersionUID = -6747095973097739750L;

	/**Static String used to narrow the payment results*/
	public static final String ALL = "ALL";

	/**Static String used to narrow the payment results*/
	public static final String SCHEDULED = "SCHEDULED";

	/**Static String used to narrow the payment results*/
	public static final String CANCELLED = "CANCELLED";

	/**Static String used to narrow the payment results*/
	public static final String COMPLETED = "COMPLETED";

	/**List of payments for the user to review*/
	@JsonProperty("Payments")
	List<PaymentDetail> payments;
}
