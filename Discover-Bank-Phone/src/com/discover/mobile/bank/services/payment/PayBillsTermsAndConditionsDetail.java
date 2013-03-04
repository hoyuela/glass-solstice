package com.discover.mobile.bank.services.payment;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Terms and conditions POJO. It is in the form of,
 * 
 * { 
 *	"title" : "Terms and Conditions",
 *	"type" : "text/html",
 *	"content" : "..."
 *	}
 * 
 * @author scottseward
 *
 */
public class PayBillsTermsAndConditionsDetail implements Serializable {
	private static final long serialVersionUID = -5023974581667384081L;

	@JsonProperty("title")
	public String title;
	
	@JsonProperty("type")
	public String type;
	
	@JsonProperty("content")
	public String content;
	
}
