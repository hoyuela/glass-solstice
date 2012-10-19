package com.discover.mobile.common.net;

/**
 * Thrown when there is no active network connection available.
 */
public class ConnectionFailureException extends Exception {
	
	private static final long serialVersionUID = 2589924490563954159L;

	public ConnectionFailureException() {
		super();
	}
	
	public ConnectionFailureException(final String detailMessage) {
		super(detailMessage);
	}
	
	public ConnectionFailureException(final Throwable throwable) {
		super(throwable);
	}
	
	public ConnectionFailureException(final String detailMessage, final Throwable throwable) {
		super(detailMessage, throwable);
	}
	
}
