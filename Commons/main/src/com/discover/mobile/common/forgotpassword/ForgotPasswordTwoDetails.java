package com.discover.mobile.common.forgotpassword;

import java.io.Serializable;

import com.discover.mobile.common.Struct;

@Struct
public class ForgotPasswordTwoDetails extends ForgotPasswordDetails implements Serializable {

	private static final long serialVersionUID = 5094796037192037185L; 

	public String password;
	public String passwordConfirm;
}
