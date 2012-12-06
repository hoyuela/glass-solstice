package com.discover.mobile.common.push.manage;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PushRemindersEnrollResultsDetail {

	@JsonProperty("cardProductGroupOutageMode")
	public boolean inOutageMode;
	
	@JsonProperty("prefTypeCodesToDisplay")
	public List<String> codesToDisplay;
}
