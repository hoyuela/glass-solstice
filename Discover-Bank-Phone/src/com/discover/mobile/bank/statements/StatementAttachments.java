package com.discover.mobile.bank.statements;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StatementAttachments implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8709421136871337858L;

	@JsonProperty("contentType")
	public String contentType;
	
	@JsonProperty("url")
	public String url;
}
