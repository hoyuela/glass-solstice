package com.discover.mobile.common.auth.bank.strong;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BankStrongAuthAnswerDetails {

	@JsonProperty("challengeQuestionId")
	public String questionId;
	
	@JsonProperty("challengeQuestionResponse")
	public String question;
	
}

