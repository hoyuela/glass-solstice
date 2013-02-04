package com.discover.mobile.common.bank.payment;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeliveryDateDetail implements Serializable{

	private static final long serialVersionUID = -7317708580258431225L;

	@JsonProperty("date")
	public String date;

	@JsonProperty("formattedDate")
	public String formattedDate;
}
