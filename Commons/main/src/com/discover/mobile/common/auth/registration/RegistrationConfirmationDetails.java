package com.discover.mobile.common.auth.registration;

import com.discover.mobile.common.Struct;

@Struct
public class RegistrationConfirmationDetails extends AccountInformationDetails {

	private static final long serialVersionUID = 4196814231958589918L;
	
	//Step Two Strings
	public String userId;
	public String email;
	public String acctLast4;
}
