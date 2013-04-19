package com.discover.mobile.card.services.auth;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Payload received from Card endpoint used to authenticate an SSO user on Bank.
 * @author sam
 *
 */
public class BankPayload implements Serializable {

	/** generated serial id */
	private static final long serialVersionUID = 1120551241724746805L;
	
	@JsonProperty("payloadText")
	public String payload;
}