package com.discover.mobile.common.auth.bank.strong;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class is used for sending the Json with the Strong auth answer. 
 * 
 *{
 *	"challengeQuestionId" : "Q3.2",
 *	"challengeQuestionResponse" : "Gertrude"
 *	"bindDevice": true,
 *	"sessionId": "4e3d1119:13b6d7e3c45:-1f78",
 *	"transactionId": "TRX_4e3D1119:13b6d7e3c45:-1f77"
 *}
 * 
 * @author ajleeds
 *
 */
public class BankStrongAuthAnswerDetails {

	@JsonProperty("challengeQuestionId")
	public String questionId;
	
	@JsonProperty("challengeQuestionResponse")
	public String questionResponse;
	
	@JsonProperty("bindDevice")
	public String bindDevice;
	
	@JsonProperty("sessionId")
	public String sessionId;
	
	@JsonProperty("transactionId")
	public String transactionId;
	
	/**
	 * Default Constructor
	 */
	public BankStrongAuthAnswerDetails() {
		
	}
	
	/**
	 * Constructor used to build a BankStrongAuthAnswerDetails from a BankStrongAuthDetails where it copies
	 * the fields used to identify the challenge request associated with the response on the server side.
	 * 
	 * @param strongAuthDetails Holds reference to a BankStrongAuthDetails object provided in a 200 OK response to 
	 * a NetworkServiceCall CreateStrongAuthRequestCall or a 401 for the same.
	 * 
	 * @param answer Holds a reference to a String object that contains the answer to the question in the 
	 * BankStrongAuthDetails object.
	 * 
	 * @param bind Holds a boolean whether the device should be remembered or not on the server
	 */
	public BankStrongAuthAnswerDetails(final BankStrongAuthDetails strongAuthDetails, final String answer, final boolean bind) {
		questionResponse = answer;
		bindDevice = (bind)?"true":"false";
		questionId = strongAuthDetails.questionId;
		sessionId = strongAuthDetails.sessionId;
		transactionId = strongAuthDetails.transactionId;
	}
}

