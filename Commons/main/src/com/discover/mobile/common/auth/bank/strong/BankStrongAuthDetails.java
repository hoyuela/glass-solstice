package com.discover.mobile.common.auth.bank.strong;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BankStrongAuthDetails {

	@JsonProperty("status")
	public String status;
	
	@JsonProperty("challengeQuestionId")
	public String questionId;
	
	@JsonProperty("challengeQuestion")
	public String question;
	
}

