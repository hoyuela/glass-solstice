package com.discover.mobile.bank.services.deposit;

import java.io.Serializable;

import com.discover.mobile.common.net.json.bank.Date;
import com.discover.mobile.common.net.json.bank.Money;
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
	    "account": 2,
	    "amount": {
	        "value": 5555,
	        "formatted": "$55.55"
	    },
	    "id": "3200000000220",
	    "creationDate": "2013-03-21T16:23:56.541+0000",
	    "confirmation": "2e90edd00dc"
	}
	
 * @author scottseward
 *
 */
public class DepositDetail implements Serializable {
	private static final long serialVersionUID = 4608587442886244788L;
	
	/**
	 * Holds the name of the amount field in a JSON request.
	 */
	public static final String AMOUNT_FIELD = "amount";
	/**
	 * Holds the name of the account field in a JSON request.
	 */
	public static final String ACCOUNT_FIELD = "account";
	
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
}
