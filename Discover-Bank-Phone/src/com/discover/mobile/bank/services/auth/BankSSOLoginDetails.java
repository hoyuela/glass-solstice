package com.discover.mobile.bank.services.auth;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Model of the request sent to Bank services for Single Sign-On login. 
 * 
 * @author Samuel Frank Smith
 *
 */
public class BankSSOLoginDetails implements Serializable{
	/**
	 * Auto-generated serial UID which is used to serialize and de-serialize
	 * BankLoginDetails objects
	 */
	private static final long serialVersionUID = 3482967958647273037L;

	@JsonProperty("payload")
	public String payload;
}
