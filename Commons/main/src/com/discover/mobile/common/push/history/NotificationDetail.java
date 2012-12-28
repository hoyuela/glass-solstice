package com.discover.mobile.common.push.history;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NotificationDetail implements Serializable{
	
	private static final long serialVersionUID = -2006076225074009399L;

	public static final String READ = "Y";
	
	public static final String UNREAD = "N";

	@JsonProperty("msgID")
	public static String messageId;
	
	@JsonProperty("notificationText")
	public static String text;
	
	@JsonProperty("msgReadInd")
	public static String messageReadInd;
	
	@JsonProperty("customData")
	public static String customData;
	
	@JsonProperty("subject")
	public static String subject;	
}
