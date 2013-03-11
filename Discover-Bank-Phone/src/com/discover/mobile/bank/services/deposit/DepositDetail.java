package com.discover.mobile.bank.services.deposit;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.discover.mobile.common.net.json.bank.Date;
import com.discover.mobile.common.net.json.bank.Money;
import com.discover.mobile.common.net.json.bank.ReceivedUrl;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The deposit object, used for sending and receiving deposit objects.
 * 
 * To deposit a check a POST request is made with the following data in a DepositDetail object to /api/deposits 
 * {
		account: 2,
		amount: { value: 25000 },
		frontImage: "/9j/4AAQSkZJRgABAgAAAQA...",
		backImage: "/9j/4AAQSkZJRgABAgAAAQAB..."
	}
	
 * 	A successful response will be in the following form with a 201 Created http response. 
	{
		id: 1552,
		account: 2,
		amount: { value: 25000 },
		confirmation: 2e90edd0384,
		scheduledDate: "2013-01-28T00:00:00Z",
		links: {
			"self" : {
				"ref" : "https://www.discoverbank.com/api/deposits/1552",
				"allowed" : [ "GET" ]
			}
		}
	}
	
 * @author scottseward
 *
 */
public class DepositDetail implements Serializable {
	private static final long serialVersionUID = 4608587442886244788L;

	@JsonProperty("id")
	public int id;
	
	@JsonProperty("amount")
	public Money amount;
	
	@JsonProperty("account")
	public int account;
	
	@JsonProperty("confirmation")
	public String confirmation;
	
	@JsonProperty("scheduledDate")
	public Date scheduledDate;
	
	/** A Base64 encoded image*/
	@JsonProperty("frontImage")
	public String frontImage;
	
	/** A Base64 encoded image*/
	@JsonProperty("backImage")
	public String backImage;
	
	/**List of links for for this object*/
	public Map<String, ReceivedUrl> links = new HashMap<String, ReceivedUrl>();
}
