package com.discover.mobile.common.bank.account.activity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ActivityDetail implements Serializable{

	private static final long serialVersionUID = -180698452175670553L;

	@JsonProperty("id")
	public String id;

	@JsonProperty("description")
	public String description;

	@JsonProperty("amount")
	public String amount;

	@JsonProperty("dates")
	public ActivityDateDetail dates;

	@JsonProperty("balance")
	public int balance;

	@JsonProperty("transactionType")
	public String type;

}
