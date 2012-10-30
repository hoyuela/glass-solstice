package com.discover.mobile.common.auth;

import java.io.Serializable;

public class RegistrationDetails implements Serializable{

	private static final long serialVersionUID = 8196921305619911757L;

	//Step One Strings.
	public String acctNbr, 
	expirationMonth, 
	expirationYear, 
	dateOfBirthMonth, 
	dateOfBirthDay, 
	socialSecurityNumber, 
	dateOfBirthYear;
	
	//Step Two Strings
	public String userId,
	userIdConfirm,
	password,
	passwordConfirm,
	email;
}
