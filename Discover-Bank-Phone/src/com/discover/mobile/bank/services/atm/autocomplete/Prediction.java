package com.discover.mobile.bank.services.atm.autocomplete;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Prediction implements Serializable {

	/**
	 * auto generated UID
	 */
	private static final long serialVersionUID = -7954924159872685017L;

	@JsonProperty("description")
	public String description;
	
	
	@Override
	public String toString() {
		return this.description;
	}
}
