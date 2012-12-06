package com.discover.mobile.common.push.manage;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PushNotificationPreferncesDetail implements Serializable{

	private static final long serialVersionUID = 2246280366974953623L;
	
	@JsonProperty("resultCode")
	public String resultCode;
	
	@JsonProperty("resultMsg")
	public String resultMsg;
	
	@JsonProperty("remindersEnrollResultsVO")
	public PushRemindersEnrollResultsDetail remindersEnrollResults;
	
	@JsonProperty("preferences")
	public List<PreferencesDetail> preferences;

}