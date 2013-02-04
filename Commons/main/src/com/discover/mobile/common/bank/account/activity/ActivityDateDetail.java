package com.discover.mobile.common.bank.account.activity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ActivityDateDetail implements Serializable{

	private static final long serialVersionUID = 3450893104602755105L;

	@JsonProperty("date")
	public String date;

	@JsonProperty("dateClassifier")
	public String classifier;

	@JsonProperty("formattedDate")
	public String formattedDate;

}
