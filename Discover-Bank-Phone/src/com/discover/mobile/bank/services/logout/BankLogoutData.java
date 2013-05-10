package com.discover.mobile.bank.services.logout;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class used to provide a token value to the BankLogOutCall service call. The instance of this class is serialized and sent as the body of HTTP request sent
 * via BankLogOutCall.
 * 
 * @author henryoyuela
 *
 */
public class BankLogoutData implements Serializable {

	/**
	 * Auto-generated serial UID which is used to serialize and de-serialize BankLogoutData objects
	 */
	private static final long serialVersionUID = -6336982874716389642L;
	
	@JsonProperty("value")
	public String value;

}
