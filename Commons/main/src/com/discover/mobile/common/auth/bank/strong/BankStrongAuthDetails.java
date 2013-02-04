package com.discover.mobile.common.auth.bank.strong;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Used for mapping the JSON returned from the strong auth request. 
 * 
 * Json Format:
 * {
 * "status":"SKIPPED",
 *  "challengeQuestionId":"Q3.2",
 *  "challengeQuestion":"What is your mother's middle name?",
 *  "bindDevice":true,
 *  "sessionId":"4e3d1119:13b6d7e3c45:-1f78",
 *  "transactionId":"TRX_4e3D1119:13b6d7e3c45:-1f77",
 *  "links":{
 *     "self":{
 *        "ref":"https://www.discoverbank.com/api/auth/strongauth",
 *        "allowed":[
 *           "GET",
 *           "POST"
 *        ]
 *     }
 *  }
 *}
 * @author ajleeds
 *
 */
public class BankStrongAuthDetails implements Serializable {
	/**
	 * Auto-generated serial UID which is used to serialize and de-serialize BankStrongAuthDetails objects
	 */
	private static final long serialVersionUID = -2034266378644868213L;

	/**
	 * Used to determine whether Strong Auth passed
	 */
	public static final String ALLOW_STATUS="ALLOW";

	@JsonProperty("status")
	public String status;
	
	@JsonProperty("challengeQuestionId")
	public String questionId;
	
	@JsonProperty("challengeQuestion")
	public String question;
	
	@JsonProperty("sessionId")
	public String sessionId;
	
	@JsonProperty("transactionId")
	public String transactionId;
	
}

