package com.discover.mobile.common.account.recent;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionDetail implements Serializable {

	private static final long serialVersionUID = 3597259705149565786L;
	
	@JsonProperty("txnAmt")
	public String amount;
	
	@JsonProperty("txnDate")
	public String date;
	
	@JsonProperty("txnDesc")
	public String description;
	
	@JsonProperty("txnPostDt")
	public String postedDate;
	
	@JsonProperty("txnCtg")
	public String category;
	
	@JsonProperty("merchantId")
	public String merchantId;
}
