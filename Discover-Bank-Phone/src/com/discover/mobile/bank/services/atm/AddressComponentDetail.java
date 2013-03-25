/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.services.atm;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Object used in the GetLocationFromAddressServiceCall response.  
 * 
 * Example JSON Response:
 * {
                    "long_name": "60060",
                    "short_name": "60060",
                    "types": [
                        "postal_code"
                    ]
                }
 * 
 * @author jthornton
 *
 */
public class AddressComponentDetail implements Serializable{

	/**Generated serial id*/
	private static final long serialVersionUID = 6362732175723029892L;

	/**Long name of the address component*/
	@JsonProperty("long_name")
	public String longName;

	/**Short name of the address component*/
	@JsonProperty("short_name")
	public String short_name;

	/**Postal types of the address*/
	@JsonProperty("types")
	public List<String> types;
}
