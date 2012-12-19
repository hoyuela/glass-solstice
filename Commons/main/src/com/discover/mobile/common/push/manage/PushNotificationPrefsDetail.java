package com.discover.mobile.common.push.manage;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PushNotificationPrefsDetail implements Serializable{

	private static final long serialVersionUID = 2246280366974953623L;
	
	@JsonProperty("resultCode")
	public String resultCode;
	
	@JsonProperty("resultMsg")
	public String resultMsg;
	
	@JsonProperty("remindersEnrollResultsVO")
	public PushRemindersEnrollResultsDetail remindersEnrollResults;
}