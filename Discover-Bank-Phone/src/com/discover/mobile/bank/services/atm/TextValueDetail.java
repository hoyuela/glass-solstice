/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.services.atm;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Value object received from the server
 * @author jthornton
 *
 */
public class TextValueDetail implements Serializable{

	/**Generated id*/
	private static final long serialVersionUID = -6599800813542812583L;

	/**Text value*/
	@JsonProperty("text")
	public String text;

	/**Double value*/
	@JsonProperty("value")
	public double value;

}
