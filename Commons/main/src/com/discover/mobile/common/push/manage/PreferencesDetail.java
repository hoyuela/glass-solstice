package com.discover.mobile.common.push.manage;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PreferencesDetail implements Serializable{
	
	public static final String PENDING = "P";
	
	public static final String ACCEPTED = "Y";
	
	public static final String TEXT_PARAM = "SMRM";
	
	public static final String PUSH_PARAM = "PNRM";

	private static final long serialVersionUID = 4993285719297737090L;

	@JsonProperty("params")
	public List<PostPrefParam> params;
	
	@JsonProperty("categoryId")
	public String categoryId;
	
	@JsonProperty("custAccptInd")
	public String accepted;
	
	@JsonProperty("prefTypeCode")
	public String prefTypeCode;
}
