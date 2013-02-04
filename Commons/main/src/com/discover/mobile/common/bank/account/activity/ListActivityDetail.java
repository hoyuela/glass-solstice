package com.discover.mobile.common.bank.account.activity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ListActivityDetail {

	@JsonProperty("transactions")
	List<ActivityDateDetail> transactions;
}
