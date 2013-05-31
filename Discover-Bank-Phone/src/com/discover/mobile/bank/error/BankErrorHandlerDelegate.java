package com.discover.mobile.bank.error;

import com.discover.mobile.bank.services.error.BankErrorResponse;

/**
 * Interface for handling incoming errors from Bank API Web Services. Used by 
 * BankBaseErrorResponseHandler to delegate error handling to a class object
 * implementing this interface.
 * 
 * @author henryoyuela
 *
 */
public interface BankErrorHandlerDelegate {
	/**
	 * Interface to be implemented will iterate through the BankError list in msgErrResponse
	 * to know which fields generated the inline error from the server. The delegate will need
	 * to know the name of the field in the JSON request that generated the error response 
	 * and compare the JSON field name with the name field in a BankError object.
	 * 
	 * @param msgErrResponse Error Response from Server after making a request
	 * 
	 * @return True if error was handled false otherwise.
	 */
	boolean handleError(final BankErrorResponse msgErrResponse);
}
