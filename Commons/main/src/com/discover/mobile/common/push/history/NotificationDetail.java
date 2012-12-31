package com.discover.mobile.common.push.history;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Details about a specific notification
 * @author jthornton
 *
 */
public class NotificationDetail implements Serializable{
	
	/**Generated unique identifier*/
	private static final long serialVersionUID = -2006076225074009399L;

	/**Value representing the notification has been read*/
	public static final String READ = "Y";
	
	/**Value representing the notification has not been read*/
	public static final String UNREAD = "N";
	
	/**Value representing the key to get the page code*/
	public static final String PAGE_CODE_KEY = "pageCode";
	
	/**Value representing the key to get the action button text*/
	public static final String ACTION_BUTTON_TEXT = "buttonText";
	
	/**Value for an equals sign*/
	private static final String EQUALS = "=";
	
	/**Value for a comma sign*/
	private static final String COMMA = ",";

	/**Id of the message*/
	@JsonProperty("reqId")
	public String messageId;
	
	/**Text of the notification*/
	@JsonProperty("notificationText")
	public String text;
	
	/**Indicator of whether or not the notification has been read*/
	@JsonProperty("msgReadInd")
	public String messageReadInd;
	
	/**Custom data string*/
	@JsonProperty("customData")
	public String customData;
	
	/**Subject of the notification*/
	@JsonProperty("subject")
	public String subject;	
	
	/**Date the notification was sent*/
	@JsonProperty("sentDate")
	public String sentDate;
	
	/**
	 * Get the page code from the other data
	 * @return the page code from the other data
	 */
	public String getPageCode(){
		final String[] array = customData.split(COMMA);
		for(String item : array){
			if(item.contains(PAGE_CODE_KEY)){
				return item.substring(item.indexOf(EQUALS)+1, item.length());
			}
		}
		return "";
	}
	
	/**
	 * Get the action button text from the other data
	 * @return get the action button text from the other data
	 */
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
