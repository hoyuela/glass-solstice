package com.discover.mobile.bank.services.account;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/*
 *	{
 *	    "payments": {
 *	        "id": "SM11946",
 *	        "accountNumber": {
 *	            "ending": "1066",
 *	            "formatted": "****1066"
 *	        },
 *	        "nickName": "RETAIL 55+CHECKING ",
 *	        "links": {
 *	            "self": {
 *	                "allowed": [
 *	                    "GET"
 *	                ],
 *	                "ref": "/api/accounts/SM11946/"
 *	            },
 *	            "postedActivity": {
 *	                "allowed": [
 *	                    "GET"
 *	                ],
 *	                "ref": "/api/accounts/SM11946/activity?view=posted"
 *	            },
 *	            "statements": {
 *	                "allowed": [
 *	                    "GET"
 * 	                ],
 *	                "ref": "/api/accounts/SM11946/statements"
 *	            },
 *	            "scheduledActivity": {
 *	                "allowed": [
 *	                    "GET"
 *	                ],
 *	                "ref": "/api/accounts/SM11946/activity?view=scheduled"
 *	            }
 *	        }
 *	    }
 *	}
 *
 */

public class PreferredAccounts implements Serializable{

	/**
	 * Auto-generated serial UID which is used to serialize and de-serialize PreferredAccountList objects
	 */
	private static final long	serialVersionUID	= -395062391901053818L;
	
	@JsonProperty("payments")
	public Account payments;
	
	@JsonProperty("transfers")
	public Account deposits;
	
	@JsonProperty("deposits")
	public Account transfers;
}
