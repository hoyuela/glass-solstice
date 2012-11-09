package com.discover.mobile.common.net.error;

import java.io.Serializable;

public interface ErrorResponse<E extends ErrorResponse<E>> extends Serializable {
	
	int getHttpStatusCode();
	
	ErrorMessageMapper<E> getMessageMapper();
	
}
