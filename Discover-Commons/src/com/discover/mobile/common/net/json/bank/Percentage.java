package com.discover.mobile.common.net.json.bank;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class is used for storing Percentage Data Types return in Bank API JSON responses. It  
 * contains a percentage, expressed in decimal and formatted representations
 *  
 * 
 * {
 *		"value" : .0088,
 *		"formatted" : "0.88%"
 * }
 *   
 * @author henryoyuela
 *
 */
public class Percentage implements Serializable {
	/**
	 * Auto-generated serial UID which is used to serialize and de-serialize Customer objects
	 */
	private static final long serialVersionUID = 2436034484547418609L;

	//The amount expression as a decimal
	@JsonProperty("value")
	public String value;

	//The amount formatted with a percent sign and a decimal.
	@JsonProperty("formatted")
	public String formatted;
}

