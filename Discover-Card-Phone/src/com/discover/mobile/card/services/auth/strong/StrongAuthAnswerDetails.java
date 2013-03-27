package com.discover.mobile.card.services.auth.strong;

import java.io.Serializable;

import com.discover.mobile.common.Struct;

@Struct
public class StrongAuthAnswerDetails implements Serializable {
	
	private static final long serialVersionUID = -1213596129111178929L;

	public String questionId;
	public String questionAnswer;
	public String bindDevice;
	public String sid;
	public String did;
	public String oid;
	
}
