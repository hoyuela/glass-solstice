package com.discover.mobile.common.bank.payee;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AddressDetail implements Serializable{

	private static final long serialVersionUID = 4019845781800082619L;

	@JsonProperty("line1")
	public String line1;

	@JsonProperty("city")
	public String city;

	@JsonProperty("state")
	public String state;

	@JsonProperty("zip")
	public String zip;
}
