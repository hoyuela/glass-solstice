package com.discover.mobile.bank.services.payee;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SearchPayeeResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8237003420261421202L;
	
	@JsonProperty("name")
	public String name;
	
	@JsonProperty("nickName")
	public String nickName;
	
	@JsonProperty("merchantNumber")
	public int merchangeNumber;
	
	@JsonProperty("isZipRequired")
	public boolean isZipRequired;

}
