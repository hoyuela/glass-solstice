package com.discover.mobile.bank.services.payee;

import com.discover.mobile.bank.services.json.PhoneNumber;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
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
 *      "memo" : "this is a payee memo",
 *		"phoneNumber" : {
 *			"number" : "3091234567"
 *		},
 *		"address" : {
 *			"streetAddress" : "101 S Main Street",
 *			"extendedAddress" : "Suite 18",
 *			"locality" : "Normal",
 *			"region" : "IL",
 *			"postalCode" : "61761"
 *		}
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
	public static final String NAME_ADDRESS_LINE1 = "address.streetAddress";
	/**
	 * Holds the name of the Address Line 2 field in a JSON request.
	 */
	public static final String NAME_ADDRESS_LINE2 = "address.extendedAddress";
	/**
	 * Holds the name of the City field in a JSON request.
	 */
	public static final String NAME_ADDRESS_CITY = "address.locality";
	/**
	 * Holds the name of the State field in a JSON request.
	 */
	public static final String NAME_ADDRESS_STATE = "address.region";
	/**
	 * Holds the name of the Zip field in a JSON request.
	 */
	public static final String NAME_ADDRESS_ZIP = "address.postalCode";
	/**
	 * Holds the name of the Memo field in a JSON request.
	 */
	public static final String NAME_MEMO = "memo";
	/**
	 * Holds the name of the phone field in a JSON request.
	 */
	public static final String NAME_PHONE = "phoneNumber.number";

	@JsonProperty("phoneNumber")
	public PhoneNumber phone;
	
	@JsonProperty("address")
	public Address address;
	
	@JsonProperty("memo")
	@JsonInclude(Include.NON_EMPTY)
	public String memo;
	
	public AddUnmanagedPayee() {
		super();
		
		/**Managed Payee fields that are not required, set to null so are not are part 
		 * of the request sent to the server via a NetworkServiceCall<?>*/
		merchantNumber = null;
		accountNumberConfirmed = null;
		zip = null;
		
		address = new Address();
		address.type = null;
		address.region = "";
		address.postalCode = "";
		address.locality = "";
		address.formattedAddress = null;
		address.streetAddress = "";
		address.extendedAddress = "";
		
		phone = new PhoneNumber();	
		phone.number = "";
		phone.formatted = null;
		phone.type = null;
		
		memo = "";	
	}
}
