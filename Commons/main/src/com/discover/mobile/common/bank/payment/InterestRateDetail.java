package com.discover.mobile.common.bank.payment;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * JSON object holding information about the interest rate
 * 
 * JSON Example:
 * 
 *       "interestRate": {
 *            "numerator": 6,
 *            "denominator": 100,
 *            "formatted": "0.06%"
 *        },
 * 
 * @author jthornton
 *
 */
public class InterestRateDetail implements Serializable{

	/**Unique identifier for the object*/
	private static final long serialVersionUID = 7552426393512896918L;

	/**Numerator for the interest rate*/
	@JsonProperty("numerator")
	public int numerator;

	/**Denominator for the object*/
	@JsonProperty("denominator")
	public int denominator;

	/**Formatted string of the interest rate*/
	@JsonProperty("formatted")
	public String formatted;
}
