package com.discover.mobile.bank.services.payee;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;

/**
 * Class used for holding information for a Search Result item sent from the Search Payee Bank API Web-service
 * after making a search request using the SearchPayeeResult class.
 * 
 * The following is an example of an Search Result  JSON response:
 * [{
 *	"name": "VERIZON COMMUNICATIONS",
 *	"nickName": "VERIZON COMMUNICATIONS",
 *	"merchantNumber": "19",
 *	"isZipRequired": "true"
 * },
 * {
 *	 "name": "VERIZON WIRELESS",
 *	 "merchantNumber": "VERIZON WIRELESS",
 *	 "id": "2082",
 *	 "isZipRequired": "true"
 * }]
 * 
 * @author henryoyuela
* */
public class SearchPayeeResult implements Serializable {

	/**
	 * Auto-generated serial UID which is used to serialize and de-serialize SearchPayeeResult objects
	 */
	private static final long serialVersionUID = 8237003420261421202L;
	
	@JsonProperty("name")
	public String name;
	
	@JsonProperty("nickName")
	public String nickName;
	
	@JsonProperty("merchantNumber")
	public String merchantNumber;
	
	@JsonProperty("zipRequired")
	public String zipRequired;
	
	/**
	 * @return Returns true is zip code is required when adding the payee with the information held by this object
	 */
	public boolean isZipRequired() {
		return (!Strings.isNullOrEmpty(zipRequired) && zipRequired.equals("true"));
	}

}
