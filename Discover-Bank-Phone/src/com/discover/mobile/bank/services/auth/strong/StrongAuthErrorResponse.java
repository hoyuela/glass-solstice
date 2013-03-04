package com.discover.mobile.bank.services.auth.strong;

import com.discover.mobile.common.net.error.AbstractErrorResponse;
import com.discover.mobile.common.net.error.ErrorMessageMapper;

public class StrongAuthErrorResponse extends AbstractErrorResponse<StrongAuthErrorResponse> {
	
	private static final long serialVersionUID = 4730608608826595010L;
	
	// TODO strong auth result
	
	// TEMP
	private final String result;
	
	StrongAuthErrorResponse(String result) {
		this.result = result;
	}
	
	public String getResult() {
		return result;
	}

	@Override
	public ErrorMessageMapper<StrongAuthErrorResponse> getMessageMapper() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
