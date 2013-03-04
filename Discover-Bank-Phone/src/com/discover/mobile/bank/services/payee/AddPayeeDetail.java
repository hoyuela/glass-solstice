package com.discover.mobile.bank.services.payee;

import java.io.Serializable;
import java.util.List;

import com.discover.mobile.common.net.json.bank.ReceivedUrl;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;

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
  *		"billingPostalCode" : "60070",
  *		"verified" : true,
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
	
	public String accountNumberConfirmed;
//	
	@JsonProperty("billingPostalCode")
	public String zip;
	
	@JsonProperty("verified")
	public boolean verified;
	
	@JsonProperty("merchantNumber")
	public String merchantNumber;
//	
	@JsonProperty("phone")
	public String phone;

	/**
	 * Contains Bank web-service API Resource links
	 */
	@JsonProperty("links")
	public List<ReceivedUrl> links;
	
	public boolean isZipRequired;
	
	public AddPayeeDetail() {
		name = "";
		nickName = "";
		accountNumber = "";
		zip = "";
		verified = false;
		merchantNumber = "";
		phone = "";
		accountNumberConfirmed = "";
	}
	
	public boolean hasZip() {
		return !(Strings.isNullOrEmpty(zip));
	}
}