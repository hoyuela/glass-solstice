package com.discover.mobile.common.bank.payment;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InterestRateDetail implements Serializable{

	private static final long serialVersionUID = 7552426393512896918L;

	@JsonProperty("numerator")
	public int numerator;

	@JsonProperty("denominator")
	public int denominator;

	@JsonProperty("formatted")
	public String formatted;
}
