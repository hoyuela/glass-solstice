package com.discover.mobile.bank.services.payee;

import com.fasterxml.jackson.annotation.JsonProperty;




/**
 * Class used for holding information for an Unmanaged Payee that will be added to a user's list of payees
 * using the Add Payee Bank web-service API (POST /api/payments/payees).
 * 
 * The AddPayeeServiceCall class uses this information stored in this object to formulate a JSON HTTP Post Request 
 * to the Add Payee Bank API. 
 * 
 * POST /api/payments/payees HTTP/1.1
 * Authorization: BankBasic QZxhqGElbjdglGVuITNls2FtEQ== 
 * Content-Type: application/json
 * {
 *		"name" : "Young America Realty",
 *		"nickName" : "Rent Bill",
 *		"phone" : "309-123-4567",
 *		"addressLine1" : "101 S Main Street",
 *		"addressLine2" : "Suite 18",
 *		"addressCity" : "Normal",
 *		"addressState" : "IL",
 *		"addressZip" : "61761",
 *		"isVerified" : false
 * }
 * @author henryoyuela
 *
 */
public class AddUnmanagedPayee extends AddPayeeDetail {

	/**
	 * Auto-generated serial UID which is used to serialize and de-serialize AddUnmanagedPayee objects
	 */
	private static final long serialVersionUID = 1374274275713260554L;

	/**
	 * Holds the name of the Address Line 1 field in a JSON request.
	 */
	public static final String NAME_ADDRESS_LINE1 = "addressLine1";
	/**
	 * Holds the name of the Address Line 2 field in a JSON request.
	 */
	public static final String NAME_ADDRESS_LINE2 = "addressLine2";
	/**
	 * Holds the name of the City field in a JSON request.
	 */
	public static final String NAME_ADDRESS_CITY = "addressCity";
	/**
	 * Holds the name of the State field in a JSON request.
	 */
	public static final String NAME_ADDRESS_STATE = "addressState";
	/**
	 * Holds the name of the Zip field in a JSON request.
	 */
	public static final String NAME_ADDRESS_ZIP = "addressZip";
	/**
	 * Holds the name of the Memo field in a JSON request.
	 */
	public static final String NAME_MEMO = "memo";
	
	@JsonProperty("addressLine1")
	public String addressLine1;
	
	@JsonProperty("addressLine2")
	public String addressLine2;
	
	@JsonProperty("addressCity")
	public String addressCity;
	
	@JsonProperty("addressState")
	public String addressState;
	
	@JsonProperty("addressZip")
	public String addressZip;
	
	public AddUnmanagedPayee() {
		super();
		
		addressLine1 = "";
		addressLine2 = "";
		addressCity = "";
		addressState = "";
		addressZip = "";
		
	}
}
