package com.discover.mobile.common.bank.payee;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Object holding the details about the address of the payee used only when the payee is not verified.
 * 
 *  API Call: /api/payees/
 * 
 *  JSON Example:
 * 
 * "address": {
 *		"line1": "2500 Lake Cook Road",
 *		"line1": "Suite 250",
 *		"city": "Riverwoods",
 *		"state": "IL",
 *		"zip": "60015"
 *	}
 *
 * @author jthornton
 */
public class AddressDetail implements Serializable{

	/**Unique object identifier*/
	private static final long serialVersionUID = 4019845781800082619L;

	/**Main line of address*/
	@JsonProperty("line1")
	public String line1;

	/**city*/
	@JsonProperty("city")
	public String city;

	/**State of address*/
	@JsonProperty("state")
	public String state;

	/**Zip code of address*/
	@JsonProperty("zip")
	public String zip;
}
