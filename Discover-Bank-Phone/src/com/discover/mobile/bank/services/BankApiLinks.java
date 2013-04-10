package com.discover.mobile.bank.services;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.discover.mobile.bank.services.json.ReceivedUrl;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class used to map a JSON response to a Java Object using the Bank API Service Call via BankApiServiceCall.
 * 
 * JSON Sample:
 * {
	    "links": {
	        "bank-holidays": {
	            "ref": "/api/content/bank-holidays/",
	            "allowed": [
	                "GET"
	            ]
	        },
	        "customer": {
	            "ref": "/api/customers/current",
	            "allowed": [
	                "GET"
	            ]
	        },
	        "privacy-policy": {
	            "ref": "/api/content/privacy-policy.html",
	            "allowed": [
	                "GET"
	            ]
	        },
	        "terms-of-use": {
	            "ref": "/api/content/terms-of-use.html",
	            "allowed": [
	                "GET"
	            ]
	        }
	    }
 *	}
 * @author henryoyuela
 *
 */
public class BankApiLinks implements Serializable {
	/**
	 *  Auto-generated serial UID which is used to serialize and de-serialize BankApiLinks objects
	 */
	private static final long serialVersionUID = -5637404253512037694L;
	
	@JsonProperty("links")
	public Map<String, ReceivedUrl> links = new HashMap<String, ReceivedUrl>();
	
}