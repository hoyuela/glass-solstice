package com.discover.mobile.bank.error;

import com.discover.mobile.common.net.error.bank.BankErrorResponse;

/**
 * Interface for handling incoming errors from Bank API Web Services. Used by 
 * BankBaseErrorResponseHandler to delegate error handling to a class object
 * implementing this interface.
 * 
 * @author henryoyuela
 *
 */
public interface BankErrorHandlerDelegate {
	public boolean handleError(final BankErrorResponse msgErrResponse);
}
