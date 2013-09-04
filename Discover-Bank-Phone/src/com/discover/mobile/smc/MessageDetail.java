package com.discover.mobile.smc;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.discover.mobile.bank.services.account.Account;
import com.discover.mobile.bank.services.json.ReceivedUrl;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageDetail implements Serializable {

	/**
	 * auto-generated UID
	 */
	private static final long serialVersionUID = -5920419776558360699L;

	/**Id of the current message*/
	@JsonProperty("id")
	public String id;
	
	/**object to hold account information	 */
	@JsonProperty("account")
	public Account account;
	
	/**Subject of the email*/
	@JsonProperty("subject")
	public String subject;
	
	/**UTC formatted string representing the date when message was created*/
	@JsonProperty("messageDate")
	public String messageDate;
	
	/**the body of the message*/
	@JsonProperty("body")
	public String body;
	
	/**states the viewed status of the message.  Can either be "opened" or...*/
	@JsonProperty("readMessageStatus")
	public String readMessageStatus;
	
	/**address of the sender*/
	@JsonProperty("fromAddress")
	public String fromAddress;
	
	/**address of the reciepient*/
	@JsonProperty("toAddress")
	public String toAddress;
	
	/**Determines whether the message is being sent or recieved*/
	@JsonProperty("incomingMessage")
	public boolean incomingMessage;
	
	/**links that provide connections to web-based services 
	 * (ex. endpoint to hit with changes to message)
	 */
	@JsonProperty("links")
	public Map<String, ReceivedUrl> links = new HashMap<String, ReceivedUrl>();
}
