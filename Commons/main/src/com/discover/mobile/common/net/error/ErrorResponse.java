package com.discover.mobile.common.net.error;

import java.io.Serializable;
import java.net.HttpURLConnection;

public interface ErrorResponse<E extends ErrorResponse<E>> extends Serializable {
	
	int getHttpStatusCode();
	
	ErrorMessageMapper<E> getMessageMapper();
	
	/**
	 * 
	 * @param connection  Interface to set a Reference to the HttpURLConnection object used to receive error response
	 */
	void setConnection(HttpURLConnection connection );
	/**
	 * 
	 * @return Interface defined to Return a reference to the HttpURLConnection object used to receive error response
	 */
	public HttpURLConnection getConnection();
	
}
