package com.discover.mobile.common.auth;

import java.io.Serializable;

import com.discover.mobile.common.Struct;

@Struct
public class StrongAuthDetails implements Serializable {

	private static final long serialVersionUID = -8151822672320966293L;

	public String questionId;
	public String questionText;
	
}
