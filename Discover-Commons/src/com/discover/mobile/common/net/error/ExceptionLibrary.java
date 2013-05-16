package com.discover.mobile.common.net.error;

import java.io.IOException;

/**
 * Library of exceptions used for application
 * 
 * @author henryoyuela
 *
 */
public class ExceptionLibrary {
	private ExceptionLibrary() {
		throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
	}
	
	/**
	 * Exception thrown when missing token in a response to an authentication request.
	 * 
	 * @author henryoyuela
	 *
	 */
	public static class MissingTokenException extends IOException {
	    /**
		 * Auto generated serial UID
		 */
		private static final long serialVersionUID = -3355391501247926196L;

		public MissingTokenException() {
	        super("Invalid Token Provided");
	    }
	}
	
	/**
	 * Exception thrown when malformed message provdied in a response to a request.
	 * 
	 * @author henryoyuela
	 *
	 */
	public static class MalformedMessageException extends IOException {
	    /**
		 * Auto generated serial UID
		 */
		private static final long serialVersionUID = -3355391501247926196L;

		public MalformedMessageException() {
	        super("Malformed Response Message");
	    }
	}
}
