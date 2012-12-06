package com.discover.mobile.common.push.manage;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PreferencesDetail implements Serializable{

	private static final long serialVersionUID = 4993285719297737090L;

	@JsonProperty("params")
	public List<PushManageCategoryParamDetail> params;
	
	@JsonProperty("categoryId")
	public String categoryId;
	
	@JsonProperty("custAccptInd")
	public String accepted;
	
	@JsonProperty("prefTypeCode")
	public String prefTypeCode;
}
