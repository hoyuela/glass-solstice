package com.discover.mobile.common.bank.payee;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.discover.mobile.common.net.json.bank.ReceivedUrl;
import com.fasterxml.jackson.annotation.JsonProperty;

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
 *  "id": "000001",
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
 * 
 * @author jthornton
 *
 */
public class PayeeDetail implements Serializable{

	/**Unique id for the object*/
	private static final long serialVersionUID = 1L;
	
	/**Id for the payee*/
	@JsonProperty("id")
	private String id;
	
	/**Payee name*/
	@JsonProperty("name")
	private String name;
	
	/**Payee nickname*/
	@JsonProperty("nickName")
	private String nickName;
	
	/**Account number for the payee*/
	@JsonProperty("accountNumber")
	private String account;
	
	/**Earliest payment date*/
	@JsonProperty("earliestPaymentDate")
	private String paymentDate;
	
	/**Boolean for if the payee is verified*/
	@JsonProperty("isVerified")
	private boolean verified;
	
	/**Payee phone number*/
	@JsonProperty("phone")
	private String phone;
	
	/**
	 * Contains Bank web-service API Resource links
	 */
	@JsonProperty("links")
	public Map<String, ReceivedUrl> links = new HashMap<String, ReceivedUrl>();

}
