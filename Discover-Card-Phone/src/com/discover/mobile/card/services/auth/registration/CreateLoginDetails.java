package com.discover.mobile.card.services.auth.registration;

import com.discover.mobile.common.Struct;

@Struct
public class CreateLoginDetails extends AccountInformationDetails {

	private static final long serialVersionUID = -8474388982680521271L;

	//Step Two Strings
	public String userId;
	public String userIdConfirm;
	public String password;
	public String passwordConfirm;
	public String email;
}
