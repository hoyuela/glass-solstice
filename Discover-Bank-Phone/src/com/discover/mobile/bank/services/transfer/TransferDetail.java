package com.discover.mobile.bank.services.transfer;

import java.io.Serializable;

import com.discover.mobile.bank.services.json.Money;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TransferDetail implements Serializable {

	private static final long serialVersionUID = 3220773738601798470L;

	@JsonProperty("id")
	public String id;

	@JsonProperty("fromAccount")
	public TransferEntity fromAccount;

	@JsonProperty("toAccount")
	public TransferEntity toAccount;

	@JsonProperty("amount")
	public Money amount;

	@JsonProperty("sendDate")
	public String sendDate;

	@JsonProperty("deliverBy")
	public String deliverBy;

	@JsonProperty("frequency")
	public String frequency;

	@JsonProperty("durationType")
	public String durationType;

	@JsonProperty("durationValue")
	public String durationValue;

}
