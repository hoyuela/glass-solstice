package com.discover.mobile.common.account.recent;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RecentActivityPeriodDetail implements Serializable{

	private static final long serialVersionUID = -3745478544230050226L;
	
	@JsonProperty("stmtDate")
	public String date;
	
	@JsonProperty("displayDate")
	public String displayDate;

}
