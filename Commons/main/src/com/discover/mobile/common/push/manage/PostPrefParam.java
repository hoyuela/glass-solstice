package com.discover.mobile.common.push.manage;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PostPrefParam implements Serializable{

	private static final long serialVersionUID = 7751622603759673195L;

	public static final String AMOUNT_CODE = "AMT";

	@JsonProperty("parmCode")
	public String code;
	
	@JsonProperty("parmValue")
	public String value;
}
