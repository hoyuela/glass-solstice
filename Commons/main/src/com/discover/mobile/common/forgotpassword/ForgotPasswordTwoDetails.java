package com.discover.mobile.common.forgotpassword;

import java.io.Serializable;

import com.discover.mobile.common.Struct;
import com.discover.mobile.common.auth.registration.AccountInformationDetails;

@Struct
public class ForgotPasswordTwoDetails extends AccountInformationDetails implements Serializable {

	private static final long serialVersionUID = 5094796037192037185L;
	
	// TODO combine with CreateLoginDetails like the original ForgotPasswordDetails

	public String password;
	public String passwordConfirm;
	
}
