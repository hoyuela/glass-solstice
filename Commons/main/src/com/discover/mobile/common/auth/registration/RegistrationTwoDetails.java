package com.discover.mobile.common.auth.registration;

import java.io.Serializable;

import com.discover.mobile.common.Struct;

@Struct
public class RegistrationTwoDetails extends RegistrationOneDetails implements Serializable {

	private static final long serialVersionUID = -8474388982680521271L;

	//Step Two Strings
	public String userId;
	public String userIdConfirm;
	public String password;
	public String passwordConfirm;
	public String email;
}
