package com.discover.mobile.bank.services.payee;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.discover.mobile.bank.services.account.AccountNumber;
import com.discover.mobile.common.net.json.bank.Date;
import com.discover.mobile.common.net.json.bank.PhoneNumber;
import com.discover.mobile.common.net.json.bank.ReceivedUrl;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This is used for storing Individual Payee detailed information provided in a JSON response to a
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
	private static final long serialVersionUID = -7210529152877467294L;

	/**Id for the payee*/
	@JsonProperty("id")
	public String id;

	/**Payee name*/
	@JsonProperty("name")
	public String name;

	/**Payee nickname*/
	@JsonProperty("nickName")
	public String nickName;

	/**Account number for the payee*/
	@JsonProperty("accountNumber")
	public AccountNumber account;

	/**Earliest payment date*/
	@JsonProperty("earliestPaymentDate")
	public Date paymentDate;

	/**Boolean for if the payee is verified*/
	@JsonProperty("isVerified")
	public boolean verified;

	/**Payee phone number*/
	@JsonProperty("phone")
	public PhoneNumber phone;
	
	/**Payee address for Unmanaged payees*/
	@JsonProperty("address")
	public Address address;
	
	/**Payee memo, for Unmanaged payees*/
	@JsonProperty("memo")
	public String memo;

	/**
	 * Contains Bank web-service API Resource links
	 */
	@JsonProperty("links")
	public Map<String, ReceivedUrl> links = new HashMap<String, ReceivedUrl>();

}
