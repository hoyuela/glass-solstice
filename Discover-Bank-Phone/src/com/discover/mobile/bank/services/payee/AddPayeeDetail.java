package com.discover.mobile.bank.services.payee;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
  * Class used for holding information for a Managed Payee that will be added to a user's list of payees
  * using the Add Payee Bank web-service API (POST /api/payees).
  * 
  * The AddPayeeServiceCall class uses this information stored in this object to formulate a JSON HTTP Post Request 
  * to the Add Payee Bank API. 
  * 
  * The following is an example of the JSON generated from this object:
  *  {
  *		"name" : "VERIZON WIRELESS",
  *		"nickName" : "Phone Bill",
  *		"accountNumber": "8888888",
  *		"addressZip" : "60070",
  *		"isVerified" : true,
  *		"merchantNumber" : "2082"
  * }
  * 
  * @author henryoyuela
 * */
public class AddPayeeDetail implements Serializable {

	/**
	 * Auto-generated serial UID which is used to serialize and de-serialize AddPayeeDetail objects
	 */
	private static final long serialVersionUID = -554027199107573402L;

	@JsonProperty("name")
	public String name;
	
	@JsonProperty("nickName")
	public String nickName;
	
	@JsonProperty("accountNumber")
	public String accountNumber;
	
	@JsonProperty("addressZip")
	public String zip;
	
	@JsonProperty("isVerified")
	public boolean verified;
	
	@JsonProperty("merchantNumber")
	public String merchantNumber;
}