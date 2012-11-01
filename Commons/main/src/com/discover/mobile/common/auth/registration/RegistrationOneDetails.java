package com.discover.mobile.common.auth.registration;

import java.io.Serializable;

import com.discover.mobile.common.Struct;

@Struct
public class RegistrationOneDetails implements Serializable {

	private static final long serialVersionUID = 8196921305619911757L;

	//Step One Strings.
	public String acctNbr;
	public String expirationMonth;
	public String expirationYear;
	public String socialSecurityNumber;
	public String dateOfBirthMonth;
	public String dateOfBirthDay;
	public String dateOfBirthYear;
	
}
