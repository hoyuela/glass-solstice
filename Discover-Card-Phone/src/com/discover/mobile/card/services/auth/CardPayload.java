package com.discover.mobile.card.services.auth;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CardPayload implements Serializable {

	/** Generated serial id */
	private static final long serialVersionUID = 7923812701331072214L;

	@JsonProperty("value")
	public String value;
	
	@JsonProperty("hashedValue")
	public String hashedValue;
	
}
