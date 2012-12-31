package com.discover.mobile.common.push.manage;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Main object retrieved from the server when the the application gets the current preferences
 * @author jthornton
 *
 */
public class PushNotificationPrefsDetail implements Serializable{

	/**Unique identifier*/
	private static final long serialVersionUID = 2246280366974953623L;
	
	/**Result code of the server call*/
	@JsonProperty("resultCode")
	public String resultCode;
	
	/**Result message of the server call*/
	@JsonProperty("resultMsg")
	public String resultMsg;
	
	/**Main object of the preferences*/
	@JsonProperty("remindersEnrollResultsVO")
	public PushRemindersEnrollResultsDetail remindersEnrollResults;
}