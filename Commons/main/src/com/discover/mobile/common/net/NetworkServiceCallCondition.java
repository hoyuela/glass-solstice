package com.discover.mobile.common.net;

import java.io.Serializable;

import com.discover.mobile.common.net.error.ErrorResponse;

public abstract class  NetworkServiceCallCondition<TYPE> {
	public abstract boolean isCallable();


	public void complete(Object arg0) {
	}


	public boolean handleFailure(ErrorResponse<?> arg0) {
	
		return false;
	}


	public boolean handleFailure(Throwable arg0) {
		
		return false;
	}

	

	public void success(Serializable arg0) {
		
	}
	
}

