package com.discover.mobile.common.net.error.bank;

import java.io.IOException;

/**
 * Library of exceptions used for Bank services
 * 
 * @author henryoyuela
 *
 */
public class BankExceptions {
	private BankExceptions() {
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

		public MissingTokenException(String message) {
	        super(message);
	    }
	}
}
