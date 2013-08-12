package com.discover.mobile.bank.services.atm.autocomplete;

import java.io.Serializable;

import com.discover.mobile.common.utils.StringUtility;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Prediction implements Serializable {

	/**
	 * auto generated UID
	 */
	private static final long serialVersionUID = -7954924159872685017L;
	/**predictions end in {location}, United States.  This string allows it to be removed*/
	private static final String US_ENDING = ", United States";
	
	@JsonProperty("description")
	public String description;
	
	
	@Override
	public String toString() {
		return this.description.replace(US_ENDING, StringUtility.EMPTY);
	}
}
