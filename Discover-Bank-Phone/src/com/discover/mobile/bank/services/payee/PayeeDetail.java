package com.discover.mobile.bank.services.payee;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.discover.mobile.bank.services.account.AccountNumber;
import com.discover.mobile.bank.services.json.PhoneNumber;
import com.discover.mobile.bank.services.json.ReceivedUrl;
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
 *	"merchantNumber": -999,
 *	"accountNumber": {
 *      "ending": "6789",
 *      "formatted": "****6789",
 *      "unmaskedAccountNumber": "23456789"
 *  },
 *	"earliestPaymentDate": "2013-01-30T05:00:00.000+0000",
 *  "phoneNumber": {
 *   	"number": "8955464564",
 *      "type": "BUSINESS",
 *      "formatted": "8955464564"
 *  },
 *  "address": {
 * 		"streetAddress": "TX",
 *      "postalCode": "75015null",
 *      "type": "BUSINESS"
 *  },	
 *  "verified": true,
 *	"links": [
 *		"self" : {
 *			"ref": "https://beta.discoverbank.com/api/payees/000001",
 *			 "allowed": ["GET","POST","DELETE"]
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

	@JsonProperty("merchantNumber")
	public String merchantNumber;
	
	/**Account number for the payee*/
	@JsonProperty("accountNumber")
	public AccountNumber account;

	/**Earliest payment date*/
	@JsonProperty("earliestPaymentDate")
	public String paymentDate;

	/**Boolean for if the payee is verified*/
	@JsonProperty("verified")
	public boolean verified;

	/**Payee phone number*/
	@JsonProperty("phoneNumber")
	public PhoneNumber phone;

	/**Payee address for Unmanaged payees*/
	@JsonProperty("address")
	public Address address;
	
	/**Payee Postal Code for Managed Payees*/
	@JsonProperty("billingPostalCode")
	public String zip;

	/**Flag used to determine wheter zip is required or not*/
	@JsonProperty("zipRequired")
	public boolean isZipRequired;
	
	/**Payee memo, for Unmanaged payees*/
	@JsonProperty("memo")
	public String memo;

	/**
	 * Contains Bank web-service API Resource links
	 */
	@JsonProperty("links")
	public Map<String, ReceivedUrl> links = new HashMap<String, ReceivedUrl>();
}
