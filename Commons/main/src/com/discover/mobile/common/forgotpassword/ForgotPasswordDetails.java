package com.discover.mobile.common.forgotpassword;

import java.io.Serializable;

import com.discover.mobile.common.Struct;
import com.fasterxml.jackson.annotation.JsonProperty;

@Struct
public class ForgotPasswordDetails implements Serializable {

	private static final long serialVersionUID = 6682647135115486736L;
	
	//Step One Strings.
	public String userId;
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
