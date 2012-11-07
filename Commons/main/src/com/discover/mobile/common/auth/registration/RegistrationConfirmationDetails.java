package com.discover.mobile.common.auth.registration;

import java.io.Serializable;

import com.discover.mobile.common.Struct;

@Struct
public class RegistrationConfirmationDetails extends RegistrationOneDetails implements Serializable {

	private static final long serialVersionUID = 4196814231958589918L;
	
	//Step Two Strings
	public String userId;
	public String email;
	public String acctLast4;
}
