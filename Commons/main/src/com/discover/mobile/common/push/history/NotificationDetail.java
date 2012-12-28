package com.discover.mobile.common.push.history;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NotificationDetail implements Serializable{
	
	private static final long serialVersionUID = -2006076225074009399L;

	public static final String READ = "Y";
	
	public static final String UNREAD = "N";
	
	public static final String PAGE_CODE_KEY = "pageCode";
	
	public static final String ACTION_BUTTON_TEXT = "buttonText";
	
	private static final String EQUALS = "=";
	
	private static final String COMMA = ",";

	@JsonProperty("reqId")
	public String messageId;
	
	@JsonProperty("notificationText")
	public String text;
	
	@JsonProperty("msgReadInd")
	public String messageReadInd;
	
	@JsonProperty("customData")
	public String customData;
	
	@JsonProperty("subject")
	public String subject;	
	
	@JsonProperty("sentDate")
	public String sentDate;
	
	public String getPageCode(){
		final String[] array = customData.split(COMMA);
		for(String item : array){
			if(item.contains(PAGE_CODE_KEY)){
				return item.substring(item.indexOf(EQUALS)+1, item.length());
			}
		}
		return "";
	}
	
	public String getActionButtonText(){
		final String[] array = customData.split(COMMA);
		for(String item : array){
			if(item.contains(ACTION_BUTTON_TEXT)){
				return item.substring(item.indexOf(EQUALS)+1, item.length());
			}
		}
		return "";
	}
}
