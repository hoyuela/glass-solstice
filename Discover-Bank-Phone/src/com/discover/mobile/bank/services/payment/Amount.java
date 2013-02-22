package com.discover.mobile.bank.services.payment;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Shared amount JSON Object
 * 
 * Sample JSON looks like:
 * 
 * "amount": {
 *		"value": 8649,
 *		"formatted": "$86.49"
 *	}
 * @author jthornton
 *
 */
public class Amount implements Serializable{

	/**Unique identifier for the class*/
	private static final long serialVersionUID = 9162772633882277785L;

	/**Actual int amount in cents*/
	@JsonProperty("value")
	public int value;

	/**Formatted amount string*/
	@JsonProperty("formatted")
	public String formatted;

}
