package com.discover.mobile.common.auth.registration;

import java.io.Serializable;

import com.discover.mobile.common.Struct;
import com.fasterxml.jackson.annotation.JsonProperty;

@Struct
public class AccountInformationDetails implements Serializable {

	private static final long serialVersionUID = 8196921305619911757L;

	/**
	 * This or {@link #userId} should always be {@code null}, never both or neither.
	 */
	public String acctNbr;
	/**
	 * This or {@link #acctNbr} should always be {@code null}, never both or neither.
	 */
	public String userId;
	
	//Step One Strings.
	public String expirationMonth;
	public String expirationYear;
	
	//DISCOVER SPELLED 'SOCIAL SECURITY NUMBER' WRONG
	//REGISTERING A USER WILL FAIL WITH 'JSON FORMAT NOT RECOGNIZED'
	//WHEN PROPER SPELLING IS USED - OMGWTFBBQLOL...
	@JsonProperty("socialSecrityNumber")
	public String socialSecurityNumber;
	public String dateOfBirthMonth;
	public String dateOfBirthDay;
	public String dateOfBirthYear;
	
}
