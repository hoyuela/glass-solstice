package com.discover.mobile.common.net.json.bank;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ID implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5886615067125174686L;
	
	@JsonProperty("id")
	public String id;
}
