package com.discover.mobile.smc;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import com.discover.mobile.bank.services.account.Account;
import com.discover.mobile.bank.services.json.ReceivedUrl;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJO that represent one item in 
 * the list of available messages
 * 
 * @author juliandale
 *
 */
public class MessageListItem implements Serializable {
	
	/**
	 * auto-generated UID
	 */
	private static final long serialVersionUID = 8831726419764354855L;

	/**String used for comparison to determine whether message has been opened or not*/
	public static final String OPENED = "opened";
	
	
	/**id of the message - used for retrieving the details*/
	@JsonProperty("id")
	public String id;
	
	/**Account object to capture the account info and nickname returned by server*/
	@JsonProperty("account")
	public Account account;
	
	/**numerical value (rep as string) of the subject*/
	@JsonProperty("subject")
	public String subject;
	
	/**UTC formatted string representing date message was sent*/
	@JsonProperty("messageDate")
	public String messageDate;
	
	/**Shows whether or not the message was previously read by user*/
	@JsonProperty("readMessageStatus")
	public String readMessageStatus;
	
	/**Incoming message flag, not sure of its utility at this point (8/27)*/
	@JsonProperty("incomingMessage")
	public boolean incomingMessage;
	
	/**Map that represent links to web-based services*/
	@JsonProperty("links")
	public Map<String, ReceivedUrl> links = new HashMap<String, ReceivedUrl>();

	
}
