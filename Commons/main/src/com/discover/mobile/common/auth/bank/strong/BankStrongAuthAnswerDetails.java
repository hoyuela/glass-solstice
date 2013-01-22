package com.discover.mobile.common.auth.bank.strong;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class is used for sending the Json with the Strong auth answer. 
 * 
 * @author ajleeds
 *
 */
public class BankStrongAuthAnswerDetails {

	@JsonProperty("challengeQuestionId")
	public String questionId;
	
	@JsonProperty("challengeQuestionResponse")
	public String question;
	
}

