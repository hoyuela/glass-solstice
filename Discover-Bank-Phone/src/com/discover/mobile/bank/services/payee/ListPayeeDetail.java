package com.discover.mobile.bank.services.payee;

import java.io.Serializable;
import java.util.List;

/**
 * This is used for storing Payee detailed information provided in a JSON response to a
 * Bank web-service API invocation
 * 
 * The GetPayeeServiceCall class uses this object to store the Payee information.
 * 
 * API call: /api/payees/
 * 
 * JSON Example:
 * 
 *[{
 *	"id": "000001",
 *	"name": "Comcast",
 *	"nickName": "Mom's Comcast",
 *	"accountNumber": "******1114",
 *	"earliestPaymentDate": "2013-01-30T05:00:00.000+0000",
 *	"isVerified": true,
 *	"phone": "800.841.3000",
 *	"links": [
 *		"self" : {
 *			"ref": "https://beta.discoverbank.com/api/payees/000001",
 *			"allowed": ["GET"]
 *		},
 *		"update" : {
 *			"ref" : "https://beta.discoverbank.com/api/payees/000001/put",
 *			"allowed" : ["POST"]
 *		},
 *		"delete" : {
 *			"ref" : "https://beta.discoverbank.com/api/payees/000001/delete",
 *			"allowed" : ["POST"]
 *		}
 *	]
 *},
 *{
 *	"id": "000002",
 *	"name": "Comcast",
 *	"nickName": "My Comcast",
 *	"accountNumber": "******1115",
 *	"earliestPaymentDate": "2013-01-30T05:00:00.000+0000",
 *	"isVerified": true,
 *	"phone": "800.841.3000",
 *	"links": [
 *		"self" : {
 *			"ref": "https://beta.discoverbank.com/api/payees/000002",
 *			"allowed": ["GET"]
 *		},
 *		"update" : {
 *			"ref" : "https://beta.discoverbank.com/api/payees/000002/put",
 *			"allowed" : ["POST"]
 *		},
 *		"delete" : {
 *			"ref" : "https://beta.discoverbank.com/api/payees/000002/delete",
 *			"allowed" : ["POST"]
 *		}
 *	]
 *}]
 * 
 * @author jthornton
 *
 */
public class ListPayeeDetail implements Serializable{


	/**
	 * Auto-generated serial UID which is used to serialize and de-serialize
	 */
	private static final long serialVersionUID = 1L;

	/**List of payees returned*/
	public List<PayeeDetail> payees;

	public String getNameFromId(final String id){
		String name = "";
		for(final PayeeDetail detail : payees){
			if(detail.id.equals(id)){
				name = detail.nickName;
			}
		}
		return name;
	}
	
	/**
	 * Method used to lookup payee in list using the payee's id.
	 * @param id ID of the payee being looked-up
	 * @return null if not found, otherwise reference to the Payee with the id specified.
	 */
	public PayeeDetail getPayeeFromId(final String id){
		PayeeDetail payee = null;
		for(final PayeeDetail detail : payees){
			if(detail.id.equals(id)){
				payee = detail;
				break;
			}
		}
		return payee;
	}
}
