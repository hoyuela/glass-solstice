package com.discover.mobile.common.bank.payment;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.discover.mobile.common.net.json.bank.ReceivedUrl;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentAccountDetail implements Serializable{

	@JsonProperty("ending")
	public String ending;

	@JsonProperty("id")
	public int id;

	@JsonProperty("name")
	public String name;

	@JsonProperty("nickname")
	public String nickName;

	@JsonProperty("type")
	public String type;

	@JsonProperty("balance")
	public int balance;

	@JsonProperty("interestRate")
	public InterestRateDetail interest;

	@JsonProperty("interestEarnedLastStatement")
	public int interestEarned;

	@JsonProperty("interestYearToDate")
	public int interestToDate;

	@JsonProperty("openDate")
	public int openDate;

	@JsonProperty("status")
	public String status;

	/**
	 * Contains Bank web-service API Resource links
	 */
	@JsonProperty("links")
	public Map<String, ReceivedUrl> links = new HashMap<String, ReceivedUrl>();
}
