package com.discover.mobile.common.push.manage;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PushManageCategoryParamDetail implements Serializable{

	private static final long serialVersionUID = 3094405120334743575L;

	@JsonProperty("paramCode")
	public String code;
	
	@JsonProperty("paramValue")
	public String value;
}
